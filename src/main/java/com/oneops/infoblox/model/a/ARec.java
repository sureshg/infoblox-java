package com.oneops.infoblox.model.a;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.model.ref.Ref;
import com.oneops.infoblox.model.ref.RefObject;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Address record {@link com.oneops.infoblox.model.DNSRecord#A } response.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class ARec {

  @RefObject
  @Json(name = "_ref")
  public abstract Ref ref();

  @Json(name = "ipv4addr")
  public abstract String ipv4Addr();

  public abstract String name();

  @Nullable
  public abstract String view();

  public static ARec create(String ref, String ipv4Addr, String name, String view) {
    return new AutoValue_ARec(Ref.of(ref), ipv4Addr, name, view);
  }

  public static JsonAdapter<ARec> jsonAdapter(Moshi moshi) {
    return new AutoValue_ARec.MoshiJsonAdapter(moshi);
  }
}
