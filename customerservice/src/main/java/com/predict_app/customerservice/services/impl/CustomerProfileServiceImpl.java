package com.predict_app.customerservice.services.impl;

import com.predict_app.customerservice.dtos.CustomerProfileRequestDto;
import com.predict_app.customerservice.dtos.CustomerProfileResponseDto;
import com.predict_app.customerservice.dtos.MLModelRequestDto;
import com.predict_app.customerservice.entities.CustomerProfile;
import com.predict_app.customerservice.repositories.CustomerProfileRepository;
import com.predict_app.customerservice.services.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;

    @Override
    public CustomerProfileResponseDto createCustomer(CustomerProfileRequestDto request) {
        // Generate UUID if not provided
        if (request.getCustomerId() == null) {
            request.setCustomerId(UUID.randomUUID());
        }

        CustomerProfile customerProfile = mapToEntity(request);
        CustomerProfile savedProfile = customerProfileRepository.save(customerProfile);
        return mapToResponseDto(savedProfile);
    }

    @Override
    public List<CustomerProfileResponseDto> createCustomers(List<CustomerProfileRequestDto> requests) {
        List<CustomerProfile> customerProfiles = requests.stream()
                .map(request -> {
                    // Generate UUID if not provided
                    if (request.getCustomerId() == null) {
                        request.setCustomerId(UUID.randomUUID());
                    }
                    return mapToEntity(request);
                })
                .toList();

        List<CustomerProfile> savedProfiles = customerProfileRepository.saveAll(customerProfiles);
        return savedProfiles.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public CustomerProfileResponseDto getProfileByCustomerId(UUID customerId) {
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
        return mapToResponseDto(customerProfile);
    }

    @Override
    public List<CustomerProfileResponseDto> getAllCustomers() {
        List<CustomerProfile> profiles = customerProfileRepository.findAll();
        return profiles.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public CustomerProfileResponseDto updateCustomer(UUID customerId, CustomerProfileRequestDto request) {
        CustomerProfile existingProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        // Update fields
        existingProfile.setFullName(request.getFullName());
        existingProfile.setEmail(request.getEmail());
        existingProfile.setAge(request.getAge());
        existingProfile.setExperience(request.getExperience());
        existingProfile.setIncome(request.getIncome());
        existingProfile.setFamily(request.getFamily());
        existingProfile.setCcAvg(request.getCcAvg());
        existingProfile.setEducation(request.getEducation());
        existingProfile.setMortgage(request.getMortgage());
        existingProfile.setSecuritiesAccount(request.getSecuritiesAccount());
        existingProfile.setCdAccount(request.getCdAccount());
        existingProfile.setOnline(request.getOnline());
        existingProfile.setCreditCard(request.getCreditCard());
        existingProfile.setPersonalLoan(request.getPersonalLoan());

        CustomerProfile updatedProfile = customerProfileRepository.save(existingProfile);
        return mapToResponseDto(updatedProfile);
    }

    // @Override
    // public void deleteCustomer(UUID customerId) {
    //     if (!customerProfileRepository.existsById(customerId)) {
    //         throw new RuntimeException("Customer profile not found");
    //     }
    //     customerProfileRepository.deleteById(customerId);
    // }

    @Override
    public List<CustomerProfileResponseDto> getApprovedCustomers() {
        List<CustomerProfile> approvedCustomers = customerProfileRepository.findAll()
                .stream()
                .filter(profile -> Boolean.TRUE.equals(profile.getPersonalLoan()))
                .toList();
        return approvedCustomers.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<CustomerProfileResponseDto> getRejectedCustomers() {
        List<CustomerProfile> rejectedCustomers = customerProfileRepository.findAll()
                .stream()
                .filter(profile -> Boolean.FALSE.equals(profile.getPersonalLoan()))
                .toList();
        return rejectedCustomers.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<CustomerProfileResponseDto> getPendingCustomers() {
        List<CustomerProfile> pendingCustomers = customerProfileRepository.findAll()
                .stream()
                .filter(profile -> profile.getPersonalLoan() == null)
                .toList();
        return pendingCustomers.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public CustomerProfileResponseDto approveCustomer(UUID customerId) {
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
        
        customerProfile.setPersonalLoan(true);
        CustomerProfile updatedProfile = customerProfileRepository.save(customerProfile);
        return mapToResponseDto(updatedProfile);
    }

    @Override
    public CustomerProfileResponseDto rejectCustomer(UUID customerId) {
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
        
        customerProfile.setPersonalLoan(false);
        CustomerProfile updatedProfile = customerProfileRepository.save(customerProfile);
        return mapToResponseDto(updatedProfile);
    }

    private CustomerProfile mapToEntity(CustomerProfileRequestDto request) {
        return CustomerProfile.builder()
                .customerId(request.getCustomerId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .age(request.getAge())
                .experience(request.getExperience())
                .income(request.getIncome())
                .family(request.getFamily())
                .ccAvg(request.getCcAvg())
                .education(request.getEducation())
                .mortgage(request.getMortgage())
                .securitiesAccount(request.getSecuritiesAccount())
                .cdAccount(request.getCdAccount())
                .online(request.getOnline())
                .creditCard(request.getCreditCard())
                .personalLoan(request.getPersonalLoan())
                .build();
    }

    private CustomerProfileResponseDto mapToResponseDto(CustomerProfile customerProfile) {
        return CustomerProfileResponseDto.builder()
                .customerId(customerProfile.getCustomerId())
                .fullName(customerProfile.getFullName())
                .email(customerProfile.getEmail())
                .age(customerProfile.getAge())
                .experience(customerProfile.getExperience())
                .income(customerProfile.getIncome())
                .family(customerProfile.getFamily())
                .ccAvg(customerProfile.getCcAvg())
                .education(customerProfile.getEducation())
                .mortgage(customerProfile.getMortgage())
                .securitiesAccount(customerProfile.getSecuritiesAccount())
                .cdAccount(customerProfile.getCdAccount())
                .online(customerProfile.getOnline())
                .creditCard(customerProfile.getCreditCard())
                .personalLoan(customerProfile.getPersonalLoan())
                .createdAt(customerProfile.getCreatedAt())
                .updatedAt(customerProfile.getUpdatedAt())
                .build();
    }

    @Override
    public MLModelRequestDto getCustomerMLData(UUID customerId) {
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
        
        return MLModelRequestDto.builder()
                .age(customerProfile.getAge())
                .experience(customerProfile.getExperience())
                .income(customerProfile.getIncome())
                .family(customerProfile.getFamily())
                .education(customerProfile.getEducation())
                .mortgage(customerProfile.getMortgage())
                .securitiesAccount(customerProfile.getSecuritiesAccount() ? 1 : 0)
                .cdAccount(customerProfile.getCdAccount() ? 1 : 0)
                .online(customerProfile.getOnline() ? 1 : 0)
                .creditCard(customerProfile.getCreditCard() ? 1 : 0)
                .annCcAvg(customerProfile.getCcAvg())
                .build();
    }
}
