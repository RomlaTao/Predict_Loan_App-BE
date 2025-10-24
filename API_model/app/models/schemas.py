from pydantic import BaseModel, Field, validator
from typing import List, Dict, Any, Optional
from datetime import datetime

class PredictionRequest(BaseModel):
    Age: int = Field(..., ge=18, le=100, description="Customer age")
    Experience: int = Field(..., ge=0, le=50, description="Years of experience")
    Income: float = Field(..., gt=0, description="Annual income")
    Family: int = Field(..., ge=1, le=10, description="Family size")
    Education: int = Field(..., ge=1, le=3, description="Education level")
    Mortgage: float = Field(..., ge=0, description="Mortgage amount")
    Securities_Account: int = Field(..., ge=0, le=1, description="Securities account")
    CD_Account: int = Field(..., ge=0, le=1, description="CD account")
    Online: int = Field(..., ge=0, le=1, description="Online banking")
    CreditCard: int = Field(..., ge=0, le=1, description="Credit card")
    ann_CCAvg: float = Field(..., ge=0, description="Average credit card spending")
    
    @validator('Education')
    def validate_education(cls, v):
        if v not in [1, 2, 3]:
            raise ValueError('Education must be 1, 2, or 3')
        return v

class PredictionResponse(BaseModel):
    prediction: bool
    confidence: float
    probabilities: Dict[str, float]
    message: str
    timestamp: datetime

class RabbitMQMessage(BaseModel):
    correlation_id: str
    request_id: str
    request: PredictionRequest
    response: Optional[PredictionResponse] = None
    timestamp: datetime
    status: str  # PENDING, PROCESSING, COMPLETED, FAILED
