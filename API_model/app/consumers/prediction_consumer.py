import json
import uuid
from datetime import datetime
from loguru import logger
from app.services.rabbitmq_service import RabbitMQService
from app.services.prediction_service import PredictionService
from app.models.schemas import (
    PredictionRequest,
    PredictionResponse,
    RabbitMQMessage,
    ModelPredictRequestedEvent,
    ModelPredictCompletedEvent,
    PredictionResultDto,
)
from app.config.settings import settings

class PredictionConsumer:
    def __init__(self):
        self.rabbitmq_service = RabbitMQService()
        self.prediction_service = PredictionService()
        self.setup_consumer()
    
    def setup_consumer(self):
        """Setup message consumer for model-predict-requested"""
        self.rabbitmq_service.channel.basic_qos(prefetch_count=1)
        self.rabbitmq_service.channel.basic_consume(
            queue=settings.model_predict_requested_queue,
            on_message_callback=self.process_model_predict_requested
        )
    
    def process_model_predict_requested(self, ch, method, properties, body):
        """Process ModelPredictRequestedEvent from Java services"""
        try:
            data = json.loads(body)
            event = ModelPredictRequestedEvent(**data)

            logger.info(f"Received model-predict-requested for predictionId={event.predictionId}")

            # Transform input to model expected keys
            features = {
                'Age': event.input.age,
                'Experience': event.input.experience,
                'Income': event.input.income,
                'Family': event.input.family,
                'Education': event.input.education,
                'Mortgage': event.input.mortgage,
                'Securities_Account': 1 if event.input.securitiesAccount else 0,
                'CD_Account': 1 if event.input.cdAccount else 0,
                'Online': 1 if event.input.online else 0,
                'CreditCard': 1 if event.input.creditCard else 0,
                'ann_CCAvg': event.input.ccAvg,
            }

            start_ts = datetime.now()
            prediction, confidence, probabilities = self.prediction_service.predict_from_dict(features)
            inference_ms = int((datetime.now() - start_ts).total_seconds() * 1000)

            # Derive label string
            label = 'approve' if prediction else 'reject'

            completed = ModelPredictCompletedEvent(
                predictionId=event.predictionId,
                customerId=event.customerId,
                result=PredictionResultDto(
                    label=label,
                    probability=confidence,
                    modelVersion='v1',
                    inferenceTimeMs=inference_ms,
                ),
                predictedAt=datetime.now(),
            )

            self.rabbitmq_service.publish_model_predict_completed(completed.dict())

            ch.basic_ack(delivery_tag=method.delivery_tag)
            logger.info("Processed model-predict-requested and published completed event")
            
        except Exception as e:
            logger.error(f"Error processing prediction request: {e}")
            
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
