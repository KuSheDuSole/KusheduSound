package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kushedusound.entity.FavoriteTrack;

import java.util.List;

public interface FavouriteTrackRepository extends JpaRepository<FavoriteTrack, Long> {
    List<FavoriteTrack> findByUserId(Long id);

    boolean existsByUserIdAndTrackId(Long userId, Long trackId);

    void deleteByUserIdAndTrackId(Long userID, Long trackID);
}
