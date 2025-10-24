import requests
import json
import time
from datetime import datetime

# Base URL for the new API
BASE_URL = "http://localhost:8009"

def test_health_endpoint():
    """Test health check endpoint"""
    print("=== TESTING HEALTH ENDPOINT ===\n")
    
    try:
        response = requests.get(f"{BASE_URL}/health")
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Health Check: {result}")
        else:
            print(f"❌ Health Check Failed: {response.status_code}")
    except Exception as e:
        print(f"❌ Health Check Error: {e}")
    
    print("-" * 50)

def test_model_info():
    """Test model info endpoint"""
    print("=== TESTING MODEL INFO ===\n")
    
    try:
        response = requests.get(f"{BASE_URL}/model/info")
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Model Info: {result}")
        else:
            print(f"❌ Model Info Failed: {response.status_code}")
    except Exception as e:
        print(f"❌ Model Info Error: {e}")
    
    print("-" * 50)

def test_prediction_api():
    """Test the new prediction API with various test cases"""
    
    # Test case 1: Khách hàng trung bình (dựa trên mean values)
    test_case_1 = {
        "Age": 45,  # mean: 45.3
        "Experience": 20,  # mean: 20.3
        "Income": 74,  # mean: 73.8
        "Family": 2,  # mean: 2.4
        "Education": 2,  # mean: 1.9
        "Mortgage": 56,  # mean: 56.5
        "Securities_Account": 0,  # mean: 0.1
        "CD_Account": 0,  # mean: 0.06
        "Online": 1,  # mean: 0.6
        "CreditCard": 0,  # mean: 0.3
        "ann_CCAvg": 23.0  # mean: 23.3
    }
    
    # Test case 2: Khách hàng trẻ có thu nhập thấp
    test_case_2 = {
        "Age": 35,
        "Experience": 11,
        "Income": 39,
        "Family": 1,
        "Education": 1,
        "Mortgage": 0,
        "Securities_Account": 0,
        "CD_Account": 0,
        "Online": 0,
        "CreditCard": 0,
        "ann_CCAvg": 8.4
    }
    
    # Test case 3: Khách hàng có thu nhập cao
    test_case_3 = {
        "Age": 55,
        "Experience": 30,
        "Income": 98,
        "Family": 3,
        "Education": 3,
        "Mortgage": 101,
        "Securities_Account": 0,
        "CD_Account": 0,
        "Online": 1,
        "CreditCard": 1,
        "ann_CCAvg": 30.0
    }

    test_cases = [
        ("Khách hàng trung bình", test_case_1),
        ("Khách hàng trẻ có thu nhập thấp", test_case_2),
        ("Khách hàng có thu nhập cao", test_case_3)
    ]
    
    print("=== TESTING PREDICTION API ===\n")
    
    for i, (description, test_data) in enumerate(test_cases, 1):
        print(f"Test Case {i}: {description}")
        print(f"Input data: {json.dumps(test_data, indent=2)}")
        
        try:
            # Send POST request to the API
            response = requests.post(f"{BASE_URL}/predict", 
                                   json=test_data,
                                   headers={'Content-Type': 'application/json'})
            
            if response.status_code == 200:
                result = response.json()
                print(f"✅ Prediction: {result['prediction']}")
                print(f"   Confidence: {result['confidence']}")
                print(f"   Probabilities: {result['probabilities']}")
                print(f"   Message: {result['message']}")
                print(f"   Timestamp: {result['timestamp']}")
            else:
                print(f"❌ Error {response.status_code}: {response.text}")
                
        except requests.exceptions.ConnectionError:
            print("❌ Connection Error: Could not connect to the API. Make sure the server is running.")
        except Exception as e:
            print(f"❌ Unexpected error: {e}")
        
        print("-" * 50)

def test_validation_errors():
    """Test API with invalid data to check validation"""
    print("=== TESTING VALIDATION ERRORS ===\n")
    
    # Test with missing required fields
    invalid_data_1 = {
        "Age": 30,
        "Income": 50000
        # Missing other required fields
    }
    
    # Test with invalid data types
    invalid_data_2 = {
        "Age": "thirty",  # Should be integer
        "Experience": 10,
        "Income": 50000,
        "Family": 2,
        "Education": 2,
        "Mortgage": 0,
        "Securities_Account": 0,
        "CD_Account": 0,
        "Online": 1,
        "CreditCard": 0,
        "ann_CCAvg": 20.0
    }
    
    # Test with invalid education value
    invalid_data_3 = {
        "Age": 30,
        "Experience": 10,
        "Income": 50000,
        "Family": 2,
        "Education": 5,  # Invalid education value (should be 1, 2, or 3)
        "Mortgage": 0,
        "Securities_Account": 0,
        "CD_Account": 0,
        "Online": 1,
        "CreditCard": 0,
        "ann_CCAvg": 20.0
    }
    
    invalid_cases = [
        ("Missing required fields", invalid_data_1),
        ("Invalid data types", invalid_data_2),
        ("Invalid education value", invalid_data_3)
    ]
    
    for i, (description, test_data) in enumerate(invalid_cases, 1):
        print(f"Invalid Test Case {i}: {description}")
        print(f"Input data: {json.dumps(test_data, indent=2)}")
        
        try:
            response = requests.post(f"{BASE_URL}/predict", 
                                   json=test_data,
                                   headers={'Content-Type': 'application/json'})
            
            print(f"Status Code: {response.status_code}")
            print(f"Response: {response.text}")
            
        except Exception as e:
            print(f"❌ Error: {e}")
        
        print("-" * 30)

def main():
    """Run all tests"""
    print("🚀 STARTING ML MODEL API TESTS")
    print(f"Testing API at: {BASE_URL}")
    print(f"Test started at: {datetime.now()}")
    print("=" * 60)
    
    # Wait a moment for the server to start
    print("Waiting for server to start...")
    time.sleep(3)
    
    # Run all tests
    test_health_endpoint()
    test_model_info()
    test_prediction_api()
    test_validation_errors()
    
    print("=" * 60)
    print("✅ ALL TESTS COMPLETED")

if __name__ == "__main__":
    main()
