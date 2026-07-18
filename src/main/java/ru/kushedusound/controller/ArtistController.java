package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.ArtistCreateRequest;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.Artist;
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
    public ResponseEntity<Artist> createArtist(@RequestBody ArtistCreateRequest request){
        Artist artist = artistService.createArtist(request.name(), request.bio());
        return ResponseEntity.ok(artist);
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists(){
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtist(@PathVariable Long id){
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<List<Album>> getArtistAlbums(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getArtistAlbums(id));
    }
}