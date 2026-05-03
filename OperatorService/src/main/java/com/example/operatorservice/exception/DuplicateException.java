package com.example.operatorservice.exception;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: DuplicateException
 * DESCRIPTION:
 *   Custom runtime exception thrown when a duplicate resource (operator or plan)
 *   is detected within the Operator Service.
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
