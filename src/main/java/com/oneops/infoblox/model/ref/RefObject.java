package com.oneops.infoblox.model.ref;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.ToJson;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * A qualifier meta annotation and it's JSON type adapter for the {@link Ref}
 * object. This is used for customizing JSON encoding of the _ref string.
 *
 * @author Suresh G
 */
@Retention(RUNTIME)
@Documented
@JsonQualifier
public @interface RefObject {

  class JsonAdapter {

    @ToJson
    String toJson(@RefObject Ref ref) {
      return ref.value();
    }

    @FromJson
    @RefObject
    Ref fromJson(String ref) {
      return Ref.of(ref);
    }
  }
}
