package com.predict_app.predictionservice.listeners.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predict_app.predictionservice.dtos.events.CustomerEnrichedEventDto;
import com.predict_app.predictionservice.listeners.CustomerProfileListener;
import com.predict_app.predictionservice.services.PredictionService;
import com.predict_app.predictionservice.services.CurrencyConverterService;
import com.predict_app.predictionservice.dtos.events.ModelPredictRequestedEventDto;
import com.predict_app.predictionservice.publishers.PredictionEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import java.io.IOException;

@Service
public class CustomerProfileListenerImpl implements CustomerProfileListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomerProfileListenerImpl.class);

    @Autowired
    private final PredictionService predictionService;

    @Autowired
    private final PredictionEventPublisher predictionEventPublisher;

    @Autowired
    private final ObjectMapper objectMapper;

    @Autowired
    private final CurrencyConverterService currencyConverterService;

    public CustomerProfileListenerImpl(
            PredictionService predictionService,
            PredictionEventPublisher predictionEventPublisher,
            ObjectMapper objectMapper,
            CurrencyConverterService currencyConverterService) {
        this.predictionService = predictionService;
        this.predictionEventPublisher = predictionEventPublisher;
        this.objectMapper = objectMapper;
        this.currencyConverterService = currencyConverterService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.customer-profile-enriched}")
    @Override
    public void handleCustomerProfileResponse(CustomerEnrichedEventDto event,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        long startTime = System.currentTimeMillis();
        String predictionId = event.getPredictionId() != null ? event.getPredictionId().toString() : "null";
        String customerId = event.getCustomer() != null && event.getCustomer().getCustomerId() != null
            ? event.getCustomer().getCustomerId().toString() 
            : "null";
        
        logger.info("[PREDICTION] Received CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
            predictionId, customerId, deliveryTag);
        logger.debug("[PREDICTION] Customer data - FullName: {}, Age: {}, Income: {}, EnrichedAt: {}", 
            event.getCustomer() != null ? event.getCustomer().getFullName() : "null",
            event.getCustomer() != null ? event.getCustomer().getAge() : "null",
            event.getCustomer() != null ? event.getCustomer().getIncome() : "null",
            event.getEnrichedAt());
    
        try {
            // Validation
            if (event.getPredictionId() == null) {
                logger.error("[PREDICTION] Validation failed: Prediction ID is required - DeliveryTag: {}", deliveryTag);
                throw new RuntimeException("Prediction ID is required");
            }
            
            if (event.getCustomer() == null) {
                logger.error("[PREDICTION] Validation failed: Customer is required - PredictionId: {}, DeliveryTag: {}", 
                    predictionId, deliveryTag);
                throw new RuntimeException("Customer is required");
            }
            
            logger.debug("💾 [PREDICTION] Saving customer input data (JSON) to prediction - PredictionId: {}", predictionId);
            String customerJson;
            try {
                customerJson = objectMapper.writeValueAsString(event.getCustomer());
            } catch (JsonProcessingException e) {
                logger.error("[PREDICTION] Failed to serialize customer data to JSON - PredictionId: {}, Error: {}",
                    predictionId, e.getMessage(), e);
                throw new RuntimeException("Failed to serialize customer data to JSON", e);
            }

            predictionService.setInputData(event.getPredictionId(), customerJson);
            logger.info("[PREDICTION] Customer input data (JSON) saved - PredictionId: {}", predictionId);

            logger.debug("[PREDICTION] Building ModelPredictRequestedEventDto - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
            
            // Convert VND to USD for model input (model expects values in *000USD)
            // Customer data is in VND, so we need to convert: income, mortgage, ccAvg
            Double incomeVnd = event.getCustomer().getIncome();
            Double mortgageVnd = event.getCustomer().getMortgage();
            Double ccAvgVnd = event.getCustomer().getCcAvg();
            
            Double incomeUsd = currencyConverterService.convertVndToUsd(incomeVnd);
            Double mortgageUsd = currencyConverterService.convertVndToUsd(mortgageVnd);
            Double ccAvgUsd = currencyConverterService.convertVndToUsd(ccAvgVnd);
            
            logger.info("[PREDICTION] Currency conversion - Income: {} VND → {} USD, Mortgage: {} VND → {} USD, CCAvg: {} VND → {} USD", 
                incomeVnd, incomeUsd, mortgageVnd, mortgageUsd, ccAvgVnd, ccAvgUsd);
            
            ModelPredictRequestedEventDto modelPredictRequestedEventDto = ModelPredictRequestedEventDto.builder()
                .predictionId(event.getPredictionId())
                .customerId(event.getCustomer().getCustomerId())
                .input(ModelPredictRequestedEventDto.ModelInputDto.builder()
                    .age(event.getCustomer().getAge())
                    .experience(event.getCustomer().getExperience())
                    .income(incomeUsd) // Converted to USD
                    .family(event.getCustomer().getFamily())
                    .education(event.getCustomer().getEducation())
                    .mortgage(mortgageUsd) // Converted to USD
                    .securitiesAccount(event.getCustomer().getSecuritiesAccount())
                    .cdAccount(event.getCustomer().getCdAccount())
                    .online(event.getCustomer().getOnline())
                    .creditCard(event.getCustomer().getCreditCard())
                    .ccAvg(ccAvgUsd) // Converted to USD
                    .build())
                .build();

            logger.info("[PREDICTION→ML_MODEL] Publishing ModelPredictRequestedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
            predictionEventPublisher.publishModelPredictRequestedEvent(modelPredictRequestedEventDto);

            // Acknowledge thành công - chỉ ack sau khi tất cả operations thành công
            try {
                if (channel.isOpen()) {
                    channel.basicAck(deliveryTag, false);
                    logger.debug("[PREDICTION] Message acknowledged - DeliveryTag: {}", deliveryTag);
                } else {
                    logger.warn("[PREDICTION] Channel is closed, cannot acknowledge message - DeliveryTag: {}", deliveryTag);
                }
            } catch (IOException ackException) {
                logger.error("[PREDICTION] Failed to acknowledge message - DeliveryTag: {}, Error: {}", 
                    deliveryTag, ackException.getMessage(), ackException);
                // Don't throw - message already processed successfully
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            logger.info("[PREDICTION] Successfully processed CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}, ProcessingTime: {}ms, DeliveryTag: {}", 
                predictionId, customerId, processingTime, deliveryTag);
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("[PREDICTION] Failed to process CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}, ProcessingTime: {}ms, DeliveryTag: {}, Error: {}", 
                predictionId, customerId, processingTime, deliveryTag, e.getMessage(), e);
            try {
                // Reject và không requeue
                channel.basicNack(deliveryTag, false, false);
                logger.warn("[PREDICTION] Message rejected and not requeued - DeliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                logger.error("[PREDICTION] Failed to acknowledge/reject message - DeliveryTag: {}, Error: {}", 
                    deliveryTag, ioException.getMessage(), ioException);
                throw new RuntimeException("Failed to acknowledge message", ioException);
            }
        }
    }  
}