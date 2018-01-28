package com.oneops.infoblox.model;

import com.google.auto.value.AutoValue;
import com.oneops.infoblox.InfobloxException;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Infoblox Appliance (IBA) WAPI error response.
 *
 * @author Suresh G
 */
@AutoValue
public abstract class Error {

  /**
   * Error type (followed by an explanation after :)
   *
   * @return error type
   */
  @Json(name = "Error")
  public abstract String error();

  /**
   * Symbolic error code.
   *
   * @return error code.
   */
  public abstract String code();

  /**
   * Explanation of the error.
   *
   * @return error description
   */
  public abstract String text();

  /**
   * Debug trace from the server, only if debug is on. Can be null.
   *
   * @return debug trace string.
   */
  @Nullable
  public abstract String trace();

  /**
   * The exception cause for this error.
   *
   * @return {@link InfobloxException}
   */
  public InfobloxException cause() {
    return new InfobloxException(code() + ": " + error());
  }

  /**
   * Creates new error object
   *
   * @param error error type
   * @param code error code
   * @param text explanation
   * @param trace debug trace
   * @return {@link Error}
   */
  public static Error create(String error, String code, String text, @Nullable String trace) {
    return new AutoValue_Error(error, code, text, trace);
  }

  public static Error create(String error, int code) {
    return create(error, String.valueOf(code), "", "");
  }

  /**
   * Json adapter for {@link Error} type, used by Moshi for JSON [de]serialization.
   */
  public static JsonAdapter<Error> jsonAdapter(Moshi moshi) {
    return new AutoValue_Error.MoshiJsonAdapter(moshi);
  }
}
