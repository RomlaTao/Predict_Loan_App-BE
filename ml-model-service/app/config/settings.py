from pydantic_settings import BaseSettings
from pydantic import ConfigDict
from typing import Optional

class Settings(BaseSettings):
    model_config = ConfigDict(
        protected_namespaces=('settings_',),  # Fix Pydantic warnings for "model_" prefix
        env_file=".env"  # Load environment variables from .env file
    )
    # Application
    app_name: str = "ML Model Service"
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

    # New event-driven integration (align with Java services defaults)
    model_predict_requested_exchange: str = "model.predict.exchange"
    model_predict_requested_queue: str = "model.predict.requested"
    model_predict_requested_routing_key: str = "model.predict.requested"

    model_predict_completed_exchange: str = "model.predict.exchange"
    model_predict_completed_queue: str = "model.predict.completed"
    model_predict_completed_routing_key: str = "model.predict.completed"
    
    # Model
    model_path: str = "models/knn_model.joblib"
    scaler_path: str = "models/scaler.joblib"
    
    # Redis (for caching)
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_db: int = 0
    
    # Eureka Service Discovery
    eureka_enabled: bool = True
    eureka_server_url: str = "http://localhost:8761/eureka/"
    eureka_app_name: str = "ml-model-service"
    eureka_instance_host: str = "localhost"
    eureka_instance_port: int = 8009
    eureka_lease_renewal_interval: int = 30  # seconds
    eureka_lease_duration: int = 90  # seconds

settings = Settings()
