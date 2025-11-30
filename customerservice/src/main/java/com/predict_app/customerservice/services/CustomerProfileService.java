package com.predict_app.customerservice.services;

import com.predict_app.customerservice.dtos.CustomerProfileRequestDto;
import com.predict_app.customerservice.dtos.CustomerProfileResponseDto;

import java.util.List;
import java.util.UUID;

public interface CustomerProfileService {
    CustomerProfileResponseDto createCustomer(CustomerProfileRequestDto request, UUID staffId);
    List<CustomerProfileResponseDto> createCustomers(List<CustomerProfileRequestDto> requests);
    CustomerProfileResponseDto updateCustomer(UUID customerId, CustomerProfileRequestDto request, UUID staffId);
    CustomerProfileResponseDto getProfileByCustomerId(UUID customerId, UUID staffId, String role);
    List<CustomerProfileResponseDto> getAllCustomers();
    List<CustomerProfileResponseDto> getApprovedCustomers();
    List<CustomerProfileResponseDto> getRejectedCustomers();
    List<CustomerProfileResponseDto> getPendingCustomers();
    List<CustomerProfileResponseDto> getCustomersByStaffId(UUID staffId, String role, UUID currentStaffId);
    CustomerProfileResponseDto approveCustomer(UUID customerId);
    CustomerProfileResponseDto rejectCustomer(UUID customerId);
}
