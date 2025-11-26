package com.predict_app.authservice.publisher.Impl;

import com.predict_app.authservice.dtos.events.UserCreatedEventDto;
import com.predict_app.authservice.publisher.AuthenticationEventPublisher;
import com.predict_app.authservice.entities.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationEventPublisherImpl implements AuthenticationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.auth_user}")
    private String authAndUserExchangeName;

    @Value("${app.rabbitmq.routing-key.user-created}")
    private String userCreatedRoutingKey;

    public void publishUserCreatedEvent(User user) {
        UserCreatedEventDto userCreatedEvent = UserCreatedEventDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
        rabbitTemplate.convertAndSend(authAndUserExchangeName, 
                                    userCreatedRoutingKey, 
                                    userCreatedEvent);
    }
}
