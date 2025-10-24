# Models Directory

This directory contains the machine learning model files used by the prediction service.

## Files

- `knn_model.joblib` - Trained KNN model for loan prediction
- `scaler.joblib` - Data scaler used for feature normalization

## Usage

These model files are automatically loaded by the ML Model Manager when the application starts. The paths are configured in `app/config/settings.py`:

```python
model_path: str = "models/knn_model.joblib"
scaler_path: str = "models/scaler.joblib"
```

## Model Information

- **Algorithm**: K-Nearest Neighbors (KNN)
- **Features**: 11 input features (Age, Experience, Income, Family, Education, Mortgage, Securities_Account, CD_Account, Online, CreditCard, ann_CCAvg)
- **Output**: Binary classification (0: Reject, 1: Accept)
- **Scaler**: StandardScaler for feature normalization

## Updating Models

To update the models:

1. Replace the `.joblib` files in this directory
2. Restart the application
3. The new models will be automatically loaded

## Security

- Model files should be version controlled
- Consider using model versioning for production deployments
- Ensure model files are not corrupted during deployment
