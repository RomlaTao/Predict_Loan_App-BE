package com.predict_app.authservice.services;

import com.predict_app.authservice.dtos.LoginRequestDto;
import com.predict_app.authservice.dtos.LoginResponseDto;
import com.predict_app.authservice.dtos.RefreshTokenRequestDto;
import com.predict_app.authservice.dtos.SignupRequestDto;
import com.predict_app.authservice.entities.User;
import com.predict_app.authservice.dtos.LogoutRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    User signup(SignupRequestDto signupRequest);
    LoginResponseDto authenticate(LoginRequestDto loginRequest);
    LoginResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);
    String logout(LogoutRequestDto logoutRequest);
}
