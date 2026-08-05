package ru.kushedusound.controller.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kushedusound.entity.dto.request.create.TrackUploadRequestDto;
import ru.kushedusound.entity.Track;
import ru.kushedusound.entity.dto.request.update.TrackUpdateRequestDto;
import ru.kushedusound.entity.dto.response.TrackResponseDto;
import ru.kushedusound.service.TrackService;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackResponseDto> uploadTrack(
            @RequestPart("file") MultipartFile file,
            @RequestParam("data") String dataJson
            ) throws IOException {
        TrackUploadRequestDto data = objectMapper.readValue(dataJson, TrackUploadRequestDto.class);
        TrackResponseDto track = trackService.uploadTrack(file, data);
        return ResponseEntity.ok(track);
    }

    @GetMapping
    public ResponseEntity<List<TrackResponseDto>> getAllTracks() {
        return ResponseEntity.ok(trackService.getAllTracks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponseDto> getTrack(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTrackDtoById(id));
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<ResourceRegion> streamTrack(@PathVariable Long id, @RequestHeader HttpHeaders headers)
        throws IOException{

        Track track = trackService.getTrackById(id);
        Path path = Path.of(track.getFilePath());
        UrlResource resource = new UrlResource(path.toUri());

        long contentLength = resource.contentLength();
        List<HttpRange> ranges = headers.getRange();

        String mimeType = Files.probeContentType(path);
        if (mimeType == null) mimeType = "audio/mpeg";

        if (ranges.isEmpty()){
            ResourceRegion region = new ResourceRegion(resource, 0, contentLength);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(region);
        }

        HttpRange range = ranges.getFirst();
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = end - start + 1;

        ResourceRegion region = new ResourceRegion(resource, start, rangeLength);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackResponseDto> updateTrack(
            @PathVariable Long id,
            @RequestBody TrackUpdateRequestDto dto){
        return ResponseEntity.ok(trackService.updateTrack(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id){
        trackService.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }
}











