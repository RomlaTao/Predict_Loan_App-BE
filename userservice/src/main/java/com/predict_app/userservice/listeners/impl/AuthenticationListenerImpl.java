package com.predict_app.userservice.listeners.impl;

import com.predict_app.userservice.listeners.AuthenticationListener;
import com.predict_app.userservice.services.UserProfileService;
import com.predict_app.userservice.dtos.events.UserCreatedEventDto;
import com.predict_app.userservice.repositories.UserProfileRepository;

import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class AuthenticationListenerImpl implements AuthenticationListener {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationListenerImpl.class);
    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;

    @RabbitListener(queues = "${app.rabbitmq.queue.user-created}", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserCreatedEvent(UserCreatedEventDto userCreatedEvent, 
                                    Channel channel, 
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            boolean exists = userProfileRepository.findByUserId(userCreatedEvent.getUserId()).isPresent();
            if (exists) {
                logger.info("Profile already exists for user: {}", userCreatedEvent.getUserId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            userProfileService.createProfileDefault(userCreatedEvent);
            logger.info("Successfully created profile default for user: {}", userCreatedEvent.getUserId());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            try {
                channel.basicNack(deliveryTag, false, false);
                logger.error("Error creating profile default for user: {}", userCreatedEvent.getUserId(), e);
            } catch (IOException ioException) {
                logger.error("Error acknowledging message for user: {}", userCreatedEvent.getUserId(), ioException);
            }
        }
    }
}