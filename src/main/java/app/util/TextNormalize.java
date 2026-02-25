package app.util;

import java.text.Normalizer;

public final class TextNormalize {
    private TextNormalize() {
    }

    /**
     * Normalizes text for searching:
     * - trims
     * - removes diacritics
     * - lowercases
     * - removes punctuation/spaces (keeps letters+digits only)
     */
    public static String normalize(String s) {
        if (s == null) return "";

        String x = s.trim();

        // Remove diacritics
        x = Normalizer.normalize(x, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        // Lowercase
        x = x.toLowerCase();

        // Remove punctuation/spaces (keep only letters and digits)
        x = x.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]+", "");

        return x;
    }
}
