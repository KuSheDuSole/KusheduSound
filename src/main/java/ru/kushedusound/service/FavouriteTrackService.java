package ru.kushedusound.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kushedusound.entity.FavoriteTrack;
import ru.kushedusound.entity.Track;
import ru.kushedusound.entity.User;
import ru.kushedusound.entity.dto.response.TrackResponseDto;
import ru.kushedusound.repository.FavouriteTrackRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavouriteTrackService {
    private final FavouriteTrackRepository favouriteTrackRepository;
    private final UserService userService;
    private final TrackService trackService;

    public void addFavourite(Long userId, Long trackId){
        if (favouriteTrackRepository.existsByUserIdAndTrackId(userId, trackId)){
            throw new IllegalStateException("Трек уже в избранном");
        }
        User user = userService.getUserById(userId);
        Track track = trackService.getTrackById(trackId);
        favouriteTrackRepository.save(new FavoriteTrack(user, track));
    }

    public void deleteFavourite(Long userId, Long trackId){
        favouriteTrackRepository.deleteByUserIdAndTrackId(userId, trackId);
    }

    @Transactional(readOnly = true)
    public List<TrackResponseDto> getUserFavourites(Long userId){
        return favouriteTrackRepository.findByUserId(userId)
                .stream().map(fav -> TrackResponseDto.from(fav.getTrack())).toList();
    }

}