package com.oneops.infoblox.model.host;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;

/**
 * Host Record Request.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class HostReq {

  public abstract String name();

  public abstract List<HostIPv4Req> ipv4addrs();

  public static HostReq create(String name, List<HostIPv4Req> ipv4addrs) {
    return new AutoValue_HostReq(name, ipv4addrs);
  }

  public static JsonAdapter<HostReq> jsonAdapter(Moshi moshi) {
    return new AutoValue_HostReq.MoshiJsonAdapter(moshi);
  }
}
