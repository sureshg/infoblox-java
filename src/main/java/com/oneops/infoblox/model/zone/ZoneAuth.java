package com.oneops.infoblox.model.zone;

import com.google.auto.value.AutoValue;
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

  public abstract String view();

  public abstract String fqdn();

  @Json(name = "_ref")
  public abstract String ref();

  public static JsonAdapter<ZoneAuth> jsonAdapter(Moshi moshi) {
    return new AutoValue_ZoneAuth.MoshiJsonAdapter(moshi);
  }
}
