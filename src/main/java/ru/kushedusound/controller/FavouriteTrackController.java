package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.response.TrackResponseDto;
import ru.kushedusound.service.FavouriteTrackService;

import java.util.List;

@Controller
@RequestMapping("/users/{userId}/favourites")
@RequiredArgsConstructor
public class FavouriteTrackController {
    private final FavouriteTrackService favouriteTrackService;

    @PostMapping("/{trackId}")
    public ResponseEntity<Void> addFavourite(@PathVariable Long userId, @PathVariable Long trackId){
        favouriteTrackService.addFavourite(userId, trackId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable Long userId, @PathVariable Long trackId){
        favouriteTrackService.deleteFavourite(userId, trackId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TrackResponseDto>> getFavourites(@PathVariable Long userId){
        return ResponseEntity.ok(favouriteTrackService.getUserFavourites(userId));
    }
}
