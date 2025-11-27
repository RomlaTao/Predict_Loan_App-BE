package com.predict_app.customerservice.controllers;

import com.predict_app.customerservice.dtos.CustomerProfileRequestDto;
import com.predict_app.customerservice.dtos.CustomerProfileResponseDto;
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
    public ResponseEntity<CustomerProfileResponseDto> createCustomer(
            @RequestBody CustomerProfileRequestDto request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID staffId) {
        if (role.equals("ROLE_STAFF")) {
            return ResponseEntity.ok(customerProfileService.createCustomer(request, staffId));
        } else {
            throw new RuntimeException("You are not authorized to create a customer");
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CustomerProfileResponseDto>> createCustomers(
            @RequestBody List<CustomerProfileRequestDto> requests,
            @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF")) {
            return ResponseEntity.ok(customerProfileService.createCustomers(requests));
        } else {
            throw new RuntimeException("You are not authorized to create customers");
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfileResponseDto> getCustomer(
            @PathVariable UUID customerId, 
            @RequestHeader("X-User-Id") UUID staffId,
            @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF") || role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(customerProfileService.getProfileByCustomerId(customerId, staffId, role));
        } else {
            throw new RuntimeException("You are not authorized to get this customer");
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerProfileResponseDto>> getAllCustomers(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF") || role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(customerProfileService.getAllCustomers());
        } else {
            throw new RuntimeException("You are not authorized to get all customers");
        }
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerProfileResponseDto> updateCustomer(
            @PathVariable UUID customerId,
            @RequestBody CustomerProfileRequestDto request,
            @RequestHeader("X-User-Id") UUID staffId,
            @RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_STAFF")) {
            return ResponseEntity.ok(customerProfileService.updateCustomer(customerId, request, staffId));
        } else {
            throw new RuntimeException("You are not authorized to update this customer");
        }
    }

    // @DeleteMapping("/{customerId}")
    // public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerId) {
    //     customerProfileService.deleteProfile(customerId);
    //     return ResponseEntity.ok().build();
    // }

    @GetMapping("/approved")
    public ResponseEntity<List<CustomerProfileResponseDto>> getApprovedCustomers(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(customerProfileService.getApprovedCustomers());
        } else {
            throw new RuntimeException("You are not authorized to get approved customers");
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<CustomerProfileResponseDto>> getRejectedCustomers(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(customerProfileService.getRejectedCustomers());
        } else {
            throw new RuntimeException("You are not authorized to get rejected customers");
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CustomerProfileResponseDto>> getPendingCustomers(@RequestHeader("X-User-Role") String role) {
        if (role.equals("ROLE_RISK_ANALYST")) {
            return ResponseEntity.ok(customerProfileService.getPendingCustomers());
        } else {
            throw new RuntimeException("You are not authorized to get pending customers");
        }
    }
}
