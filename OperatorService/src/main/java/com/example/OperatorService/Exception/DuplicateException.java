package com.example.OperatorService.Exception;

public class DuplicateException extends RuntimeException{
    public DuplicateException(String message){
        super(message);
    }
}
