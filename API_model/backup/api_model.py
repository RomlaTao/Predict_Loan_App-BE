# app.py
from flask import Flask, request, jsonify
import joblib
import pandas as pd

app = Flask(__name__)

# Placeholder for model and scaler loading
# In a real application, these would be loaded once when the app starts
knn_model = None
scaler = None
# Placeholder for expected numerical feature names from the training data
# This needs to match the order and names used during training
numeric_features = ['Age', 'Experience', 'Income', 'Mortgage', 'ann_CCAvg']


def load_model_and_scaler():
    """Loads the trained model and scaler."""
    global knn_model, scaler
    try:
        # Adjust the paths if necessary based on where the files are deployed
        model_path = 'knn_model.joblib'
        scaler_path = 'scaler.joblib'
        knn_model = joblib.load(model_path)
        scaler = joblib.load(scaler_path)
        print("Model and scaler loaded successfully.")
    except Exception as e:
        print(f"Error loading model or scaler: {e}")
        # Handle the error appropriately in a real application
        # For this example, we'll just print it.


@app.route('/predict', methods=['POST'])
def predict():
    """
    Receives customer data via POST request, makes a prediction, and returns the result.
    """
    if not request.is_json:
        return jsonify({"error": "Request must be JSON"}), 415

    data = request.get_json()

    # Placeholder for data validation and preprocessing
    # Expected data structure: {'feature_name': value, ...}
    try:
        # Convert input data to a DataFrame, ensuring the correct column order
        # We need to include all features expected by the model, even if some are not scaled
        # The order must match the training data used for fitting the model
        # We will assume the input JSON provides values for all columns in the order
        # of X_train.columns.tolist() which we don't have direct access to here,
        # so we'll use a placeholder structure based on the previous steps.
        # In a real scenario, you'd define a strict input schema.

        # This is a simplified placeholder. In a real app, you'd need to
        # ensure all required features are present and in the correct order.
        # For demonstration, we'll create a DataFrame based on the expected keys.

        # Define the expected order of columns based on X_train during training
        # (This list needs to be accurate based on the previous notebook steps)
        expected_columns = [
            'Age', 'Experience', 'Income', 'Family', 'Education', 'Mortgage',
            'Securities Account', 'CD Account', 'Online', 'CreditCard', 'ann_CCAvg'
        ]

        # Create a dictionary with lists for each feature, ensuring all expected columns are present
        # and handling potential missing keys from the input data by using default values or raising error
        # For this placeholder, we'll assume the input JSON has all keys.
        input_dict = {col: [data.get(col, 0)] for col in expected_columns} # Using 0 as a placeholder default

        new_customer_df = pd.DataFrame(input_dict)


        # Apply the same scaling as used during training
        if scaler is None:
             # Attempt to load if not already loaded (e.g., first request)
             load_model_and_scaler()
             if scaler is None:
                 return jsonify({"error": "Model or scaler not loaded"}), 500


        new_customer_scaled = new_customer_df.copy()
        new_customer_scaled[numeric_features] = scaler.transform(new_customer_df[numeric_features])

        # Placeholder for prediction
        if knn_model is None:
            # Attempt to load if not already loaded
             load_model_and_scaler()
             if knn_model is None:
                 return jsonify({"error": "Model or scaler not loaded"}), 500


        predicted_loan = knn_model.predict(new_customer_scaled)
        predicted_proba = knn_model.predict_proba(new_customer_scaled)

        # Interpret the output
        result = "Chấp nhận khoản vay cá nhân" if predicted_loan[0] == 1 else "Không chấp nhận khoản vay cá nhân"
        probability = predicted_proba[0].tolist() # Convert numpy array to list

        return jsonify({
            "prediction": result,
            "probabilities": {"Không chấp nhận": probability[0], "Chấp nhận": probability[1]}
        })

    except KeyError as e:
        return jsonify({"error": f"Missing data for required feature: {e}"}), 400
    except Exception as e:
        # Log the error in a real application
        return jsonify({"error": f"Prediction failed: {e}"}), 500

# Entry point to run the Flask app
if __name__ == '__main__':
    # Load model and scaler when the app starts
    load_model_and_scaler()
    # Run the Flask app
    # debug=True is useful for development, set to False in production
    app.run(debug=True, port=8009) # You can change the port as needed
