package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kushedusound.entity.PlaylistTrack;

import java.util.List;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {
    List<PlaylistTrack> findByPlaylistIdOrderByPosition(Long playlistId);

    int countByPlaylistId(Long playlistId);

    void deleteByPlaylistIdAndTrackId(Long playlistId, Long trackId);
}