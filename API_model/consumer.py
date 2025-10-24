from app.consumers.prediction_consumer import PredictionConsumer
from app.utils.logger import setup_logger
from loguru import logger

if __name__ == "__main__":
    # Setup logging
    setup_logger()
    
    try:
        logger.info("Starting ML Model Consumer...")
        consumer = PredictionConsumer()
        consumer.start_consuming()
    except Exception as e:
        logger.error(f"Consumer error: {e}")
