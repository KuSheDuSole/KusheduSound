package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.AlbumCreateRequest;
import ru.kushedusound.entity.Album;
import ru.kushedusound.service.AlbumService;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestBody AlbumCreateRequest request){
        Album album = albumService.createAlbum(request.title(), request.artistId(), request.releaseDate());
        return ResponseEntity.ok(album);
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums(){
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }
}
