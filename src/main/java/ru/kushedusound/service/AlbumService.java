package ru.kushedusound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.repository.AlbumRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;

    public Album createAlbum(String title, Long artistId, LocalDate releaseDate){
        Artist artist = artistService.getArtistById(artistId);

        Album album = new Album();
        album.setTitle(title);
        album.setArtist(artist);
        album.setReleaseDate(releaseDate);
        return albumRepository.save(album);
    }

    public List<Album> getArtistAlbums(Long artistId){
        return albumRepository.findByArtistId(artistId);
    }

    public List<Album> getAllAlbums(){
        return albumRepository.findAll();
    }

    public Album getAlbumById(Long id){
        return albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Альом не найден, id = " + id));
    }
}