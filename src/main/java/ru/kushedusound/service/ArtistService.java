package ru.kushedusound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.repository.ArtistRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {
    private final ArtistRepository artistRepository;

    public Artist createArtist(String name, String bio){
        Artist artist = new Artist();
        artist.setName(name);
        artist.setBio(bio);
        return artistRepository.save(artist);
    }

    public List<Artist> getAllArtists(){ return artistRepository.findAll(); }

    public Artist getArtistById(Long id){
        return artistRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Артист не найден, id = " + id));
    }


}