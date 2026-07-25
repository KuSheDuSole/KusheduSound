package ru.kushedusound.entity.dto.request.update;

public record UserUpdateRequestDto(
        String username,
        String email
) {
}
