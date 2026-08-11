package ru.kushedusound.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kushedusound.entity.Playlist;
import ru.kushedusound.entity.PlaylistTrack;
import ru.kushedusound.entity.Track;
import ru.kushedusound.entity.dto.request.create.PlaylistCreateDto;
import ru.kushedusound.entity.dto.response.PlaylistResponseDto;
import ru.kushedusound.entity.dto.response.TrackResponseDto;
import ru.kushedusound.repository.PlaylistRepository;
import ru.kushedusound.repository.PlaylistTrackRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final UserService userService;
    private final TrackService trackService;

    public PlaylistResponseDto createPlaylist(Long userId, PlaylistCreateDto dto){
        Playlist playlist = new Playlist();
        playlist.setTitle(dto.title());
        playlist.setUser(userService.getUserById(userId));
        return PlaylistResponseDto.from(playlistRepository.save(playlist));
    }

    @Transactional(readOnly = true)
    public List<PlaylistResponseDto> getUserPlaylists(Long userId){
        return playlistRepository.findByUserId(userId)
                .stream().map(PlaylistResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Playlist getPlaylistById(Long id){
        return playlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден, id = " + id));
    }

    @Transactional(readOnly = true)
    public List<TrackResponseDto> getPlaylistTracks(Long playlistId){
        return playlistTrackRepository.findByPlaylistIdOrderByPosition(playlistId)
                .stream().map(track -> TrackResponseDto.from(track.getTrack())).toList();
    }

    public void addTrackToPlaylist(Long playlistId, Long trackId){
        Playlist playlist = getPlaylistById(playlistId);
        Track track = trackService.getTrackById(trackId);
        int nextPosition = playlistTrackRepository.countByPlaylistId(playlistId);
        playlistTrackRepository.save(new PlaylistTrack(playlist, track, nextPosition));
    }

    public void deleteTrackFromPlaylist(Long playlistId, Long trackId){
        playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId);
    }

    public void deletePlaylist(Long id){
        Playlist playlist = getPlaylistById(id);
        playlistRepository.delete(playlist);
    }
}