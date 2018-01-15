package com.oneops.infoblox.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.reflect.Type;

/**
 * Holds a result type containing one or more JSON objects.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class Result<T> {

  public abstract T result();

  public static <T> Result<T> create(T result) {
    return new AutoValue_Result<>(result);
  }

  /** Json adapter for {@link Result} type, used by Moshi for JSON [de]serialization. */
  public static <T> JsonAdapter<Result<T>> jsonAdapter(Moshi moshi, Type[] types) {
    return new AutoValue_Result.MoshiJsonAdapter<>(moshi, types);
  }
}
