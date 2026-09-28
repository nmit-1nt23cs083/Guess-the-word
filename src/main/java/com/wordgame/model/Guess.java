package com.wordgame.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "guess")
public class Guess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @Column(nullable = false, length = 5)
    private String guessText;

    /** 1-based order in which this guess was submitted (1..5). */
    @Column(nullable = false)
    private int guessOrder;

    /** Comma separated colors, e.g. "GREEN,ORANGE,GREY,GREEN,GREEN" */
    @Column(nullable = false)
    private String pattern;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    public Guess() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public String getGuessText() {
        return guessText;
    }

    public void setGuessText(String guessText) {
        this.guessText = guessText;
    }

    public int getGuessOrder() {
        return guessOrder;
    }

    public void setGuessOrder(int guessOrder) {
        this.guessOrder = guessOrder;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
