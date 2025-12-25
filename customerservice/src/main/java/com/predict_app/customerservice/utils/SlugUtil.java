package com.predict_app.customerservice.utils;

import com.predict_app.customerservice.repositories.CustomerProfileRepository;
import lombok.*;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SlugUtil {
    private final CustomerProfileRepository customerProfileRepository;

    public String generateSlug(String title) {
        String slug = title.toLowerCase().replaceAll(" ", "-");
        slug = slug.replaceAll("[^a-z0-9-]", "");
        slug = slug.replaceAll("-+", "-");
        slug = slug.replaceAll("^-|-$", "");
        int index = 1;
        while(customerProfileRepository.existsByCustomerSlug(slug)) {
            slug = slug + "-" + index;
            index++;
        }
        return slug;
    }
}
