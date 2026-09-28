package com.wordgame.dto;

public class GuessDto {
    private String guessText;
    private String[] pattern;

    public String getGuessText() {
        return guessText;
    }

    public void setGuessText(String guessText) {
        this.guessText = guessText;
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }
}
