package com.predict_app.predictionservice.dtos;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for creating a prediction request.
 * 
 * Customer data will be automatically fetched from CustomerService based on customerId.
 * The prediction flow:
 * 1. Client sends customerId and employeeId
 * 2. Service creates prediction with PENDING status
 * 3. Service requests customer data from CustomerService via RabbitMQ
 * 4. CustomerService responds with enriched customer data
 * 5. Service sends customer data to ML model via RabbitMQ
 * 6. ML model processes and returns prediction result
 * 7. Service updates prediction with COMPLETED status and result
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequestDto {
    
    /**
     * Customer ID - Required
     * Used to fetch customer profile data from CustomerService
     */
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    /**
     * Employee ID - Required
     * ID of the employee creating this prediction request
     */
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    // Note: Customer profile data (age, experience, income, etc.) 
    // will be automatically retrieved from CustomerService based on customerId.
    // No need to include these fields in the request.
}
