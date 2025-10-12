package es.myinvestor.common.infrastructure.utils;

import io.hypersistence.tsid.TSID;

import java.util.regex.Pattern;

/**
 * Utility class for string operations.
 * Provides methods for common string manipulations used across the application.
 */
public final class StringUtils {

  private static final Pattern UUID_PATTERN =
      Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

  private static final Pattern TSID_PATTERN =
      Pattern.compile("^[0-9a-z]{10}$");

  // Private constructor to prevent instantiation
  private StringUtils() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  /**
   * Checks if a string is null or empty.
   *
   * @param str the string to check
   * @return true if the string is null or empty, false otherwise
   */
  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * Checks if a string is null, empty, or contains only whitespace.
   *
   * @param str the string to check
   * @return true if the string is null, empty, or contains only whitespace, false otherwise
   */
  public static boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Returns the string if it is not null, or an empty string if it is null.
   *
   * @param str the string to check
   * @return the string if it is not null, or an empty string if it is null
   */
  public static String nullToEmpty(String str) {
    return str == null ? "" : str;
  }

  /**
   * Returns the string if it is not null or empty, or the default value if it is.
   *
   * @param str          the string to check
   * @param defaultValue the default value to return if the string is null or empty
   * @return the string if it is not null or empty, or the default value if it is
   */
  public static String defaultIfEmpty(String str, String defaultValue) {
    return isEmpty(str) ? defaultValue : str;
  }

  /**
   * Truncates a string to the specified length.
   *
   * @param str       the string to truncate
   * @param maxLength the maximum length
   * @return the truncated string
   */
  public static String truncate(String str, int maxLength) {
    if (str == null) {
      return null;
    }
    return str.length() <= maxLength ? str : str.substring(0, maxLength);
  }

  /**
   * Generates a random ID string using TSID.
   *
   * @return a random TSID string
   */
  public static String generateId() {
    return TSID.fast().toString();
  }

  /**
   * Checks if a string is a valid UUID.
   *
   * @param str the string to check
   * @return true if the string is a valid UUID, false otherwise
   */
  public static boolean isValidUuid(String str) {
    if (isEmpty(str)) {
      return false;
    }
    return UUID_PATTERN.matcher(str).matches();
  }

  /**
   * Checks if a string is a valid TSID.
   *
   * @param str the string to check
   * @return true if the string is a valid TSID, false otherwise
   */
  public static boolean isValidTsid(String str) {
    if (isEmpty(str)) {
      return false;
    }
    return TSID_PATTERN.matcher(str).matches();
  }

  /**
   * Masks a sensitive string for logging.
   * For example, masks a credit card number or password.
   *
   * @param str          the string to mask
   * @param visibleChars the number of characters to leave visible at the beginning and end
   * @return the masked string
   */
  public static String maskSensitive(String str, int visibleChars) {
    if (isEmpty(str) || str.length() <= visibleChars * 2) {
      return str;
    }

    String prefix = str.substring(0, visibleChars);
    String suffix = str.substring(str.length() - visibleChars);
    int maskLength = str.length() - (visibleChars * 2);

    StringBuilder masked = new StringBuilder(prefix);
    for (int i = 0; i < maskLength; i++) {
      masked.append('*');
    }
    masked.append(suffix);

    return masked.toString();
  }
}
