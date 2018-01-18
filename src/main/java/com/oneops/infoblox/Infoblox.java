package com.oneops.infoblox;

import com.oneops.infoblox.model.Result;
import com.oneops.infoblox.model.a.ARec;
import com.oneops.infoblox.model.aaaa.AAAA;
import com.oneops.infoblox.model.cname.CNAME;
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

  /** Auth zone Record */
  @GET("zone_auth")
  Call<Result<List<ZoneAuth>>> queryAuthZones();

  @GET("zone_auth")
  Call<Result<List<ZoneAuth>>> queryAuthZone(@QueryMap(encoded = true) Map<String, String> options);

  /** Host Record */
  @GET("./record:host")
  Call<Result<List<Host>>> queryHostRec(@QueryMap(encoded = true) Map<String, String> options);

  @POST("./record:host?_return_fields=name,ipv4addrs,view")
  Call<Result<Host>> createHostRec(@Body HostReq req);

  /** Delete Record */
  @DELETE("./{ref}")
  Call<Result<String>> deleteRef(@Path(value = "ref", encoded = true) String ref);

  /** A Record */
  @GET("./record:a")
  Call<Result<List<ARec>>> queryARec(@QueryMap(encoded = true) Map<String, String> options);

  @POST("./record:a?_return_fields=name,ipv4addr,view")
  Call<Result<ARec>> createARec(@Body Map<String, String> req);

  @PUT("./{ref}?_return_fields=name,ipv4addr,view")
  Call<Result<ARec>> modifyARec(
      @Path(value = "ref", encoded = true) String ref, @Body Map<String, String> req);

  /** AAAA Record */
  @GET("./record:aaaa")
  Call<Result<List<AAAA>>> queryAAAARec(@QueryMap(encoded = true) Map<String, String> options);

  @POST("./record:aaaa?_return_fields=name,ipv6addr,view")
  Call<Result<AAAA>> createAAAARec(@Body Map<String, String> req);

  @PUT("./{ref}?_return_fields=name,ipv6addr,view")
  Call<Result<AAAA>> modifyAAAARec(
      @Path(value = "ref", encoded = true) String ref, @Body Map<String, String> req);

  /** CNAME Record */
  @GET("./record:cname")
  Call<Result<List<CNAME>>> queryCNAMERec(@QueryMap(encoded = true) Map<String, String> options);

  @POST("./record:cname?_return_fields=name,canonical,view")
  Call<Result<CNAME>> createCNAMERec(@Body Map<String, String> req);

  @PUT("./{ref}?_return_fields=name,canonical,view")
  Call<Result<CNAME>> modifyCNAMERec(
      @Path(value = "ref", encoded = true) String ref, @Body Map<String, String> req);

  @POST("logout")
  Call<Void> logout();
}
