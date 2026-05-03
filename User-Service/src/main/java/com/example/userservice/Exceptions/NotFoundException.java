package com.example.userservice.exceptions;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: NotFoundException
 * DESCRIPTION:
 *   Custom runtime exception thrown when a requested user resource cannot be found
 *   within the User Service.
 */
public class NotFoundException extends RuntimeException {

    /* ================================================================
     * METHOD: NotFoundException
     * DESCRIPTION:
     *   Constructs a NotFoundException with the given error message.
     * ================================================================ */
    public NotFoundException(String message) {
        super(message);
    }
}
