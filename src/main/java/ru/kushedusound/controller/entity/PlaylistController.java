package ru.kushedusound.controller.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.request.create.PlaylistCreateDto;
import ru.kushedusound.entity.dto.response.PlaylistResponseDto;
import ru.kushedusound.security.CustomPrincipal;
import ru.kushedusound.service.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping()
    public ResponseEntity<PlaylistResponseDto> createPlaylist(
            @RequestBody PlaylistCreateDto dto,
            @AuthenticationPrincipal CustomPrincipal principal){

        Long userId = principal.id();
        return ResponseEntity.ok(playlistService.createPlaylist(userId, dto));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PlaylistResponseDto>> getMyPlaylist(@AuthenticationPrincipal CustomPrincipal principal){
        Long userId = principal.id();
        return ResponseEntity.ok(playlistService.getUserPlaylists(userId));
    }

    @PostMapping("/{playlistId}/track/{trackId}")
    @PreAuthorize("hasRole('ADMIN') or @playlistService.isOwner(#playlistId, authentication.principal.id())")
    public ResponseEntity<Void> addTrackToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId
    ){
        playlistService.addTrackToPlaylist(playlistId, trackId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{playlistId}/track/{trackId}")
    @PreAuthorize("hasRole('ADMIN') or @playlistService.isOwner(#playlistId, authentication.principal.id())")
    public ResponseEntity<Void> deleteTrackFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId
    ){
        playlistService.deleteTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{playlistId}")
    @PreAuthorize("hasRole('ADMIN') or @playlistService.isOwner(#playlistId, authentication.principal.id())")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId){
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.noContent().build();
    }
}
