from typing import Dict, Any, Tuple
from loguru import logger
from app.models.ml_model import ml_model
from app.models.schemas import PredictionRequest, PredictionResponse
from datetime import datetime

class PredictionService:
    def __init__(self):
        self.ml_model = ml_model
    
    def predict(self, request: PredictionRequest) -> PredictionResponse:
        """Make prediction using ML model"""
        try:
            logger.info(f"Processing prediction request")
            
            # Convert request to dict
            request_data = request.dict()
            
            # Make prediction
            prediction, confidence, probabilities = self.ml_model.predict(request_data)
            
            # Create response
            response = PredictionResponse(
                prediction=prediction,
                confidence=confidence,
                probabilities=probabilities,
                message="Prediction completed successfully",
                timestamp=datetime.now()
            )
            
            logger.info(f"Prediction completed: {prediction} with confidence: {confidence}")
            return response
            
        except Exception as e:
            logger.error(f"Error in prediction service: {e}")
            raise
    
    def predict_from_dict(self, data: Dict[str, Any]) -> Tuple[bool, float, Dict[str, float]]:
        """Make prediction from dictionary data"""
        try:
            return self.ml_model.predict(data)
        except Exception as e:
            logger.error(f"Error in prediction from dict: {e}")
            raise
