package com.predict_app.authservice.services.impl;

import com.predict_app.authservice.publisher.AuthenticationEventPublisher;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.predict_app.authservice.repositories.UserRepository;
import com.predict_app.authservice.entities.User;
import com.predict_app.authservice.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Service
public class AdminInitializerImpl implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializerImpl.class);
    private final AuthenticationEventPublisher authenticationEventPublisher;

    @Value("${admin.default.email}")
    private String adminDefaultEmail;

    @Value("${admin.default.password}")
    private String adminDefaultPassword;

    @Value("${admin.default.role}")
    private String adminDefaultRole;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(adminDefaultEmail)) {

            // Create and save new user
            User admin = User.builder()
            .email(adminDefaultEmail)
            .password(passwordEncoder.encode(adminDefaultPassword))
            .role(Role.valueOf(adminDefaultRole))
            .firstLogin(true)
            .build();

            userRepository.save(admin);
            authenticationEventPublisher.publishUserCreatedEvent(admin);
            logger.info("Default ADMIN created: " + adminDefaultEmail);
        } else {
            logger.info("ADMIN already exists, skipping...");
        }
    }
}
