package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.AlbumCreateRequest;
import ru.kushedusound.entity.Album;
import ru.kushedusound.entity.dto.response.AlbumResponseDto;
import ru.kushedusound.service.AlbumService;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumResponseDto> createAlbum(@RequestBody AlbumCreateRequest request){
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
}
