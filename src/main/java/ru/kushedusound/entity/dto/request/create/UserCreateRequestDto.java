package ru.kushedusound.entity.dto.request.create;

public record UserCreateRequestDto(
        String username,
        String email,
        String password
) {
}
