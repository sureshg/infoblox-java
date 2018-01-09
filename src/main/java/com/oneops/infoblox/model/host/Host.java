package com.oneops.infoblox.model.host;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;

/**
 * DNS Host record.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class Host {

  public abstract String view();

  public abstract List<String> aliases();

  @Json(name = "_ref")
  public abstract String ref();

  public abstract String name();

  @Json(name = "ipv4addrs")
  public abstract List<Ipv4Addrs> ipv4Addrs();

  public static Host create(String view, List<String> aliases, String ref, String name,
      List<Ipv4Addrs> ipv4Addrs) {
    return new AutoValue_Host(view, aliases, ref, name, ipv4Addrs);
  }

  public static JsonAdapter<Host> jsonAdapter(Moshi moshi) {
    return new AutoValue_Host.MoshiJsonAdapter(moshi);
  }
}