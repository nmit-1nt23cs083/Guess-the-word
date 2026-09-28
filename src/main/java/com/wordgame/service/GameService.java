package com.wordgame.service;

import com.wordgame.dto.GameStateResponse;
import com.wordgame.dto.GuessDto;
import com.wordgame.dto.GuessResultResponse;
import com.wordgame.model.GameSession;
import com.wordgame.model.GameStatus;
import com.wordgame.model.Guess;
import com.wordgame.model.Role;
import com.wordgame.model.User;
import com.wordgame.model.Word;
import com.wordgame.repository.GameSessionRepository;
import com.wordgame.repository.GuessRepository;
import com.wordgame.repository.WordRepository;
import com.wordgame.util.WordMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GameService {

    public static final int MAX_GAMES_PER_DAY = 3;
    public static final int MAX_GUESSES = 5;
    private static final int WORD_LENGTH = 5;

    private final GameSessionRepository gameSessionRepository;
    private final GuessRepository guessRepository;
    private final WordRepository wordRepository;
    private final Random random = new Random();

    public GameService(GameSessionRepository gameSessionRepository,
                        GuessRepository guessRepository,
                        WordRepository wordRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.guessRepository = guessRepository;
        this.wordRepository = wordRepository;
    }

    /** Starts a new game for the user, picking a random word, if today's limit hasn't been reached. */
    public GameStateResponse startGame(User user) {
        LocalDate today = LocalDate.now();
        long playedToday = gameSessionRepository.countByUserAndPlayDate(user, today);
        if (playedToday >= MAX_GAMES_PER_DAY) {
            throw new IllegalStateException(
                    "Daily limit reached: you can only play " + MAX_GAMES_PER_DAY + " games per day. Try again tomorrow.");
        }

        List<Word> words = wordRepository.findAll();
        if (words.isEmpty()) {
            throw new IllegalStateException("No words are configured yet. Please contact the admin.");
        }
        Word word = words.get(random.nextInt(words.size()));

        GameSession session = new GameSession();
        session.setUser(user);
        session.setWord(word);
        session.setStatus(GameStatus.IN_PROGRESS);
        session.setPlayDate(today);
        session.setCreatedAt(LocalDateTime.now());
        session.setGuessCount(0);
        gameSessionRepository.save(session);

        return toGameStateResponse(session, List.of());
    }

    public GameStateResponse getState(Long gameId, User user) {
        GameSession session = getOwnedSession(gameId, user);
        List<Guess> guesses = guessRepository.findByGameSessionOrderByGuessOrderAsc(session);
        return toGameStateResponse(session, guesses);
    }

    /** Submits a guess for an in-progress game and returns the resulting color pattern and game state. */
    public GuessResultResponse submitGuess(Long gameId, User user, String guessText) {
        GameSession session = getOwnedSession(gameId, user);

        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("This game has already ended.");
        }
        if (guessText == null || !guessText.matches("^[A-Z]{" + WORD_LENGTH + "}$")) {
            throw new IllegalArgumentException("Guess must be exactly " + WORD_LENGTH + " upper case letters (A-Z).");
        }
        if (session.getGuessCount() >= MAX_GUESSES) {
            throw new IllegalStateException("No more guesses allowed for this game.");
        }

        String target = session.getWord().getWordText();
        String[] pattern = WordMatcher.match(guessText, target);

        Guess guess = new Guess();
        guess.setGameSession(session);
        guess.setGuessText(guessText);
        guess.setGuessOrder(session.getGuessCount() + 1);
        guess.setPattern(String.join(",", pattern));
        guess.setSubmittedAt(LocalDateTime.now());
        guessRepository.save(guess);

        session.setGuessCount(session.getGuessCount() + 1);

        String message = null;
        if (guessText.equals(target)) {
            session.setStatus(GameStatus.WON);
            message = "Congratulations! You guessed the word correctly!";
        } else if (session.getGuessCount() >= MAX_GUESSES) {
            session.setStatus(GameStatus.LOST);
            message = "Better luck next time! The word was " + target + ".";
        }
        gameSessionRepository.save(session);

        List<Guess> allGuesses = guessRepository.findByGameSessionOrderByGuessOrderAsc(session);

        GuessResultResponse response = new GuessResultResponse();
        response.setGameId(session.getId());
        response.setPattern(pattern);
        response.setStatus(session.getStatus().name());
        response.setMessage(message);
        response.setGuessesRemaining(MAX_GUESSES - session.getGuessCount());
        response.setGuesses(allGuesses.stream().map(this::toGuessDto).collect(Collectors.toList()));
        return response;
    }

    private GameSession getOwnedSession(Long gameId, User user) {
        GameSession session = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found."));
        boolean isOwner = session.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("You do not have access to this game.");
        }
        return session;
    }

    private GameStateResponse toGameStateResponse(GameSession session, List<Guess> guesses) {
        GameStateResponse response = new GameStateResponse();
        response.setGameId(session.getId());
        response.setStatus(session.getStatus().name());
        response.setGuessesRemaining(MAX_GUESSES - session.getGuessCount());
        response.setGuesses(guesses.stream().map(this::toGuessDto).collect(Collectors.toList()));
        return response;
    }

    private GuessDto toGuessDto(Guess guess) {
        GuessDto dto = new GuessDto();
        dto.setGuessText(guess.getGuessText());
        dto.setPattern(guess.getPattern().split(","));
        return dto;
    }
}
