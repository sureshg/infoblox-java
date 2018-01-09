package com.oneops.infoblox;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BASIC;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.model.Error;
import com.oneops.infoblox.model.JsonAdapterFactory;
import com.oneops.infoblox.model.Redacted;
import com.oneops.infoblox.model.zone.ZoneAuth;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * A Client for interacting with Infoblox Appliance (IBA) NIOS over WAPI.
 * This client implements the subset of Infoblox API.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class InfobloxClient {

  private Logger log = Logger.getLogger(getClass().getSimpleName());

  /**
   * IBA IP address of management interface
   */
  public abstract String endPoint();

  /**
   * IBA WAPI version. Browse to <a href="https://{infoblox}/wapidoc/">WapiDoc</a>
   * to see the current wapi version of Infoblox appliance. Defaults to 2.5
   */
  public abstract String wapiVersion();

  /**
   * IBA user name
   */
  @Redacted
  public abstract String userName();

  /**
   * IBA user password
   */
  @Redacted
  public abstract String password();

  /**
   * IBA default view. Defaults to 'default`.
   */
  public abstract String dnsView();

  /**
   * Checks if TLS certificate validation is enabled for communicating with Infoblox.
   */
  public abstract boolean tlsVerify();

  /**
   * IBA WAPI connection/read/write timeout.
   */
  public abstract int timeout();

  private Infoblox infoblox;

  private Converter<ResponseBody, Error> errResConverter;

  /**
   * Initializes the TLS retrofit client.
   *
   * @throws GeneralSecurityException if any error initializing the TLS context.
   */
  private void init() throws GeneralSecurityException {
    log.info("Initializing " + toString());
    Moshi moshi = new Moshi.Builder()
        .add(JsonAdapterFactory.create())
        .build();

    HttpLoggingInterceptor logIntcp = new HttpLoggingInterceptor(s -> log.info(s));
    logIntcp.setLevel(BASIC);

    TrustManager[] trustManagers = getTrustManagers();
    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
    sslContext.init(null, trustManagers, new SecureRandom());
    SSLSocketFactory socketFactory = sslContext.getSocketFactory();

    String basicCreds = Credentials.basic(userName(), password());
    OkHttpClient.Builder okBuilder = new OkHttpClient().newBuilder()
        .sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0])
        .connectionSpecs(singletonList(ConnectionSpec.MODERN_TLS))
        .followSslRedirects(false)
        .retryOnConnectionFailure(false)
        .connectTimeout(timeout(), SECONDS)
        .readTimeout(timeout(), SECONDS)
        .writeTimeout(timeout(), SECONDS)
        .addNetworkInterceptor(logIntcp)
        .addInterceptor(chain -> {
          HttpUrl origUrl = chain.request().url();
          HttpUrl url = origUrl.newBuilder()
              //.addQueryParameter("_max_results","1")
              .addQueryParameter("_return_as_object", "1")
              .build();
          Request req = chain.request().newBuilder()
              .addHeader("Content-Type", "application/json")
              .addHeader("Authorization", basicCreds)
              .url(url)
              .build();
          return chain.proceed(req);
        });

    if (!tlsVerify()) {
      okBuilder.hostnameVerifier((host, session) -> true);
    }
    OkHttpClient okHttp = okBuilder.build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(getBaseUrl())
        .client(okHttp)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build();

    infoblox = retrofit.create(Infoblox.class);
    errResConverter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);
  }

  /**
   * Returns the trust-store manager. It's uses jdk trust-store if
   * {@link #tlsVerify()} is enabled and can customize using the
   * following environment variables.
   *
   * - javax.net.ssl.trustStore - Default trust-store ,
   * - javax.net.ssl.trustStoreType - Default trust-store type,
   * - javax.net.ssl.trustStorePassword - Default trust-store password
   *
   * If the {@link #tlsVerify()} is disabled, it trusts all certs using
   * a custom trust manager.
   *
   * @return trust managers.
   * @throws GeneralSecurityException if any error initializing trust store.
   */
  private TrustManager[] getTrustManagers() throws GeneralSecurityException {

    final TrustManager[] trustMgrs;
    if (tlsVerify()) {
      log.info("Using JDK trust-store for TLS check.");
      TrustManagerFactory trustManagerFactory = TrustManagerFactory
          .getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null); // Uses JDK trust-store.
      trustMgrs = trustManagerFactory.getTrustManagers();
    } else {
      log.info("Skipping TLS certs verification.");
      trustMgrs = new TrustManager[]{
          new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return new java.security.cert.X509Certificate[]{};
            }
          }
      };
    }
    return trustMgrs;
  }

  /**
   * Helper method to handle {@link Call} object and return the execution result(s).
   * The error handling is done as per the response content-type.
   *
   * @see <a href="https://ipam.illinois.edu/wapidoc/#error-handling">WAPI error-handling</a>
   */
  private <T> T exec(Call<T> call) throws IOException {
    Response<T> res = call.execute();
    if (res.isSuccessful()) {
      return res.body();
    } else {
      Error err;
      String contentType = res.headers().get("Content-Type");
      if (contentType != null && contentType.equalsIgnoreCase("application/json")) {
        err = errResConverter.convert(Objects.requireNonNull(res.errorBody()));
      } else {
        err = Error.create("Request failed, " + res.message(), res.code());
      }
      throw err.cause();
    }
  }

  /**
   * Returns infoblox WAPI base url for given version.
   *
   * @return WAPI base url.
   */
  private String getBaseUrl() {
    StringBuilder buf = new StringBuilder();
    if (!endPoint().toLowerCase().startsWith("http")) {
      buf.append("https://");
    }
    return buf.append(endPoint())
        .append("/wapi/v")
        .append(wapiVersion())
        .append("/")
        .toString();
  }

  /**
   * Fetch all Authoritative Zones.
   *
   * @return list of {@link ZoneAuth}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ZoneAuth> getAuthZones() throws IOException {
    return exec(infoblox.getAuthZones()).result();
  }

  /**
   * Search all authoritative zones for the given fqdn regex.
   *
   * @param fqdn regex pattern.
   * @return list of {@link ZoneAuth}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ZoneAuth> getAuthZones(String fqdn) throws IOException {
    return exec(infoblox.getAuthZone(fqdn)).result();
  }

  /**
   * Returns the builder for {@link InfobloxClient} with
   * default values for un-initialized optional fields.
   *
   * @return Builder
   */
  public static Builder builder() {
    return new AutoValue_InfobloxClient.Builder()
        .tlsVerify(false)
        .timeout(10)
        .wapiVersion("2.5")
        .dnsView("default");
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder endPoint(String endPoint);

    public abstract Builder wapiVersion(String wapiVersion);

    public abstract Builder userName(String userName);

    public abstract Builder password(String password);

    public abstract Builder dnsView(String dnsView);

    public abstract Builder tlsVerify(boolean tlsVerify);

    public abstract Builder timeout(int timeout);

    abstract InfobloxClient autoBuild();

    /**
     * Build and initialize Infoblox client.
     *
     * @return client.
     */
    public InfobloxClient build() {
      InfobloxClient client = autoBuild();
      try {
        client.init();
      } catch (GeneralSecurityException ex) {
        throw new IllegalArgumentException("InfobloxClient init failed.", ex);
      }
      return client;
    }
  }
}
