package com.predict_app.authservice.dtos;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto {
    private String accessToken;
}
