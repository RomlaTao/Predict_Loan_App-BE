from pydantic_settings import BaseSettings
from typing import Optional

class Settings(BaseSettings):
    # Application
    app_name: str = "ML Model API"
    debug: bool = False
    host: str = "0.0.0.0"
    port: int = 8009
    
    # RabbitMQ
    rabbitmq_host: str = "localhost"
    rabbitmq_port: int = 5672
    rabbitmq_username: str = "guest"
    rabbitmq_password: str = "guest"
    rabbitmq_virtual_host: str = "/"
    
    # Legacy prediction queues (kept for backward compatibility)
    prediction_request_queue: str = "ml.prediction.request"
    prediction_response_queue: str = "ml.prediction.response"
    prediction_exchange: str = "ml.prediction.exchange"

    # New event-driven integration (align with Java services)
    model_predict_requested_exchange: str = "model-predict-requested-exchange"
    model_predict_requested_queue: str = "model-predict-requested-queue"
    model_predict_requested_routing_key: str = "model-predict.requested"

    model_predict_completed_exchange: str = "model-predict-completed-exchange"
    model_predict_completed_queue: str = "model-predict-completed-queue"
    model_predict_completed_routing_key: str = "model-predict.completed"
    
    # Model
    model_path: str = "models/knn_model.joblib"
    scaler_path: str = "models/scaler.joblib"
    
    # Redis (for caching)
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_db: int = 0
    
    class Config:
        env_file = ".env"

settings = Settings()
