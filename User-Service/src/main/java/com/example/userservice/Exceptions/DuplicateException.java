package com.example.userservice.exceptions;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: DuplicateException
 * DESCRIPTION:
 *   Custom runtime exception thrown when a duplicate resource (email or phone number)
 *   is detected during user registration or profile update.
 */
public class DuplicateException extends RuntimeException {

    /* ================================================================
     * METHOD: DuplicateException
     * DESCRIPTION:
     *   Constructs a DuplicateException with the given error message.
     * ================================================================ */
    public DuplicateException(String message) {
        super(message);
    }
}
