package me.ericfu.album.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PhotoException extends RuntimeException {

    public PhotoException(String message) {
        super(message);
    }

    public PhotoException(String message, Throwable cause) {
        super(message, cause);
    }
}
