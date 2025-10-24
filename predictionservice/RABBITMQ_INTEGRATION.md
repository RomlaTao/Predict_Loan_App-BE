# RabbitMQ Integration for Prediction Service

## Overview
The Prediction Service has been refactored to use RabbitMQ for asynchronous communication with the ML Model API instead of direct HTTP calls.

## Architecture Changes

### Before (Direct API Call)
```
Client Request → PredictionService → HTTP Call → ML Model API → Response
```

### After (RabbitMQ Integration)
```
Client Request → PredictionService → RabbitMQ → ML Model Consumer → Response
```

## Components

### 1. RabbitMQ Configuration (`RabbitMQConfig.java`)
- Configures exchanges, queues, and bindings
- Sets up message converters
- Defines connection factory

### 2. Message DTOs
- `MLModelRequestDto`: Input validation for prediction requests
- `MLModelResponseDto`: Structured response format
- `PredictionMessageDto`: RabbitMQ message wrapper

### 3. RabbitMQ Producer Service (`RabbitMQProducerService.java`)
- Sends prediction requests to ML Model via RabbitMQ
- Handles message correlation and timeouts
- Manages response processing

### 4. Response Listener (`PredictionResponseListener.java`)
- Listens for responses from ML Model
- Processes completed predictions
- Handles error responses

### 5. Refactored MLModelService
- Now uses RabbitMQ instead of direct HTTP calls
- Maintains same interface for backward compatibility
- Improved error handling and logging

## Configuration

### Application Properties
```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/

# Queue Configuration
rabbitmq.exchange=ml.prediction.exchange
rabbitmq.queue.request=ml.prediction.request
rabbitmq.queue.response=ml.prediction.response
rabbitmq.timeout=30000
```

### Environment Variables
```bash
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_EXCHANGE=ml.prediction.exchange
RABBITMQ_QUEUE_REQUEST=ml.prediction.request
RABBITMQ_QUEUE_RESPONSE=ml.prediction.response
```

## Message Flow

### 1. Request Flow
1. Client sends prediction request to PredictionService
2. `MLModelService.predict()` calls `RabbitMQProducerService`
3. Producer creates `PredictionMessageDto` with correlation ID
4. Message sent to `ml.prediction.request` queue
5. ML Model consumer processes the request

### 2. Response Flow
1. ML Model processes request and creates response
2. Response sent to `ml.prediction.response` queue
3. `PredictionResponseListener` receives the response
4. Response correlated with original request
5. Client receives prediction result

## Benefits

### 1. **Decoupling**
- Services are no longer tightly coupled
- ML Model can be updated independently
- Better fault isolation

### 2. **Scalability**
- Multiple ML Model consumers can be deployed
- Load balancing across consumers
- Horizontal scaling capability

### 3. **Reliability**
- Message persistence and durability
- Automatic retry mechanisms
- Dead letter queue handling

### 4. **Performance**
- Asynchronous processing
- Non-blocking operations
- Better resource utilization

### 5. **Monitoring**
- Message queue metrics
- Processing time tracking
- Error rate monitoring

## Development Setup

### 1. Start Infrastructure
```bash
# Start RabbitMQ, Redis, and MySQL
docker-compose up -d
```

### 2. Start Prediction Service
```bash
# Run the Spring Boot application
mvn spring-boot:run
```

### 3. Start ML Model Consumer
```bash
# In predictionmodel directory
python consumer.py
```

## Testing

### 1. Health Check
```bash
curl http://localhost:8008/actuator/health
```

### 2. Test Prediction
```bash
curl -X POST http://localhost:8008/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "Age": 45,
    "Experience": 20,
    "Income": 74,
    "Family": 2,
    "Education": 2,
    "Mortgage": 56,
    "Securities_Account": 0,
    "CD_Account": 0,
    "Online": 1,
    "CreditCard": 0,
    "ann_CCAvg": 23.0
  }'
```

### 3. Monitor RabbitMQ
- Access RabbitMQ Management: http://localhost:15672
- Username: guest, Password: guest
- View queues, exchanges, and message flow

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Check if RabbitMQ is running
   - Verify connection parameters
   - Check firewall settings

2. **Message Not Processed**
   - Verify queue bindings
   - Check consumer status
   - Review error logs

3. **Timeout Issues**
   - Increase timeout configuration
   - Check ML Model consumer status
   - Verify message correlation

### Logs
- Application logs: Check console output
- RabbitMQ logs: `docker logs <rabbitmq-container>`
- ML Model logs: Check predictionmodel logs

## Migration Notes

### Breaking Changes
- None - API interface remains the same
- Backward compatible with existing clients

### Configuration Changes
- Added RabbitMQ configuration
- Deprecated direct API model configuration
- Environment variables updated

### Performance Impact
- Initial setup may have slight latency
- Overall performance improved with async processing
- Better resource utilization
