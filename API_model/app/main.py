from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from loguru import logger
from app.models.schemas import PredictionRequest, PredictionResponse
from app.services.prediction_service import PredictionService
from app.config.settings import settings
from app.utils.logger import setup_logger
from datetime import datetime

# Setup logging
setup_logger()

# Create FastAPI app
app = FastAPI(
    title=settings.app_name,
    description="ML Model API for Loan Prediction",
    version="1.0.0"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize prediction service
prediction_service = PredictionService()

@app.get("/")
async def root():
    """Root endpoint"""
    return {"message": "ML Model API is running", "version": "1.0.0"}

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        "status": "healthy", 
        "service": "ML Model API",
        "timestamp": datetime.now().isoformat()
    }

@app.post("/predict", response_model=PredictionResponse)
async def predict(request: PredictionRequest):
    """Direct prediction endpoint (for testing)"""
    try:
        logger.info(f"Received prediction request: {request.dict()}")
        
        response = prediction_service.predict(request)
        
        logger.info(f"Prediction completed: {response.prediction}")
        return response
        
    except Exception as e:
        logger.error(f"Prediction error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/model/info")
async def model_info():
    """Get model information"""
    return {
        "model_path": settings.model_path,
        "scaler_path": settings.scaler_path,
        "status": "loaded"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.debug
    )
