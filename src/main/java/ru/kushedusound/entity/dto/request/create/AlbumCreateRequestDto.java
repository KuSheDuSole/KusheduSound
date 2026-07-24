package ru.kushedusound.entity.dto.request.create;

import java.time.LocalDate;

public record AlbumCreateRequestDto(String title, Long artistId, LocalDate releaseDate) {
}
