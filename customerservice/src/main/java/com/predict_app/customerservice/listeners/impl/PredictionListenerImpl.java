package com.predict_app.customerservice.listeners.impl;

import com.predict_app.customerservice.listeners.PredictionListener;
import com.predict_app.customerservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.customerservice.entities.CustomerProfile;
import com.predict_app.customerservice.repositories.CustomerProfileRepository;
import com.predict_app.customerservice.dtos.events.CustomerEnrichedEventDto;
import com.predict_app.customerservice.publishers.CustomerProfilePublisher;
import com.predict_app.customerservice.dtos.events.PredictionCompletedEventDto;
import com.predict_app.customerservice.services.CustomerProfileService;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.messaging.handler.annotation.Header;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class PredictionListenerImpl implements PredictionListener {

    private static final Logger logger = LoggerFactory.getLogger(PredictionListenerImpl.class);

    @Autowired
    private final CustomerProfileRepository customerProfileRepository;

    @Autowired
    private final CustomerProfilePublisher customerProfilePublisher;

    @Autowired
    private final CustomerProfileService customerProfileService;

    public PredictionListenerImpl(CustomerProfileRepository customerProfileRepository, 
                                CustomerProfilePublisher customerProfilePublisher, 
                                CustomerProfileService customerProfileService) {
        this.customerProfileRepository = customerProfileRepository;
        this.customerProfilePublisher = customerProfilePublisher;
        this.customerProfileService = customerProfileService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.customer-profile-requested}")
    @Override
    public void handlePredictionRequestedEvent(PredictionRequestedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        long startTime = System.currentTimeMillis();
        String predictionId = event.getPredictionId() != null ? event.getPredictionId().toString() : "null";
        String customerId = event.getCustomerId() != null ? event.getCustomerId().toString() : "null";
        
        logger.info("📥 [CUSTOMER] Received PredictionRequestedEvent - PredictionId: {}, CustomerId: {}, EmployeeId: {}, DeliveryTag: {}", 
            predictionId, customerId, event.getEmployeeId(), deliveryTag);
        
        try {
            // Validation
            if (event.getPredictionId() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Prediction ID is required - DeliveryTag: {}", deliveryTag);
                throw new RuntimeException("Prediction ID is required");
            }
            if (event.getCustomerId() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Customer ID is required - PredictionId: {}, DeliveryTag: {}", 
                    predictionId, deliveryTag);
                throw new RuntimeException("Customer ID is required");
            }
            if (event.getEmployeeId() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Employee ID is required - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
                    predictionId, customerId, deliveryTag);
                throw new RuntimeException("Employee ID is required");
            }

            logger.debug("🔍 [CUSTOMER] Fetching customer profile from database - CustomerId: {}", customerId);
            
            // Lấy customer profile
            CustomerProfile customerProfile = customerProfileRepository.findById(event.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("❌ [CUSTOMER] Customer profile not found - CustomerId: {}, PredictionId: {}", 
                        customerId, predictionId);
                    return new RuntimeException("Customer profile not found");
                });

            logger.info("✅ [CUSTOMER] Customer profile found - CustomerId: {}, FullName: {}, Age: {}, Experience: {}", 
                customerId, customerProfile.getFullName(), customerProfile.getAge(), customerProfile.getExperience());

            // Tạo customer enriched event
            logger.debug("🔄 [CUSTOMER] Building CustomerEnrichedEventDto - PredictionId: {}", predictionId);
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
            logger.info("📤 [CUSTOMER→PREDICTION] Publishing CustomerEnrichedEvent - PredictionId: {}, CustomerId: {}", 
                predictionId, customerId);
            customerProfilePublisher.publishCustomerProfileEnrichedEvent(customerEnrichedEventDto);

            // Acknowledge thành công - chỉ ack sau khi tất cả operations thành công
            try {
                if (channel.isOpen()) {
                    channel.basicAck(deliveryTag, false);
                    logger.debug("✅ [CUSTOMER] Message acknowledged - DeliveryTag: {}", deliveryTag);
                } else {
                    logger.warn("⚠️ [CUSTOMER] Channel is closed, cannot acknowledge message - DeliveryTag: {}", deliveryTag);
                }
            } catch (IOException ackException) {
                logger.error("❌ [CUSTOMER] Failed to acknowledge message - DeliveryTag: {}, Error: {}", 
                    deliveryTag, ackException.getMessage(), ackException);
                // Don't throw - message already processed successfully
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            logger.info("✅ [CUSTOMER] Successfully processed PredictionRequestedEvent - PredictionId: {}, CustomerId: {}, ProcessingTime: {}ms, DeliveryTag: {}", 
                predictionId, customerId, processingTime, deliveryTag);
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("❌ [CUSTOMER] Failed to process PredictionRequestedEvent - PredictionId: {}, CustomerId: {}, ProcessingTime: {}ms, DeliveryTag: {}, Error: {}", 
                predictionId, customerId, processingTime, deliveryTag, e.getMessage(), e);
            try {
                // Reject và không requeue
                channel.basicNack(deliveryTag, false, false);
                logger.warn("⚠️ [CUSTOMER] Message rejected and not requeued - DeliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                logger.error("❌ [CUSTOMER] Failed to acknowledge/reject message - DeliveryTag: {}, Error: {}", 
                    deliveryTag, ioException.getMessage(), ioException);
                throw new RuntimeException("Failed to acknowledge message", ioException);
            }
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.prediction-completed}")
    @Override
    public void handlePredictionCompletedEvent(PredictionCompletedEventDto event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        long startTime = System.currentTimeMillis();
        String predictionId = event.getPredictionId() != null ? event.getPredictionId().toString() : "null";
        String customerId = event.getCustomerId() != null ? event.getCustomerId().toString() : "null";

        logger.info("📥 [CUSTOMER] Received PredictionCompletedEvent - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
            predictionId, customerId, deliveryTag);

        try {
            // Validation
            if (event.getPredictionId() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Prediction ID is required - DeliveryTag: {}", deliveryTag);
                throw new RuntimeException("Prediction ID is required");
            }
            if (event.getCustomerId() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Customer ID is required - PredictionId: {}, DeliveryTag: {}", 
                    predictionId, deliveryTag);
                throw new RuntimeException("Customer ID is required");
            }
            if (event.getResultLabel() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Result label is required - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
                    predictionId, customerId, deliveryTag);
                throw new RuntimeException("Result label is required");
            }
            if (event.getProbability() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Probability is required - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
                    predictionId, customerId, deliveryTag);
                throw new RuntimeException("Probability is required");
            }
            if (event.getCompletedAt() == null) {
                logger.error("❌ [CUSTOMER] Validation failed: Completed at is required - PredictionId: {}, CustomerId: {}, DeliveryTag: {}", 
                    predictionId, customerId, deliveryTag);
                throw new RuntimeException("Completed at is required");
            }

            logger.info("✅ [CUSTOMER] PredictionCompletedEvent received - PredictionId: {}, CustomerId: {}, ResultLabel: {}, Probability: {}, CompletedAt: {}", 
                predictionId, customerId, event.getResultLabel(), event.getProbability(), event.getCompletedAt());

            if (event.getResultLabel()) {
                customerProfileService.approveCustomer(event.getCustomerId());
            } else if (!event.getResultLabel()) {
                customerProfileService.rejectCustomer(event.getCustomerId());
            } else {
                logger.error("❌ [CUSTOMER] Error in saving personal loan status - PredictionId: {}, CustomerId: {}, ResultLabel: {}, DeliveryTag: {}", 
                    predictionId, customerId, event.getResultLabel(), deliveryTag);
            }

            logger.info("✅ [CUSTOMER] Customer profile updated - CustomerId: {}, ResultLabel: {}, Probability: {}, CompletedAt: {}", 
                event.getCustomerId(), event.getResultLabel(), event.getProbability(), event.getCompletedAt());

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("❌ [CUSTOMER] Failed to process PredictionCompletedEvent - PredictionId: {}, CustomerId: {}, ProcessingTime: {}ms, DeliveryTag: {}, Error: {}", 
                predictionId, customerId, processingTime, deliveryTag, e.getMessage(), e);
            try {
                // Reject và không requeue
                channel.basicNack(deliveryTag, false, false);
                logger.warn("⚠️ [CUSTOMER] Message rejected and not requeued - DeliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                logger.error("❌ [CUSTOMER] Failed to acknowledge/reject message - DeliveryTag: {}, Error: {}", 
                    deliveryTag, ioException.getMessage(), ioException);
                throw new RuntimeException("Failed to acknowledge message", ioException);
            }
        }
    }
}
