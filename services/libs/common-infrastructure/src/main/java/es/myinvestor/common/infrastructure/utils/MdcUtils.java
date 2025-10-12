package es.myinvestor.common.infrastructure.utils;

import org.slf4j.MDC;
import java.util.Optional;

import static es.myinvestor.common.infrastructure.utils.RequestIdExtractor.REQUEST_ID_ATTRIBUTE;


public final class MdcUtils {

  public static final String MDC_REQUEST_USERNAME = "req-username";
  public static final String MDC_AUDITOR_USERNAME = "auditUsername";
  public static final String DEFAULT_AUDIT_USER = "MYI_SYSTEM"; // Fallback user
  public static final String DEFAULT_CORRELATION_ID = "CID_NOT_PRESENT"; // Fallback user

  private MdcUtils() {
    // Private constructor to prevent instantiation of utility class
  }

  public static String getCurrentAuditor() {
    return Optional.ofNullable(MDC.get(MDC_REQUEST_USERNAME))
        .orElse(DEFAULT_AUDIT_USER);
  }

  public static String getCurrentCorrelationId() {
    return Optional.ofNullable(MDC.get(REQUEST_ID_ATTRIBUTE))
        .orElse(DEFAULT_CORRELATION_ID);
  }
}
