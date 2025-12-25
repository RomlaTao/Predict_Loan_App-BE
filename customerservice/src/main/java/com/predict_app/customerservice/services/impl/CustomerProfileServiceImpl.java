package com.predict_app.customerservice.services.impl;

import com.predict_app.customerservice.dtos.CustomerProfileRequestDto;
import com.predict_app.customerservice.dtos.CustomerProfileResponseDto;
import com.predict_app.customerservice.entities.CustomerProfile;
import com.predict_app.customerservice.repositories.CustomerProfileRepository;
import com.predict_app.customerservice.services.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.predict_app.customerservice.utils.SlugUtil;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final SlugUtil slugUtil;

    @Transactional
    @Override
    public CustomerProfileResponseDto createCustomer(CustomerProfileRequestDto request, UUID staffId) {
        log.info("Creating customer {} requested by staff {}", request.getEmail(), staffId);
        // Generate UUID if not provided
        if (request.getCustomerId() == null) {
            request.setCustomerId(UUID.randomUUID());
        }

        if (staffId == null) {
            throw new RuntimeException("Staff ID not null");
        }

        // Authorization is handled by @PreAuthorize in controller
        CustomerProfile customerProfile = CustomerProfile.builder()
            .customerId(request.getCustomerId())
            .customerSlug(slugUtil.generateSlug(request.getFullName()))
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
            .staffId(staffId)
            .build();
        customerProfile.onCreate();
        CustomerProfile savedProfile = customerProfileRepository.save(customerProfile);
        return mapToResponseDto(savedProfile);
    }

    @Transactional
    @Override
    public List<CustomerProfileResponseDto> createCustomers(List<CustomerProfileRequestDto> requests) {
        // Authorization is handled by @PreAuthorize in controller
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
    public CustomerProfileResponseDto getProfileByCustomerId(UUID customerId, UUID staffId, String role) {
        // Authorization is handled by @PreAuthorize in controller
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        log.info("Getting customer profile {} for staff {} with role {}", customerId, staffId, role);

        if (role.equals("ROLE_STAFF")) {
            if (!customerProfile.getStaffId().equals(staffId)) {
                throw new RuntimeException("You are not authorized to view this customer profile");
            }
        }

        return mapToResponseDto(customerProfile);
    }

    @Override
    public List<CustomerProfileResponseDto> getAllCustomers() {
        // Authorization is handled by @PreAuthorize in controller
        List<CustomerProfile> profiles = customerProfileRepository.findAll();
        return profiles.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public CustomerProfileResponseDto getProfileByCustomerSlug(String customerSlug, UUID staffId, String role) {
        CustomerProfile customerProfile = customerProfileRepository.findByCustomerSlug(customerSlug)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
        if (role.equals("ROLE_STAFF")) {
            if (!staffId.equals(customerProfile.getStaffId())) {
                throw new RuntimeException("You are not authorized to view this customer profile");
            }
        }
        return mapToResponseDto(customerProfile);
    }

    @Transactional
    @Override
    public CustomerProfileResponseDto updateCustomer(UUID customerId, CustomerProfileRequestDto request, UUID staffId) {
        // Authorization is handled by @PreAuthorize in controller
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        // Additional check: STAFF can only update customers assigned to them
        if (staffId != null && !staffId.equals(customerProfile.getStaffId())) {
            throw new RuntimeException("You are not authorized to update this customer profile");
        }

        // Update fields
        customerProfile.setFullName(request.getFullName());
        customerProfile.setEmail(request.getEmail());
        customerProfile.setAge(request.getAge());
        customerProfile.setExperience(request.getExperience());
        customerProfile.setIncome(request.getIncome());
        customerProfile.setFamily(request.getFamily());
        customerProfile.setCcAvg(request.getCcAvg());
        customerProfile.setEducation(request.getEducation());
        customerProfile.setMortgage(request.getMortgage());
        customerProfile.setSecuritiesAccount(request.getSecuritiesAccount());
        customerProfile.setCdAccount(request.getCdAccount());
        customerProfile.setOnline(request.getOnline());
        customerProfile.setCreditCard(request.getCreditCard());
        customerProfile.setPersonalLoan(request.getPersonalLoan());
        customerProfile.onUpdate();
        CustomerProfile updatedProfile = customerProfileRepository.save(customerProfile);
        return mapToResponseDto(updatedProfile);
    }

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
    public List<CustomerProfileResponseDto> getCustomersByStaffId(
            UUID staffId, 
            String role, 
            UUID currentStaffId) {
        if (role.equals("ROLE_STAFF")) {
            if (!staffId.equals(currentStaffId)) {
                throw new RuntimeException("You are not authorized to view this customers");
            }
        }

        List<CustomerProfile> customers = customerProfileRepository.findByStaffId(staffId);
        return customers.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public CustomerProfileResponseDto approveCustomer(UUID customerId) {
        CustomerProfile customerProfile = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
        
        customerProfile.setPersonalLoan(true);
        CustomerProfile updatedProfile = customerProfileRepository.save(customerProfile);
        return mapToResponseDto(updatedProfile);
    }

    @Transactional
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
                .customerSlug(slugUtil.generateSlug(request.getFullName()))
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
                .staffId(request.getStaffId())
                .personalLoan(request.getPersonalLoan())
                .build();
    }

    private CustomerProfileResponseDto mapToResponseDto(CustomerProfile customerProfile) {
        return CustomerProfileResponseDto.builder()
                .customerId(customerProfile.getCustomerId())
                .customerSlug(customerProfile.getCustomerSlug())
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
                .staffId(customerProfile.getStaffId())
                .createdAt(customerProfile.getCreatedAt())
                .updatedAt(customerProfile.getUpdatedAt())
                .build();
    }
}
