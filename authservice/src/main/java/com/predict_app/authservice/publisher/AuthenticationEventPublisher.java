package com.predict_app.authservice.publisher;

import com.predict_app.authservice.entities.User;

public interface AuthenticationEventPublisher {
    public void publishUserCreatedEvent(User user);
}
