package org.ukdw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ScNotFoundException extends RuntimeException{

    public ScNotFoundException(String message) {
        super(message);
    }

    public ScNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}