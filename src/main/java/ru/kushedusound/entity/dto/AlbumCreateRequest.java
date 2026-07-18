package ru.kushedusound.entity.dto;

import java.time.LocalDate;

public record AlbumCreateRequest(String title, Long artistId, LocalDate releaseDate) {
}
