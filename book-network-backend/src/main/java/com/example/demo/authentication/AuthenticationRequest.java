package com.example.demo.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Email(message = "Email is not formated")
    @NotEmpty(message = "Email is mendatory")
    @NotBlank(message = "Email is mendatory")
    private String email;
    @NotEmpty(message = "Password is mendatory")
    @NotBlank(message = "Password is mendatory")
    @Size(min = 8,message = "password should be 8 characters long")
    private String password;
}
