package com.example.RechargeService.Exceptions;

public class BadRequestException extends RuntimeException{
    public  BadRequestException(String message){
        super(message);
    }
}
