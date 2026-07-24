package ru.kushedusound.exeptions;

public class AlbumFileDeleteException extends RuntimeException {
    public AlbumFileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
