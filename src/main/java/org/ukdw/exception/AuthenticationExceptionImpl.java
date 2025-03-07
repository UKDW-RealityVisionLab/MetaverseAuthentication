package org.ukdw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthenticationExceptionImpl extends AuthenticationException {

    public AuthenticationExceptionImpl(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationExceptionImpl(String msg) {
        super(msg);
    }
}