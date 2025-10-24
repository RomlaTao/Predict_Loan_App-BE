# ML Model API - Loan Prediction Service

## Overview
This is a refactored ML Model API service that provides loan prediction capabilities using machine learning models. The service is built with FastAPI and integrates with RabbitMQ for asynchronous message processing.

## Architecture

### Components
- **FastAPI Application**: REST API for direct predictions
- **RabbitMQ Consumer**: Asynchronous message processing
- **ML Model Manager**: Handles model loading and predictions
- **Configuration Management**: Environment-based settings

### Features
- ✅ FastAPI REST API
- ✅ RabbitMQ Integration
- ✅ Structured Logging
- ✅ Data Validation
- ✅ Error Handling
- ✅ Docker Support
- ✅ Health Checks

## Project Structure

```
predictionmodel/
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
│   │   ├── prediction_service.py
│   │   └── rabbitmq_service.py
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
├── consumer.py                 # RabbitMQ consumer runner
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

3. **Run RabbitMQ and Redis:**
```bash
docker-compose up rabbitmq redis -d
```

4. **Start the API:**
```bash
python -m uvicorn app.main:app --host 0.0.0.0 --port 8009 --reload
```

5. **Start the consumer (in another terminal):**
```bash
python consumer.py
```

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

### Direct Prediction
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

## RabbitMQ Integration

### Message Flow
1. **Request**: Prediction request sent to `ml.prediction.request` queue
2. **Processing**: Consumer processes the request using ML model
3. **Response**: Result sent to `ml.prediction.response` queue

### Message Format
```json
{
    "correlation_id": "uuid",
    "request_id": "uuid",
    "request": {
        "Age": 45,
        "Experience": 20,
        ...
    },
    "response": {
        "prediction": true,
        "confidence": 0.85,
        "probabilities": {
            "Không chấp nhận": 0.15,
            "Chấp nhận": 0.85
        },
        "message": "Prediction completed successfully",
        "timestamp": "2024-01-01T00:00:00"
    },
    "timestamp": "2024-01-01T00:00:00",
    "status": "COMPLETED"
}
```

## Configuration

### Environment Variables
- `RABBITMQ_HOST`: RabbitMQ host (default: localhost)
- `RABBITMQ_PORT`: RabbitMQ port (default: 5672)
- `RABBITMQ_USERNAME`: RabbitMQ username (default: guest)
- `RABBITMQ_PASSWORD`: RabbitMQ password (default: guest)
- `MODEL_PATH`: Path to ML model file (default: knn_model.joblib)
- `SCALER_PATH`: Path to scaler file (default: scaler.joblib)

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
