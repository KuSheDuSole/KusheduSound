package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kushedusound.entity.Album;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtistId(Long artistId);
}