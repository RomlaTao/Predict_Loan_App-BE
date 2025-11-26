package com.predict_app.userservice.publishers.impl;

import com.predict_app.userservice.publishers.UserProfileEventPublisher;
import com.predict_app.userservice.dtos.events.UserProfileCompletedEventDto;
import com.predict_app.userservice.entities.UserProfile;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileEventPublisherImpl implements UserProfileEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.auth_user}")
    private String authAndUserExchangeName;

    @Value("${app.rabbitmq.routing-key.user-profile-completed}")
    private String userProfileCompletedRoutingKey;

    public void publishUserProfileCompletedEvent(UserProfile userProfile) {
        
        UserProfileCompletedEventDto userProfileCompletedEvent = UserProfileCompletedEventDto.builder()
                .userId(userProfile.getUserId())
                .build();
        rabbitTemplate.convertAndSend(authAndUserExchangeName, 
                                    userProfileCompletedRoutingKey, 
                                    userProfileCompletedEvent);
    }
}
