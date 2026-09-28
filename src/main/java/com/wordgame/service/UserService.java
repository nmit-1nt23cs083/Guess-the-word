package com.wordgame.service;

import com.wordgame.model.Role;
import com.wordgame.model.User;
import com.wordgame.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    // At least 5 letters, only letters, and must contain both an upper case
    // and a lower case letter somewhere in it.
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])[A-Za-z]{5,}$");

        // At least 5 characters, must contain a letter, a digit, and one of $ % *.
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$%*]).{5,}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Registers a new PLAYER account. Admin accounts are seeded, not self-registered. */
    public User register(String username, String rawPassword) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                    "Username must be at least 5 letters long and contain both upper case and lower case letters.");
        }
        if (rawPassword == null || !PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 5 characters long and contain a letter, a digit, and one of $ % *.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("That username is already taken.");
        }

        User user = new User(username, passwordEncoder.encode(rawPassword), Role.PLAYER);
        return userRepository.save(user);
    }
}
