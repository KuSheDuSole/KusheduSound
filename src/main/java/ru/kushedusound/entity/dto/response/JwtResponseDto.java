package ru.kushedusound.entity.dto.response;

public record JwtResponseDto(
        String accessToken,
        String refreshToken
) {
}
