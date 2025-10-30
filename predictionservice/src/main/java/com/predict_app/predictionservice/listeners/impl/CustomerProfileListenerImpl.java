package com.predict_app.predictionservice.listeners.impl;

import com.predict_app.predictionservice.dtos.events.CustomerEnrichedEventDto;
import com.predict_app.predictionservice.listeners.CustomerProfileListener;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.dtos.events.ModelPredictRequestedEventDto;
import com.predict_app.predictionservice.publishers.PredictionEventPublisher;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import java.io.IOException;

@Service
public class CustomerProfileListenerImpl implements CustomerProfileListener {

    @Autowired
    private final PredictionService predictionService;

    @Autowired
    private final PredictionEventPublisher predictionEventPublisher;

    public CustomerProfileListenerImpl(PredictionService predictionService, PredictionEventPublisher predictionEventPublisher) {
        this.predictionService = predictionService;
        this.predictionEventPublisher = predictionEventPublisher;
    }

    @RabbitListener(queues = "${rabbitmq.queue.customer-profile-response}")
    @Override
    public void handleCustomerProfileResponse(CustomerEnrichedEventDto event,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
    
        try {
            if (event.getPredictionId() == null) {
                throw new RuntimeException("Prediction ID is required");
            }
            
            if (event.getCustomer() == null) {
                throw new RuntimeException("Customer is required");
            }
            
            predictionService.setInputData(event.getPredictionId(), event.getCustomer().toString());

            ModelPredictRequestedEventDto modelPredictRequestedEventDto = ModelPredictRequestedEventDto.builder()
                .predictionId(event.getPredictionId())
                .customerId(event.getCustomer().getCustomerId())
                .input(ModelPredictRequestedEventDto.ModelInputDto.builder()
                    .age(event.getCustomer().getAge())
                    .experience(event.getCustomer().getExperience())
                    .income(event.getCustomer().getIncome())
                    .family(event.getCustomer().getFamily())
                    .education(event.getCustomer().getEducation())
                    .mortgage(event.getCustomer().getMortgage())
                    .securitiesAccount(event.getCustomer().getSecuritiesAccount())
                    .cdAccount(event.getCustomer().getCdAccount())
                    .online(event.getCustomer().getOnline())
                    .creditCard(event.getCustomer().getCreditCard())
                    .ccAvg(event.getCustomer().getCcAvg())
                    .build())
                .build();

            predictionEventPublisher.publishModelPredictRequestedEvent(modelPredictRequestedEventDto);

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