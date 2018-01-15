package com.oneops.infoblox.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * WAPI Object reference. WAPI returns this reference when an object is created, modified, deleted
 * or read. An object reference is a string with the following format, without spaces: <b>wapitype /
 * refdata [ : name1 [ { / nameN }... ] ]</b>
 *
 * @author Suresh G
 */
@AutoValue
public abstract class Ref {

  public abstract String value();

  /**
   * Creates a new Ref object from the given value.
   *
   * @param value ref value.
   * @return new object.
   */
  public static Ref of(String value) {
    return new AutoValue_Ref(value);
  }

  /**
   * Returns the WAPI type from ref object.
   *
   * @return type string or <code>null</code> if can't get the type.
   */
  public String wapiType() {
    String[] vals = value().split("/", 2);
    return vals.length > 0 ? vals[0] : null;
  }

  /**
   * Returns the ref data from wapi object.
   *
   * @return data string or <code>null</code> if can't get the data.
   */
  public String refData() {
    String[] vals = value().split("/", 2);
    String ref = null;
    if (vals.length > 1) {
      ref = vals[1].split(":", 2)[0];
    }
    return ref;
  }

  /**
   * Returns the list of names from ref object.
   *
   * @return list of names or <code>empty list</code> if can't get it.
   */
  public Collection<String> names() {
    String ref = value().split("/", 2)[1];
    String[] vals = ref.split(":", 2);
    List<String> names = Collections.emptyList();
    if (vals.length > 1) {
      names = Arrays.asList(vals[1].split("/"));
    }
    return names;
  }

  /** Json adapter for {@link Error} type, used by Moshi for JSON [de]serialization. */
  public static JsonAdapter<Ref> jsonAdapter(Moshi moshi) {
    return new AutoValue_Ref.MoshiJsonAdapter(moshi);
  }
}
