package ru.kushedusound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.dto.response.AlbumResponseDto;
import ru.kushedusound.repository.AlbumRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;

    public AlbumResponseDto createAlbum(String title, Long artistId, LocalDate releaseDate) {
        Artist artist = artistService.getArtistById(artistId);

        Album album = new Album();
        album.setTitle(title);
        album.setArtist(artist);
        album.setReleaseDate(releaseDate);
        return AlbumResponseDto.from(albumRepository.save(album));
    }

    public List<AlbumResponseDto> getArtistAlbums(Long artistId) {
        return albumRepository.findByArtistId(artistId).
                stream().map(AlbumResponseDto::from).toList();
    }

    public List<AlbumResponseDto> getAllAlbums() {
        return albumRepository.findAll().
                stream().map(AlbumResponseDto::from).toList();
    }

    public Album getAlbumById(Long id) {
        return albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Альбом не найден, id = " + id));
    }

    public AlbumResponseDto getAlbumDtoById(Long id){
        return AlbumResponseDto.from(getAlbumById(id));
    }
}







