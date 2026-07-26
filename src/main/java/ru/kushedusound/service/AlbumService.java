package ru.kushedusound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.dto.request.update.AlbumUpdateRequestDto;
import ru.kushedusound.entity.dto.response.AlbumResponseDto;
import ru.kushedusound.exeptions.AlbumFileDeleteException;
import ru.kushedusound.repository.AlbumRepository;
import ru.kushedusound.repository.TrackRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private  final TrackRepository trackRepository;

    @Value("${app.storage.tracks-path}")
    private String tracksPath;

    public AlbumResponseDto createAlbum(String title, Long artistId, LocalDate releaseDate) {
        Artist artist = artistService.getArtistById(artistId);

        Album album = new Album();
        album.setTitle(title);
        album.setArtist(artist);
        album.setReleaseDate(releaseDate);
        return AlbumResponseDto.from(albumRepository.save(album));
    }

    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getArtistAlbums(Long artistId) {
        return albumRepository.findByArtistId(artistId).
                stream().map(AlbumResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AlbumResponseDto> getAllAlbums() {
        return albumRepository.findAll().
                stream().map(AlbumResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Album getAlbumById(Long id) {
        return albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Альбом не найден, id = " + id));
    }

    public AlbumResponseDto getAlbumDtoById(Long id){
        return AlbumResponseDto.from(getAlbumById(id));
    }

    public AlbumResponseDto updateAlbum(Long id, AlbumUpdateRequestDto dto){
        Album album = getAlbumById(id);
        album.setArtist(artistService.getArtistById(dto.artistId()));
        album.setTitle(dto.title());
        album.setReleaseDate(dto.releaseDate());
        return AlbumResponseDto.from(albumRepository.save(album));
    }

    public void deleteAlbum(Long id){
        Album album = getAlbumById(id);
        albumRepository.delete(album);
        try{
            FileSystemUtils.deleteRecursively(
                    Path.of(tracksPath, String.valueOf(album.getArtist().getId()), String.valueOf(id))
            );
        } catch (IOException e) {
            throw new AlbumFileDeleteException(
                    "Не удалось удалить файл альбома: " + album.getTitle(), e
            );
        }
    }

}







