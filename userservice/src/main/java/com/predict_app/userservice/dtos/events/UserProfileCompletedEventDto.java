package com.predict_app.userservice.dtos.events;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCompletedEventDto implements Serializable {
    private UUID userId;
}
