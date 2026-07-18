package ru.kushedusound.entity.dto.response;

import ru.kushedusound.entity.Artist;

public record ArtistResponseDto(
    Long id,
    String name,
    String bio
) {
    public static ArtistResponseDto from(Artist artist) {
        return new ArtistResponseDto(
                artist.getId(),
                artist.getName(),
                artist.getBio()
        );
    }
}
