package com.wordgame.repository;

import com.wordgame.model.GameSession;
import com.wordgame.model.Guess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuessRepository extends JpaRepository<Guess, Long> {
    List<Guess> findByGameSessionOrderByGuessOrderAsc(GameSession gameSession);
}
