package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.request.create.AlbumCreateRequestDto;
import ru.kushedusound.entity.dto.request.update.AlbumUpdateRequestDto;
import ru.kushedusound.entity.dto.response.AlbumResponseDto;
import ru.kushedusound.service.AlbumService;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumResponseDto> createAlbum(@RequestBody AlbumCreateRequestDto request){
        AlbumResponseDto album = albumService.createAlbum(request.title(), request.artistId(), request.releaseDate());
        return ResponseEntity.ok(album);
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDto>> getAllAlbums(){
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getAlbum(@PathVariable Long id){
        return ResponseEntity.ok(albumService.getAlbumDtoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updateAlbum(
            @PathVariable Long id,
            @RequestBody AlbumUpdateRequestDto request){
        return ResponseEntity.ok(albumService.updateAlbum(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id){
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
