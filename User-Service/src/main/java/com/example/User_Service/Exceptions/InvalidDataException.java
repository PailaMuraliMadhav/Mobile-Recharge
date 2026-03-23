package com.example.User_Service.Exceptions;


public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
    }
}