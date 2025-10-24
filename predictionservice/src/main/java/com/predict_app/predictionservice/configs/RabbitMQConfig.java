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

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.request}")
    private String requestQueueName;

    @Value("${rabbitmq.queue.response}")
    private String responseQueueName;

    // Exchange
    @Bean
    public TopicExchange predictionExchange() {
        return new TopicExchange(exchangeName);
    }

    // Request Queue
    @Bean
    public Queue requestQueue() {
        return QueueBuilder.durable(requestQueueName).build();
    }

    // Response Queue
    @Bean
    public Queue responseQueue() {
        return QueueBuilder.durable(responseQueueName).build();
    }

    // Bindings
    @Bean
    public Binding requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(predictionExchange())
                .with("prediction.request");
    }

    @Bean
    public Binding responseBinding() {
        return BindingBuilder
                .bind(responseQueue())
                .to(predictionExchange())
                .with("prediction.response");
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
