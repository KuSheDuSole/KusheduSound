package ru.kushedusound.entity.dto.request.update;

import java.time.LocalDateTime;

public record TrackUpdateRequestDto(
        String title,
        Long artistId,
        Long albumId,
        LocalDateTime createdAt
) {
}
