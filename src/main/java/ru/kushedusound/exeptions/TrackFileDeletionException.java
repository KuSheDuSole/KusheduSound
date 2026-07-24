package ru.kushedusound.exeptions;

public class TrackFileDeletionException extends RuntimeException {
    public TrackFileDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
