package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kushedusound.entity.Playlist;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(Long userId);

    @Query("select p.user.id from Playlist p where p.id = :id")
    Optional<Long> findOwnerIdById(@Param("id") Long id);
}