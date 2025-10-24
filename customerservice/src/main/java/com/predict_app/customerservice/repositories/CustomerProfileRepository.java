package com.predict_app.customerservice.repositories;

import com.predict_app.customerservice.entities.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    boolean existsByEmail(String email);
}
