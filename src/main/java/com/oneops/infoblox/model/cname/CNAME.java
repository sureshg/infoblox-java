package com.oneops.infoblox.model.cname;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.model.ref.Ref;
import com.oneops.infoblox.model.ref.RefObject;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Canonical name record {@link com.oneops.infoblox.model.DNSRecord#CNAME } response.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class CNAME {

  @RefObject
  @Json(name = "_ref")
  public abstract Ref ref();

  public abstract String canonical();

  public abstract String name();

  @Nullable
  public abstract String view();

  public static CNAME create(String ref, String canonical, String name, String view) {
    return new AutoValue_CNAME(Ref.of(ref), canonical, name, view);
  }

  public static JsonAdapter<CNAME> jsonAdapter(Moshi moshi) {
    return new AutoValue_CNAME.MoshiJsonAdapter(moshi);
  }
}
