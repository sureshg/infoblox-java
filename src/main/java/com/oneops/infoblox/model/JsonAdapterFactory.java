package com.oneops.infoblox.model;

import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;
import com.squareup.moshi.JsonAdapter;

/**
 * A {@link JsonAdapter.Factory} for all of the auto-value-moshi classes. This is
 * to avoid adding each generated JsonAdapter to your Moshi instance manually.
 *
 * @author Suresh
 */
@MoshiAdapterFactory
public abstract class JsonAdapterFactory implements JsonAdapter.Factory {

  public static JsonAdapter.Factory create() {
    return new AutoValueMoshi_JsonAdapterFactory();
  }
}
