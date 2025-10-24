package com.predict_app.predictionservice.services;

import com.predict_app.predictionservice.dtos.CustomerProfileResponseDto;
import com.predict_app.predictionservice.dtos.MLModelRequestDto;

import java.util.UUID;

public interface CustomerDataService {

    CustomerProfileResponseDto getCustomerData(UUID customerId);
    MLModelRequestDto getCustomerMLData(UUID customerId);
    MLModelRequestDto convertCustomerToMLRequest(CustomerProfileResponseDto customer);
}
