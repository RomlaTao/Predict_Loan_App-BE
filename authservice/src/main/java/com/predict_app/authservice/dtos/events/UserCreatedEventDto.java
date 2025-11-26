package com.predict_app.authservice.dtos.events;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEventDto implements Serializable {
    private UUID userId;
    private String email;
}
