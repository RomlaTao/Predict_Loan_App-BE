package com.predict_app.authservice.services.impl;

import com.predict_app.authservice.configs.JwtConfig;
import com.predict_app.authservice.dtos.LoginRequestDto;
import com.predict_app.authservice.dtos.LoginResponseDto;
import com.predict_app.authservice.dtos.RefreshTokenRequestDto;
import com.predict_app.authservice.dtos.RefreshTokenResponseDto;
import com.predict_app.authservice.dtos.SignupRequestDto;
import com.predict_app.authservice.dtos.LogoutRequestDto;
import com.predict_app.authservice.entities.User;
import com.predict_app.authservice.enums.Role;
import com.predict_app.authservice.repositories.UserRepository;
import com.predict_app.authservice.securities.CustomUserDetailsService;
import com.predict_app.authservice.securities.JwtTokenProvider;
import com.predict_app.authservice.securities.UserPrincipal;
import com.predict_app.authservice.services.AuthenticationService;
import com.predict_app.authservice.services.RedisTokenService;
import com.predict_app.authservice.publisher.AuthenticationEventPublisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Service implementation for authentication operations including:
 * - User registration (signup)
 * - User authentication (login)
 * - Token refresh
 * - User logout
 */
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${admin.default.email}")
    private String adminDefaultEmail;

    @Value("${admin.default.password}")
    private String adminDefaultPassword;

    @Value("${admin.default.role}")
    private String adminDefaultRole;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisTokenService redisTokenService;
    private final JwtConfig jwtConfig;
    private final AuthenticationEventPublisher authenticationEventPublisher;

    /**
     * Register a new user account
     * 
     * @param signupRequest Signup request containing email, password, and password confirmation
     * @return Created User entity
     * @throws RuntimeException if email already exists or passwords don't match
     */
    @Override
    public User signup(SignupRequestDto signupRequest) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Validate password confirmation
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Create and save new user
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.valueOf(signupRequest.getRole()))
                .firstLogin(true)
                .build();

        User savedUser = userRepository.save(user);
        authenticationEventPublisher.publishUserCreatedEvent(savedUser);
        return savedUser;
    }

    /**
     * Authenticate user and generate access/refresh tokens
     * 
     * @param loginRequest Login request containing email and password
     * @return LoginResponseDto containing user info and tokens
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    @Override
    public LoginResponseDto authenticate(LoginRequestDto loginRequest) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal.getUsername());

        // Store tokens in Redis with expiration
        redisTokenService.saveToken(
                "access:" + userPrincipal.getUsername(),
                accessToken,
                jwtConfig.getAccessTokenExpiration()
        );
        redisTokenService.saveToken(
                "refresh:" + userPrincipal.getUsername(),
                refreshToken,
                jwtConfig.getRefreshTokenExpiration()
        );

        return new LoginResponseDto(
                userPrincipal.getId(),
                accessToken,
                refreshToken,
                "Bearer",
                userPrincipal.getUsername(),
                userPrincipal.getRole()
        );
    }

    /**
     * Refresh access token using refresh token
     * Implements token rotation: invalidates old tokens and generates new ones
     * 
     * @param refreshTokenRequest Request containing current access and refresh tokens
     * @return RefreshTokenResponseDto containing new access and refresh tokens
     * @throws SecurityException if refresh token is invalid, expired, or blacklisted
     */
    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        // Validate refresh token
        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            throw new SecurityException("Invalid or expired refresh token");
        }

        // Check if refresh token is blacklisted
        if (redisTokenService.isBlacklisted(refreshToken)) {
            throw new SecurityException("Refresh token is blacklisted");
        }

        // Extract email from refresh token and load user
        String email = tokenProvider.getUsernameFromToken(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserByUsername(email);

        // Blacklist old refresh token (token rotation)
        long refreshTokenRemainingMillis = tokenProvider.getRemainingTime(refreshToken);
        if (refreshTokenRemainingMillis > 0) {
            redisTokenService.blacklistToken(refreshToken, refreshTokenRemainingMillis);
        }

        // Generate new tokens
        String newAccessToken = tokenProvider.generateAccessToken(userPrincipal);
        String newRefreshToken = tokenProvider.generateRefreshToken(email);

        // Store new tokens in Redis
        redisTokenService.saveToken(
                "access:" + email,
                newAccessToken,
                jwtConfig.getAccessTokenExpiration()
        );
        redisTokenService.saveToken(
                "refresh:" + email,
                newRefreshToken,
                jwtConfig.getRefreshTokenExpiration()
        );

        return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
    }

    /**
     * Logout user and invalidate all tokens
     * Allows logout even if access token is expired (to invalidate refresh token)
     * 
     * @param request HTTP request containing Authorization header
     * @param logoutRequest Request containing refresh token
     * @return Success message
     * @throws SecurityException if authorization header is invalid or refresh token is invalid
     */
    @Override
    public String logout(HttpServletRequest request, LogoutRequestDto logoutRequest) {
        // Extract and validate Authorization header
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String accessToken = header.substring(7); // Remove "Bearer " prefix
        String refreshToken = logoutRequest.getRefreshToken();

        // Validate refresh token (required for logout)
        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            throw new SecurityException("Invalid or expired refresh token");
        }

        // Blacklist access token if still valid
        long accessTokenRemainingMillis = tokenProvider.getRemainingTime(accessToken);
        if (accessTokenRemainingMillis > 0 && !redisTokenService.isBlacklisted(accessToken)) {
            redisTokenService.blacklistToken(accessToken, accessTokenRemainingMillis);
        }

        // Blacklist refresh token
        long refreshTokenRemainingMillis = tokenProvider.getRemainingTime(refreshToken);
        if (refreshTokenRemainingMillis > 0) {
            redisTokenService.blacklistToken(refreshToken, refreshTokenRemainingMillis);
        }

        // Delete cached tokens from Redis
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        redisTokenService.deleteToken("access:" + username);
        redisTokenService.deleteToken("refresh:" + username);

        return "Logged out successfully";
    }

    @Override
    public void setUserFirstLoginFalse(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstLogin(false);
        userRepository.save(user);
    }
}
