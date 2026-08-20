package ru.kushedusound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kushedusound.entity.Role;
import ru.kushedusound.entity.User;
import ru.kushedusound.repository.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-admin.email}")
    private String DEF_EMAIL;

    @Value("${app.base-admin.password}")
    private String DEF_PASSWORD;

    @Override
    public void run(String... args){
        if (userRepository.findByEmail(DEF_EMAIL).isEmpty()){
            User defUser = new User(
                    "admin",
                    DEF_EMAIL,
                    passwordEncoder.encode(DEF_PASSWORD),
                    LocalDateTime.now(),
                    Role.ADMIN
            );
            userRepository.save(defUser);
        }
    }
}
