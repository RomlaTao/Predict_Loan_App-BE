# ML Model Service - Loan Prediction Service

## Overview
This is a refactored ML Model Service that provides loan prediction capabilities using machine learning models. The service is built with FastAPI and integrates with RabbitMQ for asynchronous message processing and Eureka for service discovery.

## Architecture

### Modules & Responsibilities
- **app/main.py**: Khởi tạo FastAPI, đăng ký Eureka, khởi động PredictionConsumer ở background, expose các endpoint `/health`, `/predict`, `/model/info`.
- **app/config/settings.py**: Quản lý cấu hình (Pydantic Settings), map environment variables (RabbitMQ, Eureka, model path, …).
- **app/models/ml_model.py**: Quản lý model/scaler (load, preprocess, predict). Lưu ý tên feature khớp lúc fit: `"Securities Account"`, `"CD Account"` (có dấu cách).
- **app/models/schemas.py**: Định nghĩa schema Pydantic cho event-driven (ModelPredictRequestedEvent, ModelPredictCompletedEvent) và REST.
- **app/services/prediction_service.py**: Business logic dự đoán (giao diện cao hơn quanh ml_model).
- **app/services/rabbitmq_service.py**: Kết nối RabbitMQ, khai báo exchange/queue/binding, publish sự kiện `model.predict.completed` (serialize datetime ISO-8601 với ký tự 'T').
- **app/services/eureka_service.py**: Đăng ký/hủy đăng ký Eureka (service discovery).
- **app/consumers/prediction_consumer.py**: Consumer nhận `model.predict.requested`, chuyển đổi input → feature, suy luận, publish kết quả.
- **app/utils/logger.py**: Cấu hình log (console + file), structured logging.
- **app/exceptions/**: Các exception tuỳ biến.

### Key Capabilities
- ✅ REST API qua FastAPI
- ✅ Event-driven qua RabbitMQ (topic exchange)
- ✅ Eureka service discovery
- ✅ Structured logging, trace end-to-end
- ✅ Health checks & Dockerized

## Project Structure

```
ml-model-service/
├── app/
│   ├── __init__.py
│   ├── main.py                 # FastAPI application
│   ├── config/
│   │   ├── __init__.py
│   │   └── settings.py         # Configuration settings
│   ├── models/
│   │   ├── __init__.py
│   │   ├── ml_model.py         # ML model manager
│   │   └── schemas.py          # Pydantic schemas
│   ├── services/
│   │   ├── __init__.py
│   │   ├── prediction_service.py   # Business logic for prediction
│   │   ├── eureka_service.py       # Eureka registration/deregistration
│   │   └── rabbitmq_service.py     # RabbitMQ connection & publishing
│   ├── consumers/
│   │   ├── __init__.py
│   │   └── prediction_consumer.py
│   ├── utils/
│   │   ├── __init__.py
│   │   ├── logger.py
│   │   └── validators.py
│   └── exceptions/
│       ├── __init__.py
│       └── custom_exceptions.py
├── consumer.py                 # Legacy runner (consumer is now started from app/main.py)
├── requirements.txt
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Installation

### Local Development

1. **Install dependencies:**
```bash
pip install -r requirements.txt
```

2. **Set up environment variables:**
```bash
# Copy .env file (create if not exists)
cp .env.example .env
```

3. **Run RabbitMQ (và Redis nếu cần):**
```bash
docker-compose up rabbitmq -d
```

4. **Start the API:**
```bash
python -m uvicorn app.main:app --host 0.0.0.0 --port 8009 --reload
```

5. **Consumer**: Được khởi động tự động (background thread) từ `app/main.py` khi API start.

### Docker Deployment

1. **Build and run with Docker Compose:**
```bash
docker-compose up --build
```

## API Endpoints

### Health Check
```http
GET /health
```

### Direct Prediction (debug/local)
```http
POST /predict
Content-Type: application/json

{
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
}
```

### Model Information
```http
GET /model/info
```

## RabbitMQ Integration (Event-Driven)

### Exchanges, Queues, Routing Keys
- Exchange: `model.predict.exchange` (topic)
- Request Queue: `model.predict.requested` (rk: `model.predict.requested`)
- Completed Queue: `model.predict.completed` (rk: `model.predict.completed`)

### Message Flow (end-to-end)
1. PredictionService publish `ModelPredictRequestedEvent` → `model.predict.exchange` rk `model.predict.requested`.
2. ML Model Service consumer nhận từ `model.predict.requested`, chạy suy luận, publish `ModelPredictCompletedEvent` → `model.predict.exchange` rk `model.predict.completed`.
3. PredictionService lắng nghe `model.predict.completed`, cập nhật kết quả dự đoán.

### Event Schemas
- ModelPredictRequestedEvent
```json
{
  "predictionId": "uuid",
  "customerId": "uuid",
  "input": {
    "age": 45,
    "experience": 20,
    "income": 74.0,
    "family": 2,
    "education": 2,
    "mortgage": 56.0,
    "securitiesAccount": false,
    "cdAccount": false,
    "online": true,
    "creditCard": false,
    "ccAvg": 23.0
  }
}
```

- ModelPredictCompletedEvent (datetime ISO-8601 có ký tự 'T')
```json
{
  "predictionId": "uuid",
  "customerId": "uuid",
  "result": {
    "label": "approve",
    "probability": 0.85,
    "modelVersion": "v1",
    "inferenceTimeMs": 12
  },
  "predictedAt": "2025-11-03T12:25:52.142559"
}
```

## Configuration

### Environment Variables
- RabbitMQ
  - `RABBITMQ_HOST` (default: localhost)
  - `RABBITMQ_PORT` (default: 5672)
  - `RABBITMQ_USERNAME` (default: guest)
  - `RABBITMQ_PASSWORD` (default: guest)
  - `RABBITMQ_VIRTUAL_HOST` (default: /)
- Event Names (đã đồng bộ với PredictionService; có thể override qua env)
  - `MODEL_PREDICT_REQUESTED_EXCHANGE` (default: model.predict.exchange)
  - `MODEL_PREDICT_REQUESTED_QUEUE` (default: model.predict.requested)
  - `MODEL_PREDICT_REQUESTED_ROUTING_KEY` (default: model.predict.requested)
  - `MODEL_PREDICT_COMPLETED_EXCHANGE` (default: model.predict.exchange)
  - `MODEL_PREDICT_COMPLETED_QUEUE` (default: model.predict.completed)
  - `MODEL_PREDICT_COMPLETED_ROUTING_KEY` (default: model.predict.completed)
- Model files
  - `MODEL_PATH` (default: models/knn_model.joblib)
  - `SCALER_PATH` (default: models/scaler.joblib)
- Eureka
  - `EUREKA_ENABLED` (default: true)
  - `EUREKA_SERVER_URL` (default: http://localhost:8761/eureka/)
  - `EUREKA_APP_NAME` (default: ml-model-service)
  - `EUREKA_INSTANCE_HOST` (default: localhost)
  - `EUREKA_INSTANCE_PORT` (default: 8009)

## Logging

Logs are written to:
- Console (INFO level)
- `logs/ml_model.log` (DEBUG level, rotated daily)

## Testing

### Test the API
```bash
# Test health endpoint
curl http://localhost:8009/health

# Test prediction endpoint
curl -X POST http://localhost:8009/predict \
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

## Monitoring

### RabbitMQ Management
Access RabbitMQ management interface at: http://localhost:15672
- Username: guest
- Password: guest

### Health Checks
- API Health: `GET /health`
- Model Status: `GET /model/info`

## Development

### Code Structure
- **Models**: ML model management and data schemas
- **Services**: Business logic and external integrations
- **Consumers**: Message queue processing
- **Utils**: Logging, validation, and utilities
- **Exceptions**: Custom exception classes

### Adding New Features
1. Add new schemas in `app/models/schemas.py`
2. Implement business logic in `app/services/`
3. Add new endpoints in `app/main.py`
4. Update tests accordingly

## Troubleshooting

### Common Issues
1. **Model not loading**: Check file paths in settings
2. **RabbitMQ connection failed**: Verify RabbitMQ is running
3. **Consumer not processing**: Check queue bindings and routing keys

### Logs
Check application logs in `logs/ml_model.log` for detailed error information.
