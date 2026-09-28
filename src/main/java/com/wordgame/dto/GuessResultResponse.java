package com.wordgame.dto;

import java.util.List;

public class GuessResultResponse {
    private Long gameId;
    private String[] pattern;
    private String status;
    private String message;
    private int guessesRemaining;
    private List<GuessDto> guesses;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    public void setGuessesRemaining(int guessesRemaining) {
        this.guessesRemaining = guessesRemaining;
    }

    public List<GuessDto> getGuesses() {
        return guesses;
    }

    public void setGuesses(List<GuessDto> guesses) {
        this.guesses = guesses;
    }
}
