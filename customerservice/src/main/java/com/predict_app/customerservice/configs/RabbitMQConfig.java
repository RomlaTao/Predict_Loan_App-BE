package com.predict_app.customerservice.configs;

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

    @Value("${rabbitmq.exchange.prediction-completed}")
    private String predictionCompletedExchangeName;

    @Value("${rabbitmq.queue.prediction-completed}")
    private String predictionCompletedQueueName;

    @Value("${rabbitmq.routing-key.prediction-completed}")
    private String predictionCompletedRoutingKey;

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

    // Exchange Prediction Completed
    @Bean
    public TopicExchange predictionCompletedExchange() {
        return new TopicExchange(predictionCompletedExchangeName);
    }

    // Queue Prediction Completed
    @Bean
    public Queue predictionCompletedQueue() {
        return QueueBuilder.durable(predictionCompletedQueueName).build();
    }

    // Bindings
    @Bean
    public Binding predictionCompletedBinding() {
        return BindingBuilder
                .bind(predictionCompletedQueue())
                .to(predictionCompletedExchange())
                .with(predictionCompletedRoutingKey);
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
