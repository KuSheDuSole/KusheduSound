package ru.kushedusound.exeptions;

public class ArtistFileDeleteException extends RuntimeException {
    public ArtistFileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
