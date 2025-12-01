package com.predict_app.analysticservice.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.prediction-completed}")
    private String predictionCompletedExchangeName;

    @Value("${rabbitmq.queue.prediction-completed-analytics}")
    private String predictionCompletedAnalyticsQueueName;

    @Value("${rabbitmq.routing-key.prediction-completed-analytics}")
    private String predictionCompletedAnalyticsRoutingKey;
    
    // Exchange Prediction Completed
    @Bean
    public TopicExchange predictionCompletedExchange() {
        return new TopicExchange(predictionCompletedExchangeName);
    }
    
    // Queue Prediction Completed
    @Bean
    public Queue predictionCompletedAnalyticsQueue() {
        return QueueBuilder.durable(predictionCompletedAnalyticsQueueName).build();
    }
    
    // Bindings
    @Bean
    public Binding predictionCompletedAnalyticsBinding() {
        return BindingBuilder
                .bind(predictionCompletedAnalyticsQueue())
                .to(predictionCompletedExchange())
                .with(predictionCompletedAnalyticsRoutingKey);
    }
    // Message Converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        // Ensure Java Time (LocalDateTime, etc.) is supported
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // Listener Container Factory
    // Note: Using MANUAL acknowledgeMode because we handle ack/nack manually in listeners
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);  // Manual ack/nack
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
