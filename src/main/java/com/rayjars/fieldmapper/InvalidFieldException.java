package com.rayjars.fieldmapper;

/**
 * Created by regis on 23/04/14.
 */
public class InvalidFieldException extends Exception {

    public InvalidFieldException(String message) {
        super(message);
    }

    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldException(Throwable cause) {
        super(cause);
    }
}
