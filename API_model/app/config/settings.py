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
    
    # Queues
    prediction_request_queue: str = "ml.prediction.request"
    prediction_response_queue: str = "ml.prediction.response"
    prediction_exchange: str = "ml.prediction.exchange"
    
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
