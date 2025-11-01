# Prediction Service

## Mô tả
Prediction Service là service để hứng kết quả dự đoán từ ML Model và quản lý prediction history. Service này kết nối với ML Model Service để lấy kết quả dự đoán khoản vay cá nhân.

## Cấu hình

### Database
```sql
CREATE DATABASE predictionservice;
CREATE USER 'predictionservice'@'%' IDENTIFIED BY 'predictionservice';
GRANT ALL PRIVILEGES ON predictionservice.* TO 'predictionservice'@'%';
FLUSH PRIVILEGES;
```

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3308/predictionservice?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=predictionservice
SPRING_DATASOURCE_PASSWORD=predictionservice
SPRING_JPA_HIBERNATE_DDL_AUTO=update
EUREKA_SERVER_URL=http://localhost:8761/eureka/
ML_MODEL_SERVICE_URL=http://localhost:8009
ML_MODEL_SERVICE_TIMEOUT=30000
```

## API Endpoints

### 1. Tạo Prediction Request
```bash
curl -X POST http://localhost:8008/api/predictions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-uuid",
    "employeeId": "employee-uuid"
  }'
```

### 2. Lấy Prediction theo ID
```bash
curl -X GET http://localhost:8008/api/predictions/{prediction-id}
```

### 3. Lấy Predictions theo Customer ID
```bash
curl -X GET http://localhost:8008/api/predictions/customer/{customer-id}
```

### 4. Lấy Predictions theo Employee ID
```bash
curl -X GET http://localhost:8008/api/predictions/employee/{employee-id}
```

### 5. Lấy tất cả Predictions
```bash
curl -X GET http://localhost:8008/api/predictions
```

### 6. Cập nhật trạng thái Prediction
```bash
curl -X PUT http://localhost:8008/api/predictions/{prediction-id}/status?status=COMPLETED
```

## Test Cases

### Test Case 1: Tạo Prediction Request thành công
```bash
curl -X POST http://localhost:8008/api/predictions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "123e4567-e89b-12d3-a456-426614174000",
    "employeeId": "123e4567-e89b-12d3-a456-426614174001"
  }'
```

**Expected Response:**
```json
{
  "predictionId": "uuid-generated",
  "customerId": "123e4567-e89b-12d3-a456-426614174000",
  "employeeId": "123e4567-e89b-12d3-a456-426614174001",
  "status": "COMPLETED",
  "inputData": "{\"age\":30,\"experience\":5,\"income\":75.0,\"family\":2,\"ccAvg\":4.2,\"education\":2,\"mortgage\":150.0,\"securitiesAccount\":true,\"cdAccount\":true,\"online\":true,\"creditCard\":true}",
  "predictionResult": "{\"prediction\":true,\"confidence\":0.85,\"message\":\"Loan approved\"}",
  "confidence": 0.85,
  "errorMessage": null,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "completedAt": "2024-01-01T10:00:01"
}
```

### Test Case 2: Lấy Prediction theo ID
```bash
curl -X GET http://localhost:8008/api/predictions/{prediction-id}
```

**Expected Response:**
```json
{
  "predictionId": "uuid",
  "customerId": "customer-uuid",
  "employeeId": "employee-uuid",
  "status": "COMPLETED",
  "inputData": "...",
  "predictionResult": "...",
  "confidence": 0.85,
  "errorMessage": null,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "completedAt": "2024-01-01T10:00:01"
}
```

### Test Case 3: Lấy Predictions theo Customer
```bash
curl -X GET http://localhost:8008/api/predictions/customer/{customer-id}
```

**Expected Response:**
```json
[
  {
    "predictionId": "uuid-1",
    "customerId": "customer-uuid",
    "employeeId": "employee-uuid",
    "status": "COMPLETED",
    "confidence": 0.85,
    ...
  }
]
```

### Test Case 4: Cập nhật trạng thái Prediction
```bash
curl -X PUT http://localhost:8008/api/predictions/{prediction-id}/status?status=FAILED
```

**Expected Response:**
```json
{
  "predictionId": "uuid",
  "status": "FAILED",
  "completedAt": "2024-01-01T10:00:00",
  ...
}
```

## Luồng hoạt động

1. **Nhân viên tạo prediction request** → `POST /api/predictions`
2. **Service gọi ML Model** → `http://localhost:5000/predict`
3. **Lưu kết quả prediction** → Database
4. **Trả về kết quả** → Response với status COMPLETED/FAILED

## Chạy ứng dụng

```bash
cd predictionservice
mvn spring-boot:run
```

Ứng dụng sẽ chạy trên port 8008 và đăng ký với Eureka Server.

## Kết nối với ML Model Service

Prediction Service sẽ gọi ML Model Service tại `http://localhost:8009/predict` với payload:

```json
{
  "age": 30,
  "experience": 5,
  "income": 75.0,
  "family": 2,
  "ccAvg": 4.2,
  "education": 2,
  "mortgage": 150.0,
  "securitiesAccount": true,
  "cdAccount": true,
  "online": true,
  "creditCard": true
}
```

Và nhận response:

```json
{
  "prediction": true,
  "confidence": 0.85,
  "message": "Loan approved"
}
```
