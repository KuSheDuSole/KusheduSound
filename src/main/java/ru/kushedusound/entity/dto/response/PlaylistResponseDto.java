package ru.kushedusound.entity.dto.response;

import ru.kushedusound.entity.Playlist;

import java.time.LocalDateTime;

public record PlaylistResponseDto(
        Long id,
        String title,
        Long userId,
        String username,
        LocalDateTime createdAt
) {
    public static PlaylistResponseDto from(Playlist playlist){
        return new PlaylistResponseDto(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getUser().getId(),
                playlist.getUser().getUsername(),
                playlist.getCreatedAt()
        );
    }
}
