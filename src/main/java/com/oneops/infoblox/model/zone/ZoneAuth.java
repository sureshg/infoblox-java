package com.oneops.infoblox.model.zone;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.model.ref.Ref;
import com.oneops.infoblox.model.ref.RefObject;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * DNS Authoritative Zone
 *
 * @author Suresh G
 */
@AutoValue
public abstract class ZoneAuth {

  @RefObject
  @Json(name = "_ref")
  public abstract Ref ref();

  public abstract String view();

  public abstract String fqdn();

  public static ZoneAuth create(String ref, String view, String fqdn) {
    return new AutoValue_ZoneAuth(Ref.of(ref), view, fqdn);
  }

  public static JsonAdapter<ZoneAuth> jsonAdapter(Moshi moshi) {
    return new AutoValue_ZoneAuth.MoshiJsonAdapter(moshi);
  }
}
