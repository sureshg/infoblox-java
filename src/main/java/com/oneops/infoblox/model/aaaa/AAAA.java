package com.oneops.infoblox.model.aaaa;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * IPv6 Address record {@link com.oneops.infoblox.model.DNSRecord#AAAA } response.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class AAAA {

  @Json(name = "_ref")
  public abstract String ref();

  @Json(name = "ipv6addr")
  public abstract String ipv6Addr();

  public abstract String name();

  @Nullable
  public abstract String view();

  public static AAAA create(String ref, String ipv6Addr, String name, String view) {
    return new AutoValue_AAAA(ref, ipv6Addr, name, view);
  }

  public static JsonAdapter<AAAA> jsonAdapter(Moshi moshi) {
    return new AutoValue_AAAA.MoshiJsonAdapter(moshi);
  }
}
