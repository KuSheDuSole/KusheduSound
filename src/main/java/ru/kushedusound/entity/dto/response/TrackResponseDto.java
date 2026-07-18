package ru.kushedusound.entity.dto.response;

import ru.kushedusound.entity.Track;

import java.time.LocalDateTime;

public record TrackResponseDto (
        Long id,
        String title,
        Long artistId,
        String artistName,
        Long albumId,
        String albumName,
        String streamUrl,
        Integer durationSeconds,
        Long uploadedUserId,
        String uploadedUserName,
        LocalDateTime createdAt
){
    public static TrackResponseDto from(Track track){
        return new TrackResponseDto(
                track.getId(),
                track.getTitle(),
                track.getArtist().getId(),
                track.getArtist().getName(),
                track.getAlbum() != null ? track.getAlbum().getId() : null,
                track.getAlbum() != null ? track.getAlbum().getTitle() : null,
                "/tracks/" + track.getId() + "/stream",
                track.getDurationSeconds(),
                track.getUploadedBy().getId(),
                track.getUploadedBy().getUsername(),
                track.getCreatedAt()
        );
    }
}