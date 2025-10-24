from typing import Dict, Any
from app.models.schemas import PredictionRequest

def validate_prediction_request(data: Dict[str, Any]) -> PredictionRequest:
    """Validate and convert dictionary to PredictionRequest"""
    try:
        return PredictionRequest(**data)
    except Exception as e:
        raise ValueError(f"Invalid prediction request data: {e}")

def validate_numeric_features(data: Dict[str, Any]) -> bool:
    """Validate that all required numeric features are present and valid"""
    required_features = [
        'Age', 'Experience', 'Income', 'Family', 'Education', 'Mortgage',
        'Securities_Account', 'CD_Account', 'Online', 'CreditCard', 'ann_CCAvg'
    ]
    
    for feature in required_features:
        if feature not in data:
            raise ValueError(f"Missing required feature: {feature}")
        
        if not isinstance(data[feature], (int, float)):
            raise ValueError(f"Feature {feature} must be numeric")
    
    return True
