package com.predict_app.authservice.listener;

import com.predict_app.authservice.dtos.events.UserProfileCompletedEventDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface UserProfileListenner {
    void handleProfileCompleted(UserProfileCompletedEventDto userProfileCompletedEvent,
                                Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
}
