package com.predict_app.predictionservice.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.customer-profile-requested}")
    private String customerProfileRequestedExchangeName;

    @Value("${rabbitmq.queue.customer-profile-requested}")
    private String customerProfileRequestedQueueName;

    @Value("${rabbitmq.routing-key.customer-profile-requested}")
    private String customerProfileRequestedRoutingKey;

    @Value("${rabbitmq.exchange.customer-profile-enriched}")
    private String customerProfileEnrichedExchangeName;
    
    @Value("${rabbitmq.queue.customer-profile-enriched}")
    private String customerProfileEnrichedQueueName;

    @Value("${rabbitmq.routing-key.customer-profile-enriched}")
    private String customerProfileEnrichedRoutingKey;

    @Value("${rabbitmq.exchange.model-predict-requested}")
    private String modelPredictRequestedExchangeName;

    @Value("${rabbitmq.queue.model-predict-requested}")
    private String modelPredictRequestedQueueName;

    @Value("${rabbitmq.routing-key.model-predict-requested}")
    private String modelPredictRequestedRoutingKey;

    @Value("${rabbitmq.exchange.model-predict-completed}")
    private String modelPredictCompletedExchangeName;

    @Value("${rabbitmq.queue.model-predict-completed}")
    private String modelPredictCompletedQueueName;

    @Value("${rabbitmq.routing-key.model-predict-completed}")
    private String modelPredictCompletedRoutingKey;

    // Exchange Customer Profile Requested
    @Bean
    public TopicExchange customerProfileRequestedExchange() {
        return new TopicExchange(customerProfileRequestedExchangeName);
    }

    // Queue Customer Profile Requested
    @Bean
    public Queue customerProfileRequestedQueue() {
        return QueueBuilder.durable(customerProfileRequestedQueueName).build();
    }

    // Bindings
    @Bean
    public Binding customerProfileRequestedBinding() {
        return BindingBuilder
                .bind(customerProfileRequestedQueue())
                .to(customerProfileRequestedExchange())
                .with(customerProfileRequestedRoutingKey);
    }

    // Exchange Customer Profile Enriched
    @Bean
    public TopicExchange customerProfileEnrichedExchange() {
        return new TopicExchange(customerProfileEnrichedExchangeName);
    }

    // Queue Customer Profile Enriched
    @Bean
    public Queue customerProfileEnrichedQueue() {
        return QueueBuilder.durable(customerProfileEnrichedQueueName).build();
    }

    // Bindings
    @Bean
    public Binding customerProfileEnrichedBinding() {
        return BindingBuilder
                .bind(customerProfileEnrichedQueue())
                .to(customerProfileEnrichedExchange())
                .with(customerProfileEnrichedRoutingKey);
    }

    // Exchange Model Predict Requested
    @Bean
    public TopicExchange modelPredictRequestedExchange() {
        return new TopicExchange(modelPredictRequestedExchangeName);
    }

    // Queue Model Predict Requested
    @Bean
    public Queue modelPredictRequestedQueue() {
        return QueueBuilder.durable(modelPredictRequestedQueueName).build();
    }

    // Bindings
    @Bean
    public Binding modelPredictRequestedBinding() {
        return BindingBuilder
                .bind(modelPredictRequestedQueue())
                .to(modelPredictRequestedExchange())
                .with(modelPredictRequestedRoutingKey);
    }

    // Exchange Model Predict Completed
    @Bean
    public TopicExchange modelPredictCompletedExchange() {
        return new TopicExchange(modelPredictCompletedExchangeName);
    }
    
    // Queue Model Predict Completed
    @Bean
    public Queue modelPredictCompletedQueue() {
        return QueueBuilder.durable(modelPredictCompletedQueueName).build();
    }
    
    // Bindings
    @Bean
    public Binding modelPredictCompletedBinding() {
        return BindingBuilder
                .bind(modelPredictCompletedQueue())
                .to(modelPredictCompletedExchange())
                .with(modelPredictCompletedRoutingKey);
    }
    
    // Message Converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // Listener Container Factory
    // Note: Using MANUAL acknowledgeMode because we handle ack/nack manually in listeners
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);  // Manual ack/nack
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
