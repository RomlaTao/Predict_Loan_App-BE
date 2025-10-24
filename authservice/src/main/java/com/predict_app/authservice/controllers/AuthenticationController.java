package com.predict_app.authservice.controllers;

import com.predict_app.authservice.dtos.LoginRequestDto;
import com.predict_app.authservice.dtos.RefreshTokenRequestDto;
import com.predict_app.authservice.dtos.SignupRequestDto;
import com.predict_app.authservice.dtos.LoginResponseDto;
import com.predict_app.authservice.dtos.LogoutRequestDto;
import com.predict_app.authservice.entities.User;
import com.predict_app.authservice.services.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private final AuthenticationService authenticationService;

    AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequestDto signupRequest) {
        User user = authenticationService.signup(signupRequest);
        return "Signup successful for user: " + user.getEmail();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequest) {
            return ResponseEntity.ok(authenticationService.logout(logoutRequest));
    }
}
