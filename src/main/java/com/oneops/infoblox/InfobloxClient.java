package com.oneops.infoblox;

import static com.oneops.infoblox.util.IPAddrs.requireIPv4;
import static com.oneops.infoblox.util.IPAddrs.requireIPv6;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.curl.CurlLoggingInterceptor;
import com.oneops.infoblox.model.Error;
import com.oneops.infoblox.model.JsonAdapterFactory;
import com.oneops.infoblox.model.Redacted;
import com.oneops.infoblox.model.SearchModifier;
import com.oneops.infoblox.model.a.ARec;
import com.oneops.infoblox.model.aaaa.AAAA;
import com.oneops.infoblox.model.host.Host;
import com.oneops.infoblox.model.host.HostIPv4Req;
import com.oneops.infoblox.model.host.HostReq;
import com.oneops.infoblox.model.zone.ZoneAuth;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * A Client for interacting with Infoblox Appliance (IBA) NIOS over WAPI. This client implements the
 * subset of Infoblox API.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class InfobloxClient {

  private Logger log = Logger.getLogger(getClass().getSimpleName());

  /** IBA IP address of management interface */
  public abstract String endPoint();

  /**
   * IBA WAPI version. Browse to <a href="https://{infoblox}/wapidoc/">WapiDoc</a> to see the
   * current wapi version of Infoblox appliance. Defaults to 2.5
   */
  public abstract String wapiVersion();

  /** IBA user name */
  @Redacted
  public abstract String userName();

  /** IBA user password */
  @Redacted
  public abstract String password();

  /** IBA default view. Defaults to 'default`. */
  public abstract String dnsView();

  /** Checks if TLS certificate validation is enabled for communicating with Infoblox. */
  public abstract boolean tlsVerify();

  /** Enable http curl logging for debugging. */
  public abstract boolean debug();

  /** IBA WAPI connection/read/write timeout. */
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
    Moshi moshi = new Moshi.Builder().add(JsonAdapterFactory.create()).build();

    TrustManager[] trustManagers = getTrustManagers();
    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
    sslContext.init(null, trustManagers, new SecureRandom());
    SSLSocketFactory socketFactory = sslContext.getSocketFactory();

    String basicCreds = Credentials.basic(userName(), password());
    OkHttpClient.Builder okBuilder =
        new OkHttpClient()
            .newBuilder()
            .sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0])
            .connectionSpecs(singletonList(ConnectionSpec.MODERN_TLS))
            .followSslRedirects(false)
            .retryOnConnectionFailure(false)
            .connectTimeout(timeout(), SECONDS)
            .readTimeout(timeout(), SECONDS)
            .writeTimeout(timeout(), SECONDS)
            .addInterceptor(
                chain -> {
                  HttpUrl origUrl = chain.request().url();
                  HttpUrl url =
                      origUrl
                          .newBuilder()
                          // .addQueryParameter("_max_results","1")
                          .addQueryParameter("_return_as_object", "1")
                          .build();
                  Request req =
                      chain
                          .request()
                          .newBuilder()
                          .addHeader("Content-Type", "application/json")
                          .addHeader("Authorization", basicCreds)
                          .url(url)
                          .build();
                  return chain.proceed(req);
                });

    if (!tlsVerify()) {
      okBuilder.hostnameVerifier((host, session) -> true);
    }

    if (debug()) {
      CurlLoggingInterceptor logIntcp = new CurlLoggingInterceptor(s -> log.info(s));
      okBuilder.addNetworkInterceptor(logIntcp);
    }
    OkHttpClient okHttp = okBuilder.build();

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build();

    infoblox = retrofit.create(Infoblox.class);
    errResConverter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);
  }

  /**
   * Returns the trust-store manager. It's uses jdk trust-store if {@link #tlsVerify()} is enabled
   * and can customize using the following environment variables.
   *
   * <p>- javax.net.ssl.trustStore - Default trust-store , - javax.net.ssl.trustStoreType - Default
   * trust-store type, - javax.net.ssl.trustStorePassword - Default trust-store password
   *
   * <p>If the {@link #tlsVerify()} is disabled, it trusts all certs using a custom trust manager.
   *
   * @return trust managers.
   * @throws GeneralSecurityException if any error initializing trust store.
   */
  private TrustManager[] getTrustManagers() throws GeneralSecurityException {

    final TrustManager[] trustMgrs;
    if (tlsVerify()) {
      log.info("Using JDK trust-store for TLS check.");
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null); // Uses JDK trust-store.
      trustMgrs = trustManagerFactory.getTrustManagers();
    } else {
      log.info("Skipping TLS certs verification.");
      trustMgrs =
          new TrustManager[] {
            new X509TrustManager() {
              @Override
              public void checkClientTrusted(
                  java.security.cert.X509Certificate[] chain, String authType) {}

              @Override
              public void checkServerTrusted(
                  java.security.cert.X509Certificate[] chain, String authType) {}

              @Override
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
              }
            }
          };
    }
    return trustMgrs;
  }

  /**
   * Helper method to handle {@link Call} object and return the execution result(s). The error
   * handling is done as per the response content-type.
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
      if (contentType != null && "application/json".equalsIgnoreCase(contentType)) {
        err = errResConverter.convert(requireNonNull(res.errorBody()));
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
    return buf.append(endPoint()).append("/wapi/v").append(wapiVersion()).append("/").toString();
  }

  ////// Auth Zone Record //////

  /**
   * Fetch all Authoritative Zones.
   *
   * @return list of {@link ZoneAuth}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ZoneAuth> getAuthZones() throws IOException {
    return exec(infoblox.queryAuthZones()).result();
  }

  /**
   * Fetch all authoritative zones for the given domain name and search option.
   *
   * @param domainName fqdn.
   * @param modifier {@link SearchModifier}
   * @return list of {@link ZoneAuth}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ZoneAuth> getAuthZones(String domainName, SearchModifier modifier)
      throws IOException {
    requireNonNull(domainName, "Domain name is null");
    Map<String, String> options = new HashMap<>(1);
    options.put("fqdn" + modifier.getValue(), domainName);
    return exec(infoblox.queryAuthZone(options)).result();
  }

  /**
   * Search all authoritative zones for the given domain name.
   *
   * @param domainName fqdn.
   * @return list of {@link ZoneAuth}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ZoneAuth> getAuthZones(String domainName) throws IOException {
    return getAuthZones(domainName, SearchModifier.NONE);
  }

  ////// Host Record //////

  /**
   * Get host information for the given domain name and search option.
   *
   * @param domainName fqdn
   * @return list of matching {@link Host}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<Host> getHostRec(String domainName, SearchModifier modifier) throws IOException {
    requireNonNull(domainName, "Domain name is null");
    Map<String, String> options = new HashMap<>(1);
    options.put("name" + modifier.getValue(), domainName);
    return exec(infoblox.queryHostRec(options)).result();
  }

  /**
   * Get host information for the given domain name.
   *
   * @param domainName fqdn
   * @return list of matching {@link Host}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<Host> getHostRec(String domainName) throws IOException {
    return getHostRec(domainName, SearchModifier.NONE);
  }

  /**
   * Creates IBA host record.
   *
   * @param domainName hostname in FQDN
   * @param ipv4Addrs IPv4 address(s)
   * @return {@link Host} containing IPv4 addresses for the hostname.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public Host createHostRec(String domainName, List<String> ipv4Addrs) throws IOException {
    List<HostIPv4Req> reqList =
        ipv4Addrs.stream().map(HostIPv4Req::create).collect(Collectors.toList());
    HostReq hostReq = HostReq.create(domainName, reqList);
    return exec(infoblox.createHostRec(hostReq)).result();
  }

  /**
   * Deletes IBA host record with given domain name.
   *
   * @param domainName fqdn for the host record.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public void deleteHostRec(String domainName) throws IOException {
    for (Host host : getHostRec(domainName)) {
      String hostRef = host.ref();
      if (!hostRef.contains(domainName)) {
        throw new IllegalStateException("Received unexpected host reference: " + hostRef);
      }

      String resRef = exec(infoblox.deleteRef(hostRef)).result();
      log.warning("Deleting host res ref: " + resRef);
    }
  }

  ////// A Record //////

  /**
   * Get address records (A Record) for the given domain name and search option.
   *
   * @param domainName fqdn
   * @return list of matching {@link ARec}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ARec> getARec(String domainName, SearchModifier modifier) throws IOException {
    requireNonNull(domainName, "Domain name is null");
    Map<String, String> options = new HashMap<>(1);
    options.put("name" + modifier.getValue(), domainName);
    return exec(infoblox.queryARec(options)).result();
  }

  /**
   * Get address records (A Record) for the given domain name.
   *
   * @param domainName fqdn
   * @return list of matching {@link ARec}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ARec> getARec(String domainName) throws IOException {
    return getARec(domainName, SearchModifier.NONE);
  }

  /**
   * Creates an address record (A Record)
   *
   * @param domainName FQDN
   * @param ipv4Address IPv4 address
   * @return {@link ARec} address record.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public ARec createARec(String domainName, String ipv4Address) throws IOException {
    requireIPv4(ipv4Address);
    Map<String, String> req = new HashMap<>(2);
    req.put("name", domainName);
    req.put("ipv4addr", ipv4Address);
    return exec(infoblox.createARec(req)).result();
  }

  /**
   * Deletes address record with given domain name.
   *
   * @param domainName fqdn for the A record.
   * @return list of A record obj references deleted.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<String> deleteARec(String domainName) throws IOException {
    return getARec(domainName)
        .stream()
        .map(ARec::ref)
        .filter(ref -> ref.contains(domainName))
        .map(
            ref -> {
              try {
                return exec(infoblox.deleteRef(ref)).result();
              } catch (IOException ioe) {
                throw new IllegalStateException("Error deleting A record ref: " + ref, ioe);
              }
            })
        .collect(Collectors.toList());
  }

  /**
   * Modify the domain name of A record with given name.
   *
   * @param domainName fqdn for the A record.
   * @param newDomainName new fqdn.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<ARec> modifyARec(String domainName, String newDomainName) throws IOException {
    return getARec(domainName)
        .stream()
        .map(ARec::ref)
        .filter(ref -> ref.contains(domainName))
        .map(
            ref -> {
              Map<String, String> req = new HashMap<>(1);
              req.put("name", newDomainName);
              try {
                return exec(infoblox.modifyARec(ref, req)).result();
              } catch (IOException ioe) {
                throw new IllegalStateException("Error modifying A record ref: " + ref, ioe);
              }
            })
        .collect(Collectors.toList());
  }

  ////// AAAA Record //////
  /**
   * Get IPv6 address records (AAAA) for the given domain name and search option.
   *
   * @param domainName fqdn
   * @return list of matching {@link AAAA}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<AAAA> getAAAARec(String domainName, SearchModifier modifier) throws IOException {
    requireNonNull(domainName, "Domain name is null");
    Map<String, String> options = new HashMap<>(1);
    options.put("name" + modifier.getValue(), domainName);
    return exec(infoblox.queryAAAARec(options)).result();
  }

  /**
   * Get IPv6 address records (AAAA) for the given domain name.
   *
   * @param domainName fqdn
   * @return list of matching {@link AAAA}
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<AAAA> getAAAARec(String domainName) throws IOException {
    return getAAAARec(domainName, SearchModifier.NONE);
  }

  /**
   * Creates an IPv6 address record (AAAA Record)
   *
   * @param domainName FQDN
   * @param ipv6Address IPv6 address
   * @return {@link AAAA} address record.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public AAAA createAAAARec(String domainName, String ipv6Address) throws IOException {
    requireIPv6(ipv6Address);
    Map<String, String> req = new HashMap<>(2);
    req.put("name", domainName);
    req.put("ipv6addr", ipv6Address);
    return exec(infoblox.createAAAARec(req)).result();
  }

  /**
   * Deletes IPv6 address record with given domain name.
   *
   * @param domainName fqdn for the AAAA record.
   * @return list of A record obj references deleted.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<String> deleteAAAARec(String domainName) throws IOException {
    return getAAAARec(domainName)
        .stream()
        .map(AAAA::ref)
        .filter(ref -> ref.contains(domainName))
        .map(
            ref -> {
              try {
                return exec(infoblox.deleteRef(ref)).result();
              } catch (IOException ioe) {
                throw new IllegalStateException("Error deleting AAAA record ref: " + ref, ioe);
              }
            })
        .collect(Collectors.toList());
  }

  /**
   * Modify the domain name of AAAA record with given name.
   *
   * @param domainName fqdn for the AAAA record.
   * @param newDomainName new fqdn.
   * @throws IOException if a problem occurred talking to the infoblox.
   */
  public List<AAAA> modifyAAAARec(String domainName, String newDomainName) throws IOException {
    return getAAAARec(domainName)
        .stream()
        .map(AAAA::ref)
        .filter(ref -> ref.contains(domainName))
        .map(
            ref -> {
              Map<String, String> req = new HashMap<>(1);
              req.put("name", newDomainName);
              try {
                return exec(infoblox.modifyAAAARec(ref, req)).result();
              } catch (IOException ioe) {
                throw new IllegalStateException("Error modifying AAAA record ref: " + ref, ioe);
              }
            })
        .collect(Collectors.toList());
  }

  ////// CNAME Record //////

  /**
   * Returns the builder for {@link InfobloxClient} with default values for un-initialized optional
   * fields.
   *
   * @return Builder
   */
  public static Builder builder() {
    return new AutoValue_InfobloxClient.Builder()
        .tlsVerify(true)
        .timeout(10)
        .wapiVersion("2.5")
        .dnsView("default")
        .debug(false);
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

    public abstract Builder debug(boolean debug);

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
