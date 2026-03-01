package com.movieflix.movieapi.exceptions;

public class FileExistException extends RuntimeException {
    public FileExistException(String message) {
        super(message);
    }
}
