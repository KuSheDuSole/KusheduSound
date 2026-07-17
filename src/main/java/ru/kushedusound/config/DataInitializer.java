package ru.kushedusound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kushedusound.entity.User;
import ru.kushedusound.repository.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Value("${app.test-user}")
    private String DEF_USERNAME;

    @Override
    public void run(String... args){
        if (userRepository.findByUsername(DEF_USERNAME).isEmpty()){
            User defUser = new User(
                    DEF_USERNAME,
                    "def@kushedusound.ru",
                    "not_read_hash_password",
                    LocalDateTime.now()
            );
            userRepository.save(defUser);
        }
    }
}
