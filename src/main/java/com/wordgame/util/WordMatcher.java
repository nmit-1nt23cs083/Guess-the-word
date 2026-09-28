package com.wordgame.util;

/**
 * Computes the per-letter feedback pattern for a guess against a target word,
 * the same way Wordle-style games do:
 *   - GREEN  -> letter is correct and in the correct position
 *   - ORANGE -> letter exists in the word but in the wrong position
 *   - GREY   -> letter does not exist in the word (or all its occurrences
 *               were already accounted for by other positions)
 *
 * Duplicate letters are handled correctly: a letter is only marked ORANGE as
 * many times as it still has "unused" occurrences left in the target word,
 * after all exact (GREEN) matches have been removed from the pool.
 */
public final class WordMatcher {

    public static final String GREEN = "GREEN";
    public static final String ORANGE = "ORANGE";
    public static final String GREY = "GREY";

    private WordMatcher() {
    }

    public static String[] match(String guess, String target) {
        if (guess == null || target == null || guess.length() != target.length()) {
            throw new IllegalArgumentException("Guess and target must be non-null and the same length.");
        }

        int len = target.length();
        char[] targetChars = target.toUpperCase().toCharArray();
        char[] guessChars = guess.toUpperCase().toCharArray();
        String[] result = new String[len];

        int[] remaining = new int[26];
        for (char c : targetChars) {
            remaining[c - 'A']++;
        }

        // First pass: exact position matches (GREEN). Remove them from the pool.
        for (int i = 0; i < len; i++) {
            if (guessChars[i] == targetChars[i]) {
                result[i] = GREEN;
                remaining[guessChars[i] - 'A']--;
            }
        }

        // Second pass: letters present elsewhere (ORANGE) vs absent (GREY).
        for (int i = 0; i < len; i++) {
            if (result[i] != null) {
                continue;
            }
            int idx = guessChars[i] - 'A';
            if (idx >= 0 && idx < 26 && remaining[idx] > 0) {
                result[i] = ORANGE;
                remaining[idx]--;
            } else {
                result[i] = GREY;
            }
        }

        return result;
    }
}
