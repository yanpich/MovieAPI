package com.movieflix.movieapi.exceptions;

public class EmptyFileException extends RuntimeException {

    public EmptyFileException(String message) {
        super(message);
    }
}