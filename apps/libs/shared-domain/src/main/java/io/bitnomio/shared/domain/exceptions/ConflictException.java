/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 5/8/25 Time: 20:02
 *
 */
package io.bitnomio.shared.domain.exceptions;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
