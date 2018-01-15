package com.oneops.infoblox.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation to denote the redacted fields for auto value classes.
 *
 * @author Suresh
 */
@Retention(SOURCE)
@Target({METHOD, PARAMETER, FIELD})
public @interface Redacted {}
