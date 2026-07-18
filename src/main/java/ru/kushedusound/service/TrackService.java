package ru.kushedusound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.Track;
import ru.kushedusound.entity.User;
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

    public Track uploadTrack(MultipartFile file, String title, Long artistId, Long albumId) throws IOException {
        Artist artist = artistService.getArtistById(artistId);
        Album album = (albumId != null)? albumService.getAlbumById(albumId) : null;

        Path storageDir = buildStorageDir(artistId, albumId);
        Files.createDirectories(storageDir);

        String extension = getExtension(file.getOriginalFilename());
        String generatedFileName = UUID.randomUUID() + extension;
        Path targetPath = storageDir.resolve(generatedFileName);

        file.transferTo(targetPath);

        User defUser = userRepository.findByUsername(startUser)
                .orElseThrow(() -> new IllegalStateException("Заглушка-юзер не найдена — проверь DataInitializer"));
        Track track = new Track();
        track.setName(title);
        track.setArtist(artist);
        track.setAlbum(album);
        track.setFilePath(targetPath.toString());
        track.setFileSizeBytes(file.getSize());
        track.setUploadedBy(defUser);
        track.setCreatedAt(LocalDateTime.now());
        return trackRepository.save(track);
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Track getTrackById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Трек не найден: id=" + id));
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