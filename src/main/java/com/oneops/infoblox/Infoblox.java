package com.oneops.infoblox;

import com.oneops.infoblox.model.Result;
import com.oneops.infoblox.model.a.ARec;
import com.oneops.infoblox.model.host.Host;
import com.oneops.infoblox.model.host.HostReq;
import com.oneops.infoblox.model.zone.ZoneAuth;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Infoblox DNS appliance (IBA) REST interface.
 *
 * @author Suresh G
 */
public interface Infoblox {

  @GET("zone_auth")
  Call<Result<List<ZoneAuth>>> queryAuthZones();

  @GET("zone_auth")
  Call<Result<List<ZoneAuth>>> queryAuthZone(@QueryMap(encoded = true) Map<String, String> options);

  @GET("./record:host")
  Call<Result<List<Host>>> queryHostRec(@QueryMap(encoded = true) Map<String, String> options);

  @POST("./record:host?_return_fields=name,ipv4addrs,view")
  Call<Result<Host>> createHostRec(@Body HostReq req);

  @DELETE("./{ref}")
  Call<Result<String>> deleteRef(@Path(value = "ref", encoded = true) String objRef);

  @GET("./record:a")
  Call<Result<List<ARec>>> queryARec(@QueryMap(encoded = true) Map<String, String> options);

  @POST("./record:a?_return_fields=name,ipv4addr,view")
  Call<Result<ARec>> createARec(@Body Map<String, String> req);

  @PUT("./{ref}?_return_fields=name,ipv4addr,view")
  Call<Result<ARec>> modifyARec(
      @Path(value = "ref", encoded = true) String aRef, @Body Map<String, String> req);

  @POST("logout")
  Call<Void> logout();
}
