package com.predict_app.authservice.dtos.events;

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
