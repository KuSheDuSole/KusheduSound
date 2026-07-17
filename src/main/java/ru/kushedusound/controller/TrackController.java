package ru.kushedusound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kushedusound.entity.Track;
import ru.kushedusound.service.TrackService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<Track> uploadTrack(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("artist") String artist
    ) throws IOException {
        Track track = trackService.uploadTrack(file, name, artist);
        return ResponseEntity.ok(track);
    }

    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        return ResponseEntity.ok(trackService.getAllTracks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrack(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTrackById(id));
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
}











