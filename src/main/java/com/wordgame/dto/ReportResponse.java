package com.wordgame.dto;

public class ReportResponse {
    private String date;
    private long numberOfUsers;
    private long numberOfGamesPlayed;
    private long numberOfCorrectGuesses;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(long numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public long getNumberOfGamesPlayed() {
        return numberOfGamesPlayed;
    }

    public void setNumberOfGamesPlayed(long numberOfGamesPlayed) {
        this.numberOfGamesPlayed = numberOfGamesPlayed;
    }

    public long getNumberOfCorrectGuesses() {
        return numberOfCorrectGuesses;
    }

    public void setNumberOfCorrectGuesses(long numberOfCorrectGuesses) {
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
    }
}
