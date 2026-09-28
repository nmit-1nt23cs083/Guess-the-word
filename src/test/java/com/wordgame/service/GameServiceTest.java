package com.wordgame.service;

import com.wordgame.dto.GameStateResponse;
import com.wordgame.dto.GuessResultResponse;
import com.wordgame.model.*;
import com.wordgame.repository.GameSessionRepository;
import com.wordgame.repository.GuessRepository;
import com.wordgame.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameSessionRepository gameSessionRepository;
    private GuessRepository guessRepository;
    private WordRepository wordRepository;
    private GameService gameService;
    private User player;

    @BeforeEach
    void setUp() {
        gameSessionRepository = mock(GameSessionRepository.class);
        guessRepository = mock(GuessRepository.class);
        wordRepository = mock(WordRepository.class);
        gameService = new GameService(gameSessionRepository, guessRepository, wordRepository);

        player = new User("PlayerOne", "hashed", Role.PLAYER);
        player.setId(1L);

        when(wordRepository.findAll()).thenReturn(List.of(new Word("TOWER")));

        // save() just returns whatever was passed, assigning a fake id if missing
        when(gameSessionRepository.save(any(GameSession.class))).thenAnswer(inv -> {
            GameSession gs = inv.getArgument(0);
            if (gs.getId() == null) gs.setId(100L);
            return gs;
        });
        when(guessRepository.save(any(Guess.class))).thenAnswer(inv -> inv.getArgument(0));
        when(guessRepository.findByGameSessionOrderByGuessOrderAsc(any())).thenReturn(new ArrayList<>());
    }

    @Test
    void startGameRejectedWhenDailyLimitReached() {
        when(gameSessionRepository.countByUserAndPlayDate(eq(player), eq(LocalDate.now()))).thenReturn(3L);
        assertThrows(IllegalStateException.class, () -> gameService.startGame(player));
    }

    @Test
    void startGameSucceedsUnderLimit() {
        when(gameSessionRepository.countByUserAndPlayDate(eq(player), eq(LocalDate.now()))).thenReturn(1L);
        GameStateResponse response = gameService.startGame(player);
        assertEquals("IN_PROGRESS", response.getStatus());
        assertEquals(5, response.getGuessesRemaining());
    }

    @Test
    void submitGuessRejectsInvalidFormat() {
        GameSession session = sessionInProgress();
        when(gameSessionRepository.findById(100L)).thenReturn(Optional.of(session));
        assertThrows(IllegalArgumentException.class, () -> gameService.submitGuess(100L, player, "abc"));
        assertThrows(IllegalArgumentException.class, () -> gameService.submitGuess(100L, player, "TOOLONG"));
    }

    @Test
    void submitGuessWinsWhenGuessMatchesWord() {
        GameSession session = sessionInProgress();
        when(gameSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        GuessResultResponse response = gameService.submitGuess(100L, player, "TOWER");

        assertEquals("WON", response.getStatus());
        assertNotNull(response.getMessage());
        assertArrayEquals(new String[]{"GREEN", "GREEN", "GREEN", "GREEN", "GREEN"}, response.getPattern());
    }

    @Test
    void submitGuessLosesAfterFiveWrongGuesses() {
        GameSession session = sessionInProgress();
        when(gameSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        ArgumentCaptor<GameSession> captor = ArgumentCaptor.forClass(GameSession.class);

        GuessResultResponse last = null;
        for (int i = 0; i < 5; i++) {
            last = gameService.submitGuess(100L, player, "PLANE"); // never matches TOWER
        }
        assertEquals("LOST", last.getStatus());
        assertTrue(last.getMessage().contains("TOWER"));
    }

    @Test
    void submitGuessRejectedAfterGameAlreadyEnded() {
        GameSession session = sessionInProgress();
        session.setStatus(GameStatus.WON);
        when(gameSessionRepository.findById(100L)).thenReturn(Optional.of(session));
        assertThrows(IllegalStateException.class, () -> gameService.submitGuess(100L, player, "TOWER"));
    }

    @Test
    void otherUsersCannotAccessSomeoneElsesGame() {
        GameSession session = sessionInProgress();
        when(gameSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        User other = new User("SomeoneElse", "hashed", Role.PLAYER);
        other.setId(2L);

        assertThrows(SecurityException.class, () -> gameService.submitGuess(100L, other, "TOWER"));
    }

    private GameSession sessionInProgress() {
        GameSession session = new GameSession();
        session.setId(100L);
        session.setUser(player);
        session.setWord(new Word("TOWER"));
        session.setStatus(GameStatus.IN_PROGRESS);
        session.setPlayDate(LocalDate.now());
        session.setGuessCount(0);
        return session;
    }
}
