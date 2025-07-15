package org.refit.spring.auth.dto;

import lombok.*;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
