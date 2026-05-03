package com.example.rechargeservice.exceptions;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: BadRequestException
 * DESCRIPTION:
 *   Custom runtime exception thrown when a client request contains invalid or
 *   unprocessable data within the Recharge Service.
 */
public class BadRequestException extends RuntimeException {

    /* ================================================================
     * METHOD: BadRequestException
     * DESCRIPTION:
     *   Constructs a BadRequestException with the given error message.
     * ================================================================ */
    public BadRequestException(String message) {
        super(message);
    }
}
