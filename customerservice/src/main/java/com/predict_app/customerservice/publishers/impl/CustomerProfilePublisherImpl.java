package com.predict_app.customerservice.publishers.impl;

import com.predict_app.customerservice.publishers.CustomerProfilePublisher;
import com.predict_app.customerservice.dtos.events.CustomerEnrichedEventDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CustomerProfilePublisherImpl implements CustomerProfilePublisher {

    private static final Logger logger = LoggerFactory.getLogger(CustomerProfilePublisherImpl.class);


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
        String predictionId = customerEnrichedEventDto.getPredictionId() != null 
            ? customerEnrichedEventDto.getPredictionId().toString() 
            : "null";
        String customerId = customerEnrichedEventDto.getCustomer() != null 
            && customerEnrichedEventDto.getCustomer().getCustomerId() != null
            ? customerEnrichedEventDto.getCustomer().getCustomerId().toString() 
            : "null";
        
        logger.info("📤 [CUSTOMER→PREDICTION] Publishing CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}", 
            predictionId, customerId);
        logger.debug("📤 [CUSTOMER→PREDICTION] Exchange: {}, RoutingKey: {}, EnrichedAt: {}", 
            customerProfileEnrichedExchangeName, customerProfileEnrichedRoutingKey, 
            customerEnrichedEventDto.getEnrichedAt());

        try {
            CustomerEnrichedEventDto enrichedEventDto = CustomerEnrichedEventDto.builder()
                .predictionId(customerEnrichedEventDto.getPredictionId())
                .customer(customerEnrichedEventDto.getCustomer())
                .enrichedAt(LocalDateTime.now())
                .build();

            rabbitTemplate.convertAndSend(customerProfileEnrichedExchangeName, customerProfileEnrichedRoutingKey, enrichedEventDto);
            
            logger.info("✅ [CUSTOMER→PREDICTION] Successfully published CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
        } catch (Exception e) {
            logger.error("❌ [CUSTOMER→PREDICTION] Failed to publish CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}, Error: {}", 
                predictionId, customerId, e.getMessage(), e);
            throw e;
        }
    }
}
