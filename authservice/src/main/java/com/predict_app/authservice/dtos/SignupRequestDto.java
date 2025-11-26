package com.predict_app.authservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    String email;
    String password;
    String passwordConfirm;
    String role;
}
