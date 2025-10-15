/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 5/8/25 Time: 20:02
 *
 */
package io.bitnomio.shared.domain.exceptions;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}