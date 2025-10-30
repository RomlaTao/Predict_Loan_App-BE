package com.predict_app.customerservice.publishers;

import com.predict_app.customerservice.dtos.events.CustomerEnrichedEventDto;

public interface CustomerProfilePublisher {
    void publishCustomerProfileEnrichedEvent(CustomerEnrichedEventDto customerEnrichedEventDto);
}
