package com.wordgame.dto;

/** One line of the per-user report: a date, words tried that day, and how many were guessed correctly. */
public class UserReportRow {
    private String date;
    private long wordsTried;
    private long correctGuesses;

    public UserReportRow() {
    }

    public UserReportRow(String date, long wordsTried, long correctGuesses) {
        this.date = date;
        this.wordsTried = wordsTried;
        this.correctGuesses = correctGuesses;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getWordsTried() {
        return wordsTried;
    }

    public void setWordsTried(long wordsTried) {
        this.wordsTried = wordsTried;
    }

    public long getCorrectGuesses() {
        return correctGuesses;
    }

    public void setCorrectGuesses(long correctGuesses) {
        this.correctGuesses = correctGuesses;
    }
}
