package com.oneops.infoblox.model.host;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.model.ref.Ref;
import com.oneops.infoblox.model.ref.RefObject;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;
import javax.annotation.Nullable;

/**
 * DNS Host record.
 *
 * <p>Note: There is no such thing as a Host record in the actual DNS specification. Host records
 * are generally a logical construct in DDI (DNS, DHCP, and IPAM) solutions like Infoblox and
 * others. They comprise various DNS record types (A, AAAA, PTR, CNAME, etc) and other metadata
 * associated with a "host".
 *
 * @author Suresh G
 * @see <a href="https://serverfault.com/a/700350">Host record and A record</a>
 */
@AutoValue
public abstract class Host {

  @RefObject
  @Json(name = "_ref")
  public abstract Ref ref();

  @Json(name = "ipv4addrs")
  public abstract List<Ipv4Addrs> ipv4Addrs();

  public abstract String name();

  @Nullable
  public abstract String view();

  @Nullable
  public abstract List<String> aliases();

  public static Host create(
      String ref, List<Ipv4Addrs> ipv4Addrs, String name, String view, List<String> aliases) {
    return new AutoValue_Host(Ref.of(ref), ipv4Addrs, name, view, aliases);
  }

  public static JsonAdapter<Host> jsonAdapter(Moshi moshi) {
    return new AutoValue_Host.MoshiJsonAdapter(moshi);
  }
}
