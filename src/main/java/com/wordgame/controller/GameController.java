package com.wordgame.controller;

import com.wordgame.dto.GameStateResponse;
import com.wordgame.dto.GuessRequest;
import com.wordgame.dto.GuessResultResponse;
import com.wordgame.model.User;
import com.wordgame.repository.UserRepository;
import com.wordgame.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final UserRepository userRepository;

    public GameController(GameService gameService, UserRepository userRepository) {
        this.gameService = gameService;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }

    @PostMapping("/start")
    public ResponseEntity<GameStateResponse> start(Authentication authentication) {
        return ResponseEntity.ok(gameService.startGame(currentUser(authentication)));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateResponse> getState(@PathVariable Long gameId, Authentication authentication) {
        return ResponseEntity.ok(gameService.getState(gameId, currentUser(authentication)));
    }

    @PostMapping("/{gameId}/guess")
    public ResponseEntity<GuessResultResponse> guess(@PathVariable Long gameId,
                                                       @RequestBody GuessRequest request,
                                                       Authentication authentication) {
        String guessText = request.getGuess() == null ? null : request.getGuess().trim().toUpperCase();
        return ResponseEntity.ok(gameService.submitGuess(gameId, currentUser(authentication), guessText));
    }
}
