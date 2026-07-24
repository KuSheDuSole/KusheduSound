package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.request.create.ArtistCreateRequestDto;
import ru.kushedusound.entity.dto.request.update.ArtistUpdateRequestDto;
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
    public ResponseEntity<ArtistResponseDto> createArtist(@RequestBody ArtistCreateRequestDto request){
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

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> updateArtist(
            @PathVariable Long id,
            @RequestBody ArtistUpdateRequestDto dto){
        return ResponseEntity.ok(artistService.updateArtist(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id){
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

}








