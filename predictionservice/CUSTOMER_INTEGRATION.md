# Customer Service Integration

## Overview
PredictionService đã được tích hợp với CustomerService để tự động lấy dữ liệu khách hàng và tạo prediction thông qua ML model.

## Architecture Flow

```
Client Request → PredictionService → CustomerService → ML Model → PredictionService → Database → Client Response
```

## Components

### 1. CustomerServiceClient
- **File**: `clients/CustomerServiceClient.java`
- **Purpose**: Feign client để giao tiếp với CustomerService
- **Endpoints**:
  - `GET /api/customers/{customerId}` - Lấy thông tin khách hàng
  - `GET /api/customers/{customerId}/ml-data` - Lấy dữ liệu ML

### 2. CustomerDataService
- **File**: `services/CustomerDataService.java`
- **Purpose**: Service layer để xử lý dữ liệu khách hàng
- **Methods**:
  - `getCustomerData(UUID customerId)` - Lấy thông tin khách hàng
  - `getCustomerMLData(UUID customerId)` - Lấy dữ liệu ML
  - `convertCustomerToMLRequest(CustomerProfileResponseDto)` - Convert dữ liệu

### 3. Updated PredictionServiceImpl
- **File**: `services/impl/PredictionServiceImpl.java`
- **Changes**:
  - Tích hợp `CustomerDataService` và `MLModelService`
  - Luồng xử lý mới: Lấy data → Gửi ML → Lưu kết quả
  - Error handling và logging

## API Endpoints

### CustomerService - New Endpoint
```http
GET /api/customers/{customerId}/ml-data
```

**Response:**
```json
{
  "age": 45,
  "experience": 20,
  "income": 74.0,
  "family": 2,
  "education": 2,
  "mortgage": 56.0,
  "securitiesAccount": 0,
  "cdAccount": 0,
  "online": 1,
  "creditCard": 0,
  "annCcAvg": 23.0
}
```

### PredictionService - Updated Flow
```http
POST /api/predictions
{
  "customerId": "uuid",
  "employeeId": "uuid"
}
```

**New Flow:**
1. Lấy customer data từ CustomerService
2. Convert sang MLModelRequestDto
3. Gửi đến ML model qua RabbitMQ
4. Nhận kết quả và lưu vào database
5. Trả response cho client

## Configuration

### PredictionService Properties
```properties
# Customer Service URL
customer.service.url=http://localhost:8007

# Feign Client Configuration
feign.client.config.default.connect-timeout=5000
feign.client.config.default.read-timeout=10000
```

### Environment Variables
```bash
CUSTOMER_SERVICE_URL=http://localhost:8007
FEIGN_CONNECT_TIMEOUT=5000
FEIGN_READ_TIMEOUT=10000
```

## Dependencies

### PredictionService
```xml
<!-- OpenFeign for service-to-service communication -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Application Configuration
```java
@SpringBootApplication
@EnableFeignClients  // Enable Feign clients
public class PredictionserviceApplication {
    // ...
}
```

## Data Flow

### 1. Request Processing
```
Client → PredictionService.createPrediction()
       ↓
CustomerDataService.getCustomerMLData(customerId)
       ↓
CustomerServiceClient.getCustomerMLData(customerId)
       ↓
CustomerService.getCustomerMLData(customerId)
       ↓
Return MLModelRequestDto
```

### 2. ML Processing
```
MLModelRequestDto → MLModelService.predict()
                  ↓
RabbitMQProducerService.sendPredictionRequest()
                  ↓
RabbitMQ → ML Model Consumer
                  ↓
ML Model Processing
                  ↓
RabbitMQ Response
```

### 3. Result Storage
```
MLModelResponseDto → Prediction Entity
                   ↓
PredictionRepository.save()
                   ↓
Database Storage
                   ↓
Return PredictionResponseDto
```

## Error Handling

### CustomerService Errors
- **Customer not found**: Return 404 with error message
- **Service unavailable**: Return 503 with retry suggestion
- **Timeout**: Return 408 with timeout message

### ML Model Errors
- **ML Model unavailable**: Store prediction with FAILED status
- **Invalid data**: Store prediction with error message
- **Timeout**: Store prediction with timeout error

### Database Errors
- **Connection failed**: Return 500 with database error
- **Constraint violation**: Return 400 with validation error

## Testing

### 1. Test CustomerService Integration
```bash
# Test customer ML data endpoint
curl -X GET http://localhost:8007/api/customers/{customerId}/ml-data
```

### 2. Test PredictionService Integration
```bash
# Test prediction creation
curl -X POST http://localhost:8008/api/predictions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-uuid",
    "employeeId": "employee-uuid"
  }'
```

### 3. Test Full Flow
```bash
# 1. Create customer
curl -X POST http://localhost:8007/api/customers \
  -H "Content-Type: application/json" \
  -d '{...customer data...}'

# 2. Create prediction
curl -X POST http://localhost:8008/api/predictions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-uuid-from-step-1",
    "employeeId": "employee-uuid"
  }'
```

## Monitoring

### Logs
- **CustomerService**: Log customer data requests
- **PredictionService**: Log prediction creation and ML processing
- **RabbitMQ**: Log message flow and processing

### Metrics
- **Response times**: CustomerService and ML model response times
- **Success rates**: Prediction success/failure rates
- **Error rates**: Service error rates and types

## Benefits

### 1. **Data Consistency**
- Single source of truth cho customer data
- Automatic data synchronization
- Reduced data duplication

### 2. **Service Decoupling**
- Services communicate qua well-defined APIs
- Independent deployment và scaling
- Clear service boundaries

### 3. **Error Handling**
- Comprehensive error handling
- Graceful degradation
- Detailed logging và monitoring

### 4. **Performance**
- Efficient data retrieval
- Caching opportunities
- Async processing với RabbitMQ

## Future Enhancements

### 1. **Caching**
- Cache customer data trong PredictionService
- Redis integration cho performance
- Cache invalidation strategies

### 2. **Event-Driven Updates**
- Customer data changes → notify PredictionService
- Real-time data synchronization
- Event sourcing patterns

### 3. **Advanced Error Handling**
- Circuit breaker patterns
- Retry mechanisms
- Fallback strategies

### 4. **Monitoring & Observability**
- Distributed tracing
- Performance metrics
- Health checks
