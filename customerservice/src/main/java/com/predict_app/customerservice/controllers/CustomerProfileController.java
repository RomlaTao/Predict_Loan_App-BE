package com.predict_app.customerservice.controllers;

import com.predict_app.customerservice.dtos.CustomerProfileRequestDto;
import com.predict_app.customerservice.dtos.CustomerProfileResponseDto;
import com.predict_app.customerservice.dtos.MLModelRequestDto;
import com.predict_app.customerservice.services.CustomerProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    CustomerProfileController(CustomerProfileService customerProfileService){
        this.customerProfileService = customerProfileService;
    }

    @PostMapping
    public ResponseEntity<CustomerProfileResponseDto> createCustomer(@RequestBody CustomerProfileRequestDto request) {
        return ResponseEntity.ok(customerProfileService.createCustomer(request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CustomerProfileResponseDto>> createCustomers(@RequestBody List<CustomerProfileRequestDto> requests) {
        return ResponseEntity.ok(customerProfileService.createCustomers(requests));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfileResponseDto> getCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerProfileService.getProfileByCustomerId(customerId));
    }

    @GetMapping
    public ResponseEntity<List<CustomerProfileResponseDto>> getAllCustomers() {
        return ResponseEntity.ok(customerProfileService.getAllCustomers());
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerProfileResponseDto> updateCustomer(
            @PathVariable UUID customerId,
            @RequestBody CustomerProfileRequestDto request) {
        return ResponseEntity.ok(customerProfileService.updateCustomer(customerId, request));
    }

    // @DeleteMapping("/{customerId}")
    // public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerId) {
    //     customerProfileService.deleteProfile(customerId);
    //     return ResponseEntity.ok().build();
    // }

    @GetMapping("/approved")
    public ResponseEntity<List<CustomerProfileResponseDto>> getApprovedCustomers() {
        return ResponseEntity.ok(customerProfileService.getApprovedCustomers());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<CustomerProfileResponseDto>> getRejectedCustomers() {
        return ResponseEntity.ok(customerProfileService.getRejectedCustomers());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CustomerProfileResponseDto>> getPendingCustomers() {
        return ResponseEntity.ok(customerProfileService.getPendingCustomers());
    }

    @PostMapping("/{customerId}/approve")
    public ResponseEntity<CustomerProfileResponseDto> approveCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerProfileService.approveCustomer(customerId));
    }

    @PostMapping("/{customerId}/reject")
    public ResponseEntity<CustomerProfileResponseDto> rejectCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerProfileService.rejectCustomer(customerId));
    }

    @GetMapping("/{customerId}/ml-data")
    public ResponseEntity<MLModelRequestDto> getCustomerMLData(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerProfileService.getCustomerMLData(customerId));
    }
}
