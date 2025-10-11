package es.bitnomio.utilities.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtils {

  // By default, but awaiting to internationalization
  private static final String ZONE_EUROPE_MADRID = "Europe/Madrid";

  public static LocalDate fromStringUTCToLocalDate(String date) {
    // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such.
    Locale spain = new Locale("es", "ES");
    Locale mexico = new Locale("mx", "MX");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    formatter = formatter.withLocale(spain);
    String validDate = date.substring(0, 10);
    return LocalDate.parse(validDate, formatter);
  }

  public static int getDiffInYearsFromNow(LocalDate dateIni) {
    LocalDate now = LocalDate.now();

    return getDiffInYears(dateIni, now);
  }

  public static int getDiffInMonthsFromNow(LocalDate dateIni) {
    LocalDate now = LocalDate.now();

    return getDiffInMonths(dateIni, now);
  }

  public static int getDiffInDaysFromNow(LocalDate dateIni) {
    LocalDate now = LocalDate.now();

    return getDiffInDays(dateIni, now);
  }

  public static int getDiffInYears(LocalDate dateIni, LocalDate dateEnd) {
    Period age = Period.between(dateIni, dateEnd);
    return age.getYears();
  }

  public static int getDiffInMonths(LocalDate dateIni, LocalDate dateEnd) {
    Period age = Period.between(dateIni, dateEnd);
    return age.getMonths();
  }

  public static int getDiffInDays(LocalDate dateIni, LocalDate dateEnd) {
    Period age = Period.between(dateIni, dateEnd);
    return age.getDays();
  }


  public static LocalDateTime stringToDateTime(String dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(dateTime, formatter);
  }

  public static LocalDateTime dateMinusHour(LocalDateTime dateTime, long hours) {
    return dateTime.minusHours(hours);
  }

  public static LocalDateTime datePlusHour(LocalDateTime dateTime, long hours) {
    return dateTime.plusHours(hours);
  }

  public static LocalDateTime getActualMadridDate() {
    return LocalDateTime.now(ZoneId.of(ZONE_EUROPE_MADRID));
  }
}
