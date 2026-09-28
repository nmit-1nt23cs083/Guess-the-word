package com.wordgame.service;

import com.wordgame.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        userService = new UserService(userRepository, new BCryptPasswordEncoder());
    }

    @Test
    void rejectsUsernameShorterThanFiveLetters() {
        assertThrows(IllegalArgumentException.class, () -> userService.register("AbCd", "Passw0rd$"));
    }

    @Test
    void rejectsUsernameWithoutBothCases() {
        assertThrows(IllegalArgumentException.class, () -> userService.register("abcde", "Passw0rd$"));
        assertThrows(IllegalArgumentException.class, () -> userService.register("ABCDE", "Passw0rd$"));
    }

    @Test
    void acceptsValidUsername() {
        assertDoesNotThrow(() -> userService.register("AbCde", "Passw0rd$"));
    }

    @Test
    void rejectsPasswordWithoutSpecialCharacter() {
        assertThrows(IllegalArgumentException.class, () -> userService.register("AbCde", "Passw0rd"));
    }

    @Test
    void rejectsPasswordShorterThanFiveCharacters() {
        assertThrows(IllegalArgumentException.class, () -> userService.register("AbCde", "Ab1$"));
    }

    @Test
    void rejectsPasswordWithoutDigit() {
        assertThrows(IllegalArgumentException.class, () -> userService.register("AbCde", "Abcde$$"));
    }

    @Test
    void acceptsValidPassword() {
        assertDoesNotThrow(() -> userService.register("AbCde", "Ab1$de"));
    }

    @Test
    void acceptsOtherCharactersWhenRequiredPasswordCharactersArePresent() {
        assertDoesNotThrow(() -> userService.register("AbCde", "Ab1$de!"));
    }

    @Test
    void rejectsDuplicateUsername() {
        Mockito.when(userRepository.existsByUsername("AbCde")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.register("AbCde", "Ab1$de"));
    }
}
