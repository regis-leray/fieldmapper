package com.rayjars.fieldmapper;

/**
 * Created by regis on 28/04/14.
 */
public class InvalidFieldValueException extends InvalidFieldException {
    public InvalidFieldValueException(String message) {
        super(message);
    }

    public InvalidFieldValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldValueException(Throwable cause) {
        super(cause);
    }
}
