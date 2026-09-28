package com.wordgame.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WordMatcherTest {

    @Test
    void allGreenWhenExactMatch() {
        String[] result = WordMatcher.match("TOWER", "TOWER");
        assertArrayEquals(new String[]{"GREEN", "GREEN", "GREEN", "GREEN", "GREEN"}, result);
    }

    @Test
    void allGreyWhenNoLettersMatch() {
        String[] result = WordMatcher.match("BUNCH", "TOWER");
        assertArrayEquals(new String[]{"GREY", "GREY", "GREY", "GREY", "GREY"}, result);
    }

    @Test
    void detectsCorrectAndMisplacedLetters() {
        // Target: TOWER, Guess: HOMER
        // H -> not in TOWER            -> GREY
        // O -> position 1 matches      -> GREEN
        // M -> not in TOWER            -> GREY
        // E -> position 3 matches      -> GREEN
        // R -> position 4 matches      -> GREEN
        String[] result = WordMatcher.match("HOMER", "TOWER");
        assertArrayEquals(new String[]{"GREY", "GREEN", "GREY", "GREEN", "GREEN"}, result);
    }

    @Test
    void handlesDuplicateLettersInGuessCorrectly() {
        // Target TOWER has exactly one 'O'. Guess TOOLS has two 'O's.
        // T -> position 0 matches       -> GREEN
        // O -> position 1 matches       -> GREEN (uses up the only O)
        // O -> no more O's left         -> GREY
        // L -> not in TOWER             -> GREY
        // S -> not in TOWER             -> GREY
        String[] result = WordMatcher.match("TOOLS", "TOWER");
        assertArrayEquals(new String[]{"GREEN", "GREEN", "GREY", "GREY", "GREY"}, result);
    }

    @Test
    void marksMisplacedLettersAsOrangeAndKeepsExactMatchGreen() {
        // Target: TOWER (T-O-W-E-R), Guess: RIVET (R-I-V-E-T)
        // R -> present but wrong position (target R is at index 4) -> ORANGE
        // I -> not in TOWER                                        -> GREY
        // V -> not in TOWER                                        -> GREY
        // E -> exact position match (index 3 in both)              -> GREEN
        // T -> present but wrong position (target T is at index 0) -> ORANGE
        String[] result = WordMatcher.match("RIVET", "TOWER");
        assertArrayEquals(new String[]{"ORANGE", "GREY", "GREY", "GREEN", "ORANGE"}, result);
    }

    @Test
    void throwsOnMismatchedLength() {
        assertThrows(IllegalArgumentException.class, () -> WordMatcher.match("ABCD", "TOWER"));
    }
}
