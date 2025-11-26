package com.predict_app.authservice.services;

import com.predict_app.authservice.dtos.LoginRequestDto;
import com.predict_app.authservice.dtos.LoginResponseDto;
import com.predict_app.authservice.dtos.RefreshTokenRequestDto;
import com.predict_app.authservice.dtos.RefreshTokenResponseDto;
import com.predict_app.authservice.dtos.SignupRequestDto;
import com.predict_app.authservice.entities.User;
import com.predict_app.authservice.dtos.LogoutRequestDto;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public interface AuthenticationService {
    User signup(SignupRequestDto signupRequest);
    LoginResponseDto authenticate(LoginRequestDto loginRequest);
    RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);
    String logout(HttpServletRequest request, LogoutRequestDto logoutRequest);
    void setUserFirstLoginFalse(UUID userId);
}
