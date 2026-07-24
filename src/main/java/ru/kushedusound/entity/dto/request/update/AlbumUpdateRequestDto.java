package ru.kushedusound.entity.dto.request.update;

import java.time.LocalDate;

public record AlbumUpdateRequestDto(
        String title,
        Long artistId,
        LocalDate releaseDate
) {

}
