class MLModelException(Exception):
    """Base exception for ML Model errors"""
    pass

class ModelLoadException(MLModelException):
    """Exception raised when model fails to load"""
    pass

class PredictionException(MLModelException):
    """Exception raised when prediction fails"""
    pass

class DataValidationException(MLModelException):
    """Exception raised when data validation fails"""
    pass

class RabbitMQException(MLModelException):
    """Exception raised when RabbitMQ operations fail"""
    pass
