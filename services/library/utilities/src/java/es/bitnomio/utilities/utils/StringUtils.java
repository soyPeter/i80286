package es.bitnomio.utilities.utils;

import es.bitnomio.utilities.constants.AppConfig;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * String utils
 * 2024 [Peter]
 */
public final class StringUtils {

    /**
     * Removes accents and Spanish specials chars
     *
     * @param input the string to be cleaned from accents and local chars
     * @return returns clean String
     */
    public static String normalice(String input) {
        return Normalizer
            .normalize(input, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * <p>
     * Receives a String and returns a fixed-size List sliced the string on a regex delimiter defined as:
     *
     * <ol>
     *  <li> Zero or more whitespace </li>
     *  <li> A literal split marker </li>
     *  <li> Zero or more whitespace </li>
     * </ol>
     * </p>
     * This will place the filter into the list and remove any whitespace between the words and commas.
     *
     * @param rawString string to split
     * @param marker    marker to split by
     * @return List<String>
     */
    public static List<String> rawStringToListSpliterator(final String rawString, final String marker) {
        return Arrays.asList(rawString.split("\\s*" + marker + "\\s*"));
    }

    /**
     * <p>
     * Receives a String and returns a fixed-size List sliced the string on a regex delimiter defined as:
     *
     * <ol>
     *  <li> Zero or more whitespace </li>
     *  <li> A literal split marker </li>
     *  <li> Zero or more whitespace </li>
     * </ol>
     * </p>
     * This will place the filter into the list and remove any whitespace between the words and commas and
     * concat a parenthesis due to remove the regex delimeter.
     *
     * @param rawString string to split
     * @param marker marker to split by
     * @return List<String>
     */
    public static List<String> rawStringToCriteriaListSpliterator(final String rawString, final String marker) {
        return Arrays
                .asList(rawString.split("\\s*" + marker + "\\s*"))
                .stream()
                .map(s -> s.concat(AppConfig.Markers.PARENTHESIS_CLOSE))
                .collect(Collectors.toList());
    }


    public static boolean isBlank( final String s ) {
        // Null-safe, short-circuit evaluation.
        return Objects.isNull(s) || s.isBlank();
    }

    /**
     * Removes spaces from the input String
     * @param input a text.
     * @return input without spaces if input is not null, otherwise return null.
     */
    public static String removeSpaces(String input) {
        return input == null ? null : input.replace(" ", "");
    }

    /**
     * Remove all (),-,_; characters from the input
     *
     * @param input a text
     * @return input without characters not valid and other case null.
     */
    public static String removeInvalidCharacters(String input) {
        return normalice(input)
            .replaceAll("[-._;]", " ")
            .replaceAll("[()]","");
    }


}
