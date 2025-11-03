import joblib
import pandas as pd
import numpy as np
from typing import Dict, Any, Tuple
from loguru import logger
from app.config.settings import settings

class MLModelManager:
    def __init__(self):
        self.model = None
        self.scaler = None
        self.numeric_features = [
            'Age', 'Experience', 'Income', 'Mortgage', 'ann_CCAvg'
        ]
        self.load_model()
    
    def load_model(self):
        """Load the trained model and scaler"""
        try:
            self.model = joblib.load(settings.model_path)
            self.scaler = joblib.load(settings.scaler_path)
            logger.info("Model and scaler loaded successfully")
        except Exception as e:
            logger.error(f"Error loading model: {e}")
            raise
    
    def preprocess_data(self, data: Dict[str, Any]) -> pd.DataFrame:
        """Preprocess input data"""
        try:
            # Create DataFrame with expected columns
            expected_columns = [
                'Age', 'Experience', 'Income', 'Family', 'Education', 'Mortgage',
                'Securities Account', 'CD Account', 'Online', 'CreditCard', 'ann_CCAvg'
            ]
            
            # Map input data to DataFrame columns
            input_dict = {}
            for col in expected_columns:
                if col in data:
                    input_dict[col] = [data[col]]
                else:
                    # Handle missing columns
                    input_dict[col] = [0]
            
            df = pd.DataFrame(input_dict)
            
            # Apply scaling to numeric features
            df_scaled = df.copy()
            df_scaled[self.numeric_features] = self.scaler.transform(
                df[self.numeric_features]
            )
            
            return df_scaled
            
        except Exception as e:
            logger.error(f"Error preprocessing data: {e}")
            raise
    
    def predict(self, data: Dict[str, Any]) -> Tuple[bool, float, Dict[str, float]]:
        """Make prediction"""
        try:
            if self.model is None or self.scaler is None:
                raise ValueError("Model or scaler not loaded")
            
            # Preprocess data
            processed_data = self.preprocess_data(data)
            
            # Make prediction
            prediction = self.model.predict(processed_data)[0]
            probabilities = self.model.predict_proba(processed_data)[0]
            
            # Convert to boolean
            prediction_bool = bool(prediction)
            confidence = float(max(probabilities))
            
            # Create probabilities dict
            prob_dict = {
                "Không chấp nhận": float(probabilities[0]),
                "Chấp nhận": float(probabilities[1])
            }
            
            return prediction_bool, confidence, prob_dict
            
        except Exception as e:
            logger.error(f"Error making prediction: {e}")
            raise

# Global model instance
ml_model = MLModelManager()
