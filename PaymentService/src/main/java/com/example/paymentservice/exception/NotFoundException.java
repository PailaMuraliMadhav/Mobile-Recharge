package com.example.paymentservice.exception;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: NotFoundException
 * DESCRIPTION:
 *   Custom runtime exception thrown when a requested payment resource cannot be found
 *   within the Payment Service.
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
