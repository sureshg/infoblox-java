package com.oneops.infoblox;

import com.oneops.infoblox.model.Error;
import java.io.IOException;

/**
 * A custom exception for Infoblox (IBA) response and i/o error messages.
 *
 * @author Suresh G
 */
public class InfobloxException extends IOException {

  /**
   * Creates new IBA exception for the given error response.
   *
   * @param ibaError {@link Error}
   */
  public InfobloxException(Error ibaError) {
    super(ibaError.error());
  }

  /**
   * Constructs a new IBA exception with the specified error message.
   *
   * @param message error message.
   */
  public InfobloxException(String message) {
    super(message);
  }

  /**
   * Constructs a new IBA exception with the specified error message
   * and cause.
   *
   * @param message error message.
   * @param cause the cause.
   */
  public InfobloxException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new IBA exception with the specified cause.
   *
   * @param cause the cause.
   */
  public InfobloxException(Throwable cause) {
    super(cause);
  }
}
