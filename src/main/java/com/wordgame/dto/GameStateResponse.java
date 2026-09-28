package com.wordgame.dto;

import java.util.List;

public class GameStateResponse {
    private Long gameId;
    private String status;
    private int guessesRemaining;
    private List<GuessDto> guesses;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
