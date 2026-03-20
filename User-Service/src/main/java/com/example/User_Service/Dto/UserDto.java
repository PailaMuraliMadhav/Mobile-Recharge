package com.example.User_Service.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Value;

@Data
public class UserDto {
    @NotBlank(message = "Name is Required")
    private  String name;

    @Email
    @NotBlank(message = "Email is Required")
    private  String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 6, message = "Password must be min size of 6")
    private  String password;

    @NotBlank(message = "Number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",message = "phone number must be valid 10 digit number")
    private  String phoneNumber;


}
