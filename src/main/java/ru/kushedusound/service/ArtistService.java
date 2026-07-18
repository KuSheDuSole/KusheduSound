package ru.kushedusound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.Artist;
import ru.kushedusound.entity.dto.response.ArtistResponseDto;
import ru.kushedusound.repository.ArtistRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistResponseDto createArtist(String name, String bio){
        Artist artist = new Artist();
        artist.setName(name);
        artist.setBio(bio);
        return ArtistResponseDto.from(artistRepository.save(artist));
    }

    public List<ArtistResponseDto> getAllArtists(){ return artistRepository.findAll()
            .stream().map(ArtistResponseDto::from).toList(); }

    public ArtistResponseDto getArtistDtoById(Long id) {
        return ArtistResponseDto.from(getArtistById(id));
    }


    public Artist getArtistById(Long id){
        return artistRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Артист не найден, id = " + id));
    }


}