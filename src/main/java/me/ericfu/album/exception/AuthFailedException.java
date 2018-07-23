package me.ericfu.album.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthFailedException extends RuntimeException {

    public AuthFailedException(String message) {
        super(message);
    }

}
