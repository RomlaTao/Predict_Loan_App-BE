import json
import uuid
from datetime import datetime
from loguru import logger
from app.services.rabbitmq_service import RabbitMQService
from app.services.prediction_service import PredictionService
from app.models.schemas import PredictionRequest, PredictionResponse, RabbitMQMessage

class PredictionConsumer:
    def __init__(self):
        self.rabbitmq_service = RabbitMQService()
        self.prediction_service = PredictionService()
        self.setup_consumer()
    
    def setup_consumer(self):
        """Setup message consumer"""
        self.rabbitmq_service.channel.basic_qos(prefetch_count=1)
        self.rabbitmq_service.channel.basic_consume(
            queue='ml.prediction.request',
            on_message_callback=self.process_prediction_request
        )
    
    def process_prediction_request(self, ch, method, properties, body):
        """Process incoming prediction request"""
        try:
            # Parse message
            message_data = json.loads(body)
            message = RabbitMQMessage(**message_data)
            
            logger.info(f"Processing prediction request: {message.correlation_id}")
            
            # Update status to PROCESSING
            message.status = "PROCESSING"
            
            # Make prediction using prediction service
            response = self.prediction_service.predict(message.request)
            
            # Update message with response
            message.response = response
            message.status = "COMPLETED"
            message.timestamp = datetime.now()
            
            # Publish response
            self.rabbitmq_service.publish_response(message.dict())
            
            # Acknowledge message
            ch.basic_ack(delivery_tag=method.delivery_tag)
            
            logger.info(f"Completed prediction for correlation_id: {message.correlation_id}")
            
        except Exception as e:
            logger.error(f"Error processing prediction request: {e}")
            
            # Send error response
            try:
                error_message = RabbitMQMessage(
                    correlation_id=message.correlation_id,
                    request_id=message.request_id,
                    request=message.request,
                    response=PredictionResponse(
                        prediction=False,
                        confidence=0.0,
                        probabilities={"Không chấp nhận": 1.0, "Chấp nhận": 0.0},
                        message=f"Error: {str(e)}",
                        timestamp=datetime.now()
                    ),
                    timestamp=datetime.now(),
                    status="FAILED"
                )
                
                self.rabbitmq_service.publish_response(error_message.dict())
                
            except Exception as send_error:
                logger.error(f"Error sending error response: {send_error}")
            
            # Reject message
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
    
    def start_consuming(self):
        """Start consuming messages"""
        logger.info("Starting prediction consumer...")
        try:
            self.rabbitmq_service.channel.start_consuming()
        except KeyboardInterrupt:
            logger.info("Stopping consumer...")
            self.rabbitmq_service.channel.stop_consuming()
            self.rabbitmq_service.close()
