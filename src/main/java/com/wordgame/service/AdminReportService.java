package com.wordgame.service;

import com.wordgame.dto.ReportResponse;
import com.wordgame.dto.UserReportResponse;
import com.wordgame.dto.UserReportRow;
import com.wordgame.model.User;
import com.wordgame.repository.UserRepository;
import com.wordgame.model.GameStatus;
import com.wordgame.repository.GameSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminReportService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;

    public AdminReportService(GameSessionRepository gameSessionRepository, UserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Builds the daily report for the given date:
     *   - distinct number of users who played that day
     *   - total number of games played that day
     *   - number of games that were won (correctly guessed) that day
     */
    public ReportResponse getDailyReport(LocalDate date) {
        ReportResponse response = new ReportResponse();
        response.setDate(date.toString());
        response.setNumberOfUsers(gameSessionRepository.countDistinctUsersByPlayDate(date));
        response.setNumberOfGamesPlayed(gameSessionRepository.countByPlayDate(date));
        response.setNumberOfCorrectGuesses(gameSessionRepository.countByPlayDateAndStatus(date, GameStatus.WON));
        return response;
    }

    /**
     * Builds the report for one user: for each date, the number of words tried
     * and the number guessed correctly. If a date is given, only that date is returned.
     */
    public UserReportResponse getUserReport(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<UserReportRow> rows = new ArrayList<>();
        for (Object[] r : gameSessionRepository.findUserReportRows(user)) {
            LocalDate playDate = (LocalDate) r[0];
            if (date != null && !date.equals(playDate)) {
                continue;
            }
            rows.add(new UserReportRow(playDate.toString(), ((Number) r[1]).longValue(), ((Number) r[2]).longValue()));
        }

        UserReportResponse response = new UserReportResponse();
        response.setUsername(user.getUsername());
        response.setRows(rows);
        return response;
    }
}
