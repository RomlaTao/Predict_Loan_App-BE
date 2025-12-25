package com.predict_app.customerservice.repositories;

import com.predict_app.customerservice.entities.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    boolean existsByEmail(String email);
    List<CustomerProfile> findByStaffId(UUID staffId);
    Optional<CustomerProfile> findByCustomerSlug(String customerSlug);
    boolean existsByCustomerSlug(String customerSlug);
}
