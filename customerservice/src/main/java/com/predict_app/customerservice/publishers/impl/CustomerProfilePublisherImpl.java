package com.predict_app.customerservice.publishers.impl;

import com.predict_app.customerservice.publishers.CustomerProfilePublisher;
import com.predict_app.customerservice.dtos.events.CustomerEnrichedEventDto;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CustomerProfilePublisherImpl implements CustomerProfilePublisher {


    @Value("${rabbitmq.exchange.customer-profile-enriched}")
    private String customerProfileEnrichedExchangeName;

    @Value("${rabbitmq.queue.customer-profile-enriched}")
    private String customerProfileEnrichedQueueName;

    @Value("${rabbitmq.routing-key.customer-profile-enriched}")
    private String customerProfileEnrichedRoutingKey;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public CustomerProfilePublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishCustomerProfileEnrichedEvent(CustomerEnrichedEventDto customerEnrichedEventDto) {

        CustomerEnrichedEventDto enrichedEventDto = CustomerEnrichedEventDto.builder()
            .predictionId(customerEnrichedEventDto.getPredictionId())
            .customer(customerEnrichedEventDto.getCustomer())
            .enrichedAt(LocalDateTime.now())
            .build();

        rabbitTemplate.convertAndSend(customerProfileEnrichedExchangeName, customerProfileEnrichedRoutingKey, enrichedEventDto);
    }
}
