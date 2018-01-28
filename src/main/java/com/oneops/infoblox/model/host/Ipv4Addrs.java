package com.oneops.infoblox.model.host;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.model.ref.Ref;
import com.oneops.infoblox.model.ref.RefObject;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Host record response IPv4 object.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class Ipv4Addrs {

  @RefObject
  @Json(name = "_ref")
  public abstract Ref ref();

  public abstract String host();

  @Json(name = "ipv4addr")
  public abstract String ipv4Addr();

  @Json(name = "configure_for_dhcp")
  public abstract boolean configureForDhcp();

  public static Ipv4Addrs create(
      String ref, String host, String ipv4Addr, boolean configureForDhcp) {
    return new AutoValue_Ipv4Addrs(Ref.of(ref), host, ipv4Addr, configureForDhcp);
  }

  public static JsonAdapter<Ipv4Addrs> jsonAdapter(Moshi moshi) {
    return new AutoValue_Ipv4Addrs.MoshiJsonAdapter(moshi);
  }
}
