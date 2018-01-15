package com.oneops.infoblox.model.host;

import static com.oneops.infoblox.util.IPAddrs.requireIPv4;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * IPv4 object to be used in Host request.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class HostIPv4Req {

  public abstract String ipv4addr();

  public static HostIPv4Req create(String ipv4addr) {
    requireIPv4(ipv4addr);
    return new AutoValue_HostIPv4Req(ipv4addr);
  }

  public static JsonAdapter<HostIPv4Req> jsonAdapter(Moshi moshi) {
    return new AutoValue_HostIPv4Req.MoshiJsonAdapter(moshi);
  }
}
