package com.realtap.phone.exceptions;

public class PhoneNumberParsingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PhoneNumberParsingException() {
    }

    /*public PhoneNumberParsingException(Throwable cause) {
        super(cause instanceof ServerException ? cause.getCause() : cause);
    } */

    public PhoneNumberParsingException(Throwable cause) {
        super(cause.getCause());
    }

    public PhoneNumberParsingException(String message) {
        super(message);
    }

    public PhoneNumberParsingException(String message, Throwable cause) {
        super(message, cause instanceof PhoneNumberParsingException ? cause.getCause() : cause);
    }

}