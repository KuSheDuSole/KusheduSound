package ru.kushedusound.entity.dto.request.create;

import java.time.LocalDate;

public record AlbumCreateRequest(String title, Long artistId, LocalDate releaseDate) {
}
