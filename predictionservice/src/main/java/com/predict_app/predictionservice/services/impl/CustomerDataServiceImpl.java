package com.predict_app.predictionservice.services.impl;

import com.predict_app.predictionservice.clients.CustomerServiceClient;
import com.predict_app.predictionservice.dtos.CustomerProfileResponseDto;
import com.predict_app.predictionservice.dtos.MLModelRequestDto;
import com.predict_app.predictionservice.services.CustomerDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDataServiceImpl implements CustomerDataService {

    private final CustomerServiceClient customerServiceClient;

    @Override
    public CustomerProfileResponseDto getCustomerData(UUID customerId) {
        try {
            log.info("Fetching customer data for customerId: {}", customerId);
            return customerServiceClient.getCustomerById(customerId);
        } catch (Exception e) {
            log.error("Error fetching customer data for customerId: {}", customerId, e);
            throw new RuntimeException("Failed to fetch customer data: " + e.getMessage());
        }
    }

    @Override
    public MLModelRequestDto getCustomerMLData(UUID customerId) {
        try {
            log.info("Fetching ML data for customerId: {}", customerId);
            return customerServiceClient.getCustomerMLData(customerId);
        } catch (Exception e) {
            log.error("Error fetching ML data for customerId: {}", customerId, e);
            throw new RuntimeException("Failed to fetch ML data: " + e.getMessage());
        }
    }

    @Override
    public MLModelRequestDto convertCustomerToMLRequest(CustomerProfileResponseDto customer) {
        try {
            log.info("Converting customer data to ML request for customerId: {}", customer.getCustomerId());
            
            return MLModelRequestDto.builder()
                    .age(customer.getAge())
                    .experience(customer.getExperience())
                    .income(customer.getIncome())
                    .family(customer.getFamily())
                    .education(customer.getEducation())
                    .mortgage(customer.getMortgage())
                    .securitiesAccount(customer.getSecuritiesAccount() ? 1 : 0)
                    .cdAccount(customer.getCdAccount() ? 1 : 0)
                    .online(customer.getOnline() ? 1 : 0)
                    .creditCard(customer.getCreditCard() ? 1 : 0)
                    .annCcAvg(customer.getCcAvg())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error converting customer data to ML request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert customer data: " + e.getMessage());
        }
    }
}
