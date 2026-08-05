package ru.kushedusound.service.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.User;
import ru.kushedusound.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с таким username не найден, username = " + username)
        );
        return org.springframework.security.core.userdetails
                .User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(user.getRole().toAuthority()))
                .build();
    }

}
