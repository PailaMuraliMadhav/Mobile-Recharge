package com.example.userservice.exceptions;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: InvalidDataException
 * DESCRIPTION:
 *   Custom runtime exception thrown when invalid credentials or data are provided,
 *   such as an incorrect password during login.
 */
public class InvalidDataException extends RuntimeException {

    /* ================================================================
     * METHOD: InvalidDataException
     * DESCRIPTION:
     *   Constructs an InvalidDataException with the given error message.
     * ================================================================ */
    public InvalidDataException(String message) {
        super(message);
    }
}
