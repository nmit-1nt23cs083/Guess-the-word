package com.wordgame.repository;

import com.wordgame.model.GameSession;
import com.wordgame.model.GameStatus;
import com.wordgame.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    long countByUserAndPlayDate(User user, LocalDate playDate);

    long countByPlayDate(LocalDate playDate);

    long countByPlayDateAndStatus(LocalDate playDate, GameStatus status);

    @Query("select count(distinct g.user) from GameSession g where g.playDate = :date")
    long countDistinctUsersByPlayDate(@Param("date") LocalDate date);

    /** Per-day rows for one user: [playDate, wordsTried, correctGuesses], newest date first. */
    @Query("select g.playDate, count(g), sum(case when g.status = com.wordgame.model.GameStatus.WON then 1 else 0 end) "
            + "from GameSession g where g.user = :user group by g.playDate order by g.playDate desc")
    List<Object[]> findUserReportRows(@Param("user") User user);
}
