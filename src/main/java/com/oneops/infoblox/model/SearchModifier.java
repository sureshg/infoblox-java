package com.oneops.infoblox.model;

/**
 * Common modifiers used while searching for network objects.
 *
 * @author Suresh G
 */
public enum SearchModifier {

  NONE(""),
  NEGATE("!"),
  CASE_INSENSITIVE(":"),
  REGEX("~"),
  LT("<"),
  GT(">");

  private final String value;

  SearchModifier(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
