package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.ArtistCreateRequest;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.dto.response.AlbumResponseDto;
import ru.kushedusound.entity.dto.response.ArtistResponseDto;
import ru.kushedusound.service.AlbumService;
import ru.kushedusound.service.ArtistService;

import java.util.List;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<ArtistResponseDto> createArtist(@RequestBody ArtistCreateRequest request){
        ArtistResponseDto artist = artistService.createArtist(request.name(), request.bio());
        return ResponseEntity.ok(artist);
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponseDto>> getAllArtists(){
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> getArtist(@PathVariable Long id){
        return ResponseEntity.ok(artistService.getArtistDtoById(id));
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<List<AlbumResponseDto>> getArtistAlbums(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getArtistAlbums(id));
    }
}