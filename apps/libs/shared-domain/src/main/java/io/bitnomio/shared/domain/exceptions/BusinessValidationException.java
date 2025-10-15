/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 5/8/25 Time: 20:01
 *
 */
package io.bitnomio.shared.domain.exceptions;

public class BusinessValidationException extends RuntimeException {
  private final String field;
  private final String errorCode;

  public BusinessValidationException(String message) {
    super(message);
    this.field = null;
    this.errorCode = "BAD_REQUEST"; // Default error code
  }

  public BusinessValidationException(String message, String field) {
    super(message);
    this.field = field;
    this.errorCode = "BAD_REQUEST";
  }

  public BusinessValidationException(String message, String field, String errorCode) {
    super(message);
    this.field = field;
    this.errorCode = errorCode;
  }

  public String getField() {
    return field;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
