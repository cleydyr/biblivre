package biblivre.cataloging.isbnlookup;

import java.util.Locale;

/** ISBN normalization and check-digit validation. */
public final class Isbn {

    private Isbn() {}

    /** Removes everything except digits and {@code X}, upper-casing the latter. */
    public static String normalize(String rawIsbn) {
        if (rawIsbn == null) {
            return "";
        }

        return rawIsbn.toUpperCase(Locale.ROOT).replaceAll("[^0-9X]", "");
    }

    /** Whether a normalized value is an ISBN-10 or ISBN-13 with a correct check digit. */
    public static boolean isValid(String normalizedIsbn) {
        if (normalizedIsbn == null) {
            return false;
        }

        return switch (normalizedIsbn.length()) {
            case 10 -> isValidIsbn10(normalizedIsbn);
            case 13 -> isValidIsbn13(normalizedIsbn);
            default -> false;
        };
    }

    /** Converts a valid ISBN-10 to ISBN-13; returns ISBN-13 input unchanged. */
    public static String toIsbn13(String normalizedIsbn) {
        if (normalizedIsbn.length() != 10) {
            return normalizedIsbn;
        }

        String body = "978" + normalizedIsbn.substring(0, 9);

        return body + isbn13CheckDigit(body);
    }

    private static boolean isValidIsbn10(String isbn) {
        if (!isbn.matches("\\d{9}[\\dX]")) {
            return false;
        }

        int sum = 0;

        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int value = c == 'X' ? 10 : c - '0';
            sum += value * (10 - i);
        }

        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        if (!isbn.matches("97[89]\\d{10}")) {
            return false;
        }

        return isbn13CheckDigit(isbn.substring(0, 12)) == isbn.charAt(12) - '0';
    }

    private static int isbn13CheckDigit(String first12Digits) {
        int sum = 0;

        for (int i = 0; i < 12; i++) {
            int digit = first12Digits.charAt(i) - '0';
            sum += i % 2 == 0 ? digit : digit * 3;
        }

        return (10 - sum % 10) % 10;
    }
}
