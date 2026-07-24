package ru.kushedusound.entity.dto.request.create;

public record TrackUploadRequest(String title, Long artistId, Long albumId) {
}
