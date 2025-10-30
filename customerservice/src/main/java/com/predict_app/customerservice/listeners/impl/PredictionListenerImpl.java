package com.predict_app.customerservice.listeners.impl;

import com.predict_app.customerservice.listeners.PredictionListener;
import com.predict_app.customerservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.customerservice.entities.CustomerProfile;
import com.predict_app.customerservice.repositories.CustomerProfileRepository;
import com.predict_app.customerservice.dtos.events.CustomerEnrichedEventDto;
import com.predict_app.customerservice.publishers.CustomerProfilePublisher;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.messaging.handler.annotation.Header;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class PredictionListenerImpl implements PredictionListener {

    @Autowired
    private final CustomerProfileRepository customerProfileRepository;

    @Autowired
    private final CustomerProfilePublisher customerProfilePublisher;

    public PredictionListenerImpl(CustomerProfileRepository customerProfileRepository, CustomerProfilePublisher customerProfilePublisher) {
        this.customerProfileRepository = customerProfileRepository;
        this.customerProfilePublisher = customerProfilePublisher;
    }

    @Override
    public void handlePredictionRequestedEvent(PredictionRequestedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            if (event.getPredictionId() == null) {
                throw new RuntimeException("Prediction ID is required");
            }
            if (event.getCustomerId() == null) {
                throw new RuntimeException("Customer ID is required");
            }
            if (event.getEmployeeId() == null) {
                throw new RuntimeException("Employee ID is required");
            }

            // Lấy customer profile
            CustomerProfile customerProfile = customerProfileRepository.findById(event.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            // Tạo customer enriched event
            CustomerEnrichedEventDto customerEnrichedEventDto = CustomerEnrichedEventDto.builder()
                .predictionId(event.getPredictionId())
                .customer(CustomerEnrichedEventDto.CustomerDto.builder()
                    .customerId(customerProfile.getCustomerId())
                    .fullName(customerProfile.getFullName())
                    .email(customerProfile.getEmail())
                    .age(customerProfile.getAge())
                    .experience(customerProfile.getExperience())
                    .income(customerProfile.getIncome())
                    .family(customerProfile.getFamily())
                    .education(customerProfile.getEducation())
                    .mortgage(customerProfile.getMortgage())
                    .securitiesAccount(customerProfile.getSecuritiesAccount())
                    .cdAccount(customerProfile.getCdAccount())
                    .online(customerProfile.getOnline())
                    .creditCard(customerProfile.getCreditCard())
                    .ccAvg(customerProfile.getCcAvg())
                    .personalLoan(customerProfile.getPersonalLoan())
                    .build())
                .enrichedAt(LocalDateTime.now())
                .build();

            // Publish customer enriched event
            customerProfilePublisher.publishCustomerProfileEnrichedEvent(customerEnrichedEventDto);

            // Acknowledge thành công
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                // Reject và không requeue
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                // Log error
                throw new RuntimeException("Failed to acknowledge message", ioException);
            }
        }
    }
}
