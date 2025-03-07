package org.ukdw.exception;

public class RequestParameterErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestParameterErrorException(String message) {
        super(message);
    }

    public RequestParameterErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
