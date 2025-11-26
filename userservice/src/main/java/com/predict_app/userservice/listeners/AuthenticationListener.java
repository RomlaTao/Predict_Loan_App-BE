package com.predict_app.userservice.listeners;

import com.predict_app.userservice.dtos.events.UserCreatedEventDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface AuthenticationListener {
    void handleUserCreatedEvent(UserCreatedEventDto userCreatedEvent, 
                                Channel channel,        
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag);
}
