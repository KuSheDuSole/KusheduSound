package ru.kushedusound.entity.dto.response;

import ru.kushedusound.entity.Album;

import java.time.LocalDate;

public record AlbumResponseDto(
    Long id,
    String title,
    Long artistId,
    String artistName,
    LocalDate releaseDate
) {
    public static AlbumResponseDto from(Album album){
        return new AlbumResponseDto(
                album.getId(),
                album.getTitle(),
                album.getArtist().getId(),
                album.getArtist().getName(),
                album.getReleaseDate()
        );
    }
}
