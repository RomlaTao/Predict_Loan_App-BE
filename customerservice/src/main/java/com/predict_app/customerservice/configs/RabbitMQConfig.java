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

    @Value("${rabbitmq.exchange.customer-profile-response}")
    private String customerProfileResponseExchangeName;
    
    @Value("${rabbitmq.queue.customer-profile-response}")
    private String customerProfileResponseQueueName;

    @Value("${rabbitmq.routing-key.customer-profile-response}")
    private String customerProfileResponseRoutingKey;

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

    // Exchange Customer Profile Response
    @Bean
    public TopicExchange customerProfileResponseExchange() {
        return new TopicExchange(customerProfileResponseExchangeName);
    }

    // Queue Customer Profile Response
    @Bean
    public Queue customerProfileResponseQueue() {
        return QueueBuilder.durable(customerProfileResponseQueueName).build();
    }

    // Bindings
    @Bean
    public Binding customerProfileResponseBinding() {
        return BindingBuilder
                .bind(customerProfileResponseQueue())
                .to(customerProfileResponseExchange())
                .with(customerProfileResponseRoutingKey);
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
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
