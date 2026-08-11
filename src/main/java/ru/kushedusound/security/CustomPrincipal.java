package ru.kushedusound.security;

public record CustomPrincipal(
        Long id,
        String email,
        String username
) {
}
