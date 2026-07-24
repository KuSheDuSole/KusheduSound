package ru.kushedusound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.dto.request.update.ArtistUpdateRequestDto;
import ru.kushedusound.entity.dto.response.ArtistResponseDto;
import ru.kushedusound.exeptions.ArtistFileDeleteException;
import ru.kushedusound.repository.AlbumRepository;
import ru.kushedusound.repository.ArtistRepository;
import ru.kushedusound.repository.TrackRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;

    @Value("${app.storage.tracks-path}")
    private String tracksPath;

    public ArtistResponseDto createArtist(String name, String bio){
        Artist artist = new Artist();
        artist.setName(name);
        artist.setBio(bio);
        return ArtistResponseDto.from(artistRepository.save(artist));
    }

    @Transactional(readOnly = true)
    public List<ArtistResponseDto> getAllArtists(){ return artistRepository.findAll()
            .stream().map(ArtistResponseDto::from).toList(); }

    @Transactional(readOnly = true)
    public Artist getArtistById(Long id){
        return artistRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Артист не найден, id = " + id));
    }

    public ArtistResponseDto getArtistDtoById(Long id) {
        return ArtistResponseDto.from(getArtistById(id));
    }

    public ArtistResponseDto updateArtist(Long id, ArtistUpdateRequestDto dto){
        Artist artist = getArtistById(id);
        artist.setName(dto.name());
        artist.setBio(dto.bio());
        return ArtistResponseDto.from(artistRepository.save(artist));
    }

    public void deleteArtist(Long id){
        Artist artist = getArtistById(id);
        trackRepository.deleteByArtistId(id);
        albumRepository.deleteByArtistId(id);
        artistRepository.delete(artist);
        try{
            FileSystemUtils.deleteRecursively(
                    Path.of(tracksPath, String.valueOf(artist.getId()))
            );
        } catch (IOException e) {
            throw new ArtistFileDeleteException(
                    "Не удалось удалить файл артиста: " + artist.getName(), e
            );
        }
    }
}







