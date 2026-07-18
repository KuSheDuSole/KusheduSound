package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kushedusound.entity.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

}
