import pika
import json
from typing import Dict, Any
from loguru import logger
from app.config.settings import settings

class RabbitMQService:
    def __init__(self):
        self.connection = None
        self.channel = None
        self.connect()
    
    def connect(self):
        """Establish connection to RabbitMQ"""
        try:
            credentials = pika.PlainCredentials(
                settings.rabbitmq_username, 
                settings.rabbitmq_password
            )
            
            parameters = pika.ConnectionParameters(
                host=settings.rabbitmq_host,
                port=settings.rabbitmq_port,
                virtual_host=settings.rabbitmq_virtual_host,
                credentials=credentials
            )
            
            self.connection = pika.BlockingConnection(parameters)
            self.channel = self.connection.channel()
            
            # Declare exchange and queues
            self.setup_queues()
            
            logger.info("Connected to RabbitMQ")
            
        except Exception as e:
            logger.error(f"Error connecting to RabbitMQ: {e}")
            raise
    
    def setup_queues(self):
        """Setup exchanges and queues"""
        # Declare exchange
        self.channel.exchange_declare(
            exchange=settings.prediction_exchange,
            exchange_type='topic',
            durable=True
        )
        
        # Declare request queue
        self.channel.queue_declare(
            queue=settings.prediction_request_queue,
            durable=True
        )
        
        # Declare response queue
        self.channel.queue_declare(
            queue=settings.prediction_response_queue,
            durable=True
        )
        
        # Bind queues to exchange
        self.channel.queue_bind(
            exchange=settings.prediction_exchange,
            queue=settings.prediction_request_queue,
            routing_key='prediction.request'
        )
        
        self.channel.queue_bind(
            exchange=settings.prediction_exchange,
            queue=settings.prediction_response_queue,
            routing_key='prediction.response'
        )
    
    def publish_response(self, message: Dict[str, Any]):
        """Publish response to response queue"""
        try:
            self.channel.basic_publish(
                exchange=settings.prediction_exchange,
                routing_key='prediction.response',
                body=json.dumps(message, default=str),
                properties=pika.BasicProperties(
                    delivery_mode=2,  # Make message persistent
                    correlation_id=message.get('correlation_id')
                )
            )
            logger.info(f"Published response for correlation_id: {message.get('correlation_id')}")
            
        except Exception as e:
            logger.error(f"Error publishing response: {e}")
            raise
    
    def close(self):
        """Close connection"""
        if self.connection and not self.connection.is_closed:
            self.connection.close()
