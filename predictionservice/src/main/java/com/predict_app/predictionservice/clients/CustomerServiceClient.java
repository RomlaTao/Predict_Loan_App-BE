package com.predict_app.predictionservice.clients;

import com.predict_app.predictionservice.dtos.CustomerProfileResponseDto;
import com.predict_app.predictionservice.dtos.MLModelRequestDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customerservice", url = "${customer.service.url}")
public interface CustomerServiceClient {
    
    @GetMapping("/api/customers/{customerId}")
    CustomerProfileResponseDto getCustomerById(@PathVariable UUID customerId);
    
    @GetMapping("/api/customers/{customerId}/ml-data")
    MLModelRequestDto getCustomerMLData(@PathVariable UUID customerId);
}
