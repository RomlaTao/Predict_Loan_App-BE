package com.predict_app.customerservice.configs;

import org.springframework.context.annotation.Configuration;

/**
 * ListenerConfig - Previously contained duplicate listener container factory
 * 
 * Note: Listener container factory is now configured in RabbitMQConfig
 * with MANUAL acknowledgeMode to match the manual ack/nack handling in listeners.
 * This configuration class is kept for future use or can be removed.
 */
@Configuration
public class ListenerConfig {
    // Configuration moved to RabbitMQConfig to avoid duplicate bean definitions
    // Listener container factory is now defined as 'rabbitListenerContainerFactory' 
    // in RabbitMQConfig with MANUAL acknowledgeMode
}