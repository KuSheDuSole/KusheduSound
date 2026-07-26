package ru.kushedusound.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.Track;
import ru.kushedusound.entity.User;
import ru.kushedusound.entity.dto.request.create.TrackUploadRequestDto;
import ru.kushedusound.entity.dto.request.update.TrackUpdateRequestDto;
import ru.kushedusound.entity.dto.response.TrackResponseDto;
import ru.kushedusound.exeptions.TrackFileDeletionException;
import ru.kushedusound.repository.TrackRepository;
import ru.kushedusound.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final ArtistService artistService;
    private final AlbumService albumService;

    @Value("${app.storage.tracks-path}")
    private String tracksPath;

    @Value("${app.test-user}")
    private String startUser;

    public TrackResponseDto uploadTrack(MultipartFile file, TrackUploadRequestDto dto) throws IOException {
        Artist artist = artistService.getArtistById(dto.artistId());
        Album album = (dto.albumId() != null) ? albumService.getAlbumById(dto.albumId()) : null;

        Path storageDir = buildStorageDir(dto.artistId(), dto.albumId());
        Files.createDirectories(storageDir);

        String extension = getExtension(file.getOriginalFilename());
        String generatedFileName = UUID.randomUUID() + extension;
        Path targetPath = storageDir.resolve(generatedFileName);

        file.transferTo(targetPath);

        User defUser = userRepository.findByUsername(startUser)
                .orElseThrow(() -> new IllegalStateException("Заглушка-юзер не найдена — проверь DataInitializer"));
        Track track = new Track();
        track.setTitle(dto.title());
        track.setArtist(artist);
        track.setAlbum(album);
        track.setFilePath(targetPath.toString());
        track.setFileSizeBytes(file.getSize());
        track.setUploadedBy(defUser);
        track.setCreatedAt(LocalDateTime.now());
        return TrackResponseDto.from(trackRepository.save(track));
    }

    @Transactional(readOnly = true)
    public List<TrackResponseDto> getAllTracks() {
        return trackRepository.findAll()
                .stream().map(TrackResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Track getTrackById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Трек не найден: id=" + id));
    }


    public TrackResponseDto getTrackDtoById(Long id){
        return TrackResponseDto.from(getTrackById(id));
    }

    public TrackResponseDto updateTrack(Long id, TrackUpdateRequestDto dto){
        Track track = getTrackById(id);
        Album album = (dto.albumId() != null) ? albumService.getAlbumById(dto.albumId()) : null;
        track.setTitle(dto.title());
        track.setArtist(artistService.getArtistById(dto.artistId()));
        track.setAlbum(album);
        track.setCreatedAt(dto.createdAt());
        return TrackResponseDto.from(trackRepository.save(track));
    }

    public void deleteTrack(Long id){
        Track track = getTrackById(id);
        trackRepository.delete(track);
        trackRepository.flush();
        try {
            FileSystemUtils.deleteRecursively(Path.of(track.getFilePath()));
        } catch (IOException e) {
            throw new TrackFileDeletionException(
                    "Не удалось удалить файл трека: " + track.getFilePath(), e
            );
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }

    private Path buildStorageDir(Long artistId, Long albumId) {
        Path base = Path.of(tracksPath, String.valueOf(artistId));
        return (albumId != null)
                ? base.resolve(String.valueOf(albumId))
                : base.resolve("singles");
    }
}