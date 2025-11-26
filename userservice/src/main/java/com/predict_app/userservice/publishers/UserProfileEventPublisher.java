package com.predict_app.userservice.publishers;

import com.predict_app.userservice.entities.UserProfile;

public interface UserProfileEventPublisher {
    void publishUserProfileCompletedEvent(UserProfile userProfile);
}