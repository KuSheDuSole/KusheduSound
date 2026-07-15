package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kushedusound.entity.Track;

public interface TrackRepository extends JpaRepository<Track, Long> {
}
