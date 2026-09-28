package com.wordgame.config;

import com.wordgame.model.Role;
import com.wordgame.model.User;
import com.wordgame.model.Word;
import com.wordgame.repository.UserRepository;
import com.wordgame.repository.WordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database on first startup with:
 *   - the twenty 5-letter words the game picks from
 *   - a default admin account (username: AdminUser / password: Admin1$23)
 *
 * Nothing is re-seeded on subsequent restarts if data already exists.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final List<String> SEED_WORDS = List.of(
            "AUDIO", "HOMER", "JOKER", "TONER", "TOWER",
            "PLANE", "GRAPE", "STONE", "CRANE", "LEMON",
            "MANGO", "RIVER", "TIGER", "HOUSE", "MOUSE",
            "BREAD", "CHESS", "EARTH", "SOUND", "LIGHT"
    );

    private static final String DEFAULT_ADMIN_USERNAME = "AdminUser";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin1$23";

    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(WordRepository wordRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (wordRepository.count() == 0) {
            SEED_WORDS.forEach(w -> wordRepository.save(new Word(w)));
            System.out.println("Seeded " + SEED_WORDS.size() + " words.");
        }

        if (userRepository.findByUsername(DEFAULT_ADMIN_USERNAME).isEmpty()) {
            User admin = new User(DEFAULT_ADMIN_USERNAME, passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD), Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Seeded default admin user: " + DEFAULT_ADMIN_USERNAME + " / " + DEFAULT_ADMIN_PASSWORD);
        }
    }
}
