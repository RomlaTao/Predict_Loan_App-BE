package com.predict_app.customerservice.services;

import com.predict_app.customerservice.dtos.CustomerProfileRequestDto;
import com.predict_app.customerservice.dtos.CustomerProfileResponseDto;

import java.util.List;
import java.util.UUID;

public interface CustomerProfileService {

    // Create customer profile
    CustomerProfileResponseDto createCustomer(CustomerProfileRequestDto request);
    List<CustomerProfileResponseDto> createCustomers(List<CustomerProfileRequestDto> requests);
    CustomerProfileResponseDto updateCustomer(UUID customerId, CustomerProfileRequestDto request);
    
    // Ignore for now
    // void deleteCustomer(UUID customerId);
    
    // Loan approval management
    CustomerProfileResponseDto getProfileByCustomerId(UUID customerId);
    List<CustomerProfileResponseDto> getAllCustomers();
    List<CustomerProfileResponseDto> getApprovedCustomers();
    List<CustomerProfileResponseDto> getRejectedCustomers();
    List<CustomerProfileResponseDto> getPendingCustomers();

    // Loan approval management
    CustomerProfileResponseDto approveCustomer(UUID customerId);
    CustomerProfileResponseDto rejectCustomer(UUID customerId);
}
