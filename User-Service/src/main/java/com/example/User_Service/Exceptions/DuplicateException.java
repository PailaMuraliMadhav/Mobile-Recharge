package com.example.User_Service.Exceptions;


public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
}
