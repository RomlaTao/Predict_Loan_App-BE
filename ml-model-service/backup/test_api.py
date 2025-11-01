import requests
import json

# Base URL for the API
BASE_URL = "http://localhost:5000"

def test_api():
    """Test the loan prediction API with various test cases"""
    
    # Test case 1: Khách hàng trung bình (dựa trên mean values)
    test_case_1 = {
        "Age": 45,  # mean: 45.3
        "Experience": 20,  # mean: 20.3
        "Income": 74,  # mean: 73.8
        "Family": 2,  # mean: 2.4
        "Education": 2,  # mean: 1.9
        "Mortgage": 56,  # mean: 56.5
        "Securities Account": 0,  # mean: 0.1
        "CD Account": 0,  # mean: 0.06
        "Online": 1,  # mean: 0.6
        "CreditCard": 0,  # mean: 0.3
        "ann_CCAvg": 23.0  # mean: 23.3
    }
    
    # Test case 2: Khách hàng trẻ có thu nhập thấp (dựa trên 25th percentile)
    test_case_2 = {
        "Age": 35,  # 25th percentile
        "Experience": 11,  # 25th percentile
        "Income": 39,  # 25th percentile
        "Family": 1,  # 25th percentile
        "Education": 1,  # 25th percentile
        "Mortgage": 0,  # 25th percentile
        "Securities Account": 0,  # 25th percentile
        "CD Account": 0,  # 25th percentile
        "Online": 0,  # 25th percentile
        "CreditCard": 0,  # 25th percentile
        "ann_CCAvg": 8.4  # 25th percentile
    }
    
    # Test case 3: Khách hàng có thu nhập cao (dựa trên 75th percentile)
    test_case_3 = {
        "Age": 55,  # 75th percentile
        "Experience": 30,  # 75th percentile
        "Income": 98,  # 75th percentile
        "Family": 3,  # 75th percentile
        "Education": 3,  # 75th percentile
        "Mortgage": 101,  # 75th percentile
        "Securities Account": 0,  # 75th percentile
        "CD Account": 0,  # 75th percentile
        "Online": 1,  # 75th percentile
        "CreditCard": 1,  # 75th percentile
        "ann_CCAvg": 30.0  # 75th percentile
    }
    
    # Test case 4: Khách hàng có thu nhập rất cao (dựa trên max values)
    test_case_4 = {
        "Age": 67,  # max
        "Experience": 43,  # max
        "Income": 224,  # max
        "Family": 4,  # max
        "Education": 3,  # max
        "Mortgage": 635,  # max
        "Securities Account": 1,  # max
        "CD Account": 1,  # max
        "Online": 1,  # max
        "CreditCard": 1,  # max
        "ann_CCAvg": 120.0  # max
    }
    
    # Test case 5: Khách hàng trẻ có thu nhập thấp (dựa trên min values)
    test_case_5 = {
        "Age": 23,  # min
        "Experience": 0,  # min
        "Income": 8,  # min
        "Family": 1,  # min
        "Education": 1,  # min
        "Mortgage": 0,  # min
        "Securities Account": 0,  # min
        "CD Account": 0,  # min
        "Online": 0,  # min
        "CreditCard": 0,  # min
        "ann_CCAvg": 0.0  # min
    }
    
    # Test case 6: Khách hàng trung bình có tài khoản đầu tư (dựa trên median + có tài khoản)
    test_case_6 = {
        "Age": 45,  # median
        "Experience": 20,  # median
        "Income": 64,  # median
        "Family": 2,  # median
        "Education": 2,  # median
        "Mortgage": 0,  # median
        "Securities Account": 1,  # có tài khoản chứng khoán
        "CD Account": 1,  # có tài khoản CD
        "Online": 1,  # median
        "CreditCard": 0,  # median
        "ann_CCAvg": 18.0  # median
    }
    
    # Test case 7: Khách hàng có thu nhập cao và chi tiêu thẻ tín dụng cao (dựa trên 75th percentile + tài khoản)
    test_case_7 = {
        "Age": 55,  # 75th percentile
        "Experience": 30,  # 75th percentile
        "Income": 98,  # 75th percentile
        "Family": 3,  # 75th percentile
        "Education": 3,  # 75th percentile
        "Mortgage": 101,  # 75th percentile
        "Securities Account": 1,  # có tài khoản chứng khoán
        "CD Account": 1,  # có tài khoản CD
        "Online": 1,  # 75th percentile
        "CreditCard": 1,  # 75th percentile
        "ann_CCAvg": 30.0  # 75th percentile
    }
    
    # Test case 8: Khách hàng có thu nhập trung bình cao (dựa trên mean + std)
    test_case_8 = {
        "Age": 57,  # mean + 1 std: 45.3 + 11.5
        "Experience": 32,  # mean + 1 std: 20.3 + 11.3
        "Income": 120,  # mean + 1 std: 73.8 + 46.0
        "Family": 4,  # max
        "Education": 3,  # max
        "Mortgage": 158,  # mean + 1 std: 56.5 + 101.7
        "Securities Account": 1,  # có tài khoản chứng khoán
        "CD Account": 1,  # có tài khoản CD
        "Online": 1,  # có online banking
        "CreditCard": 1,  # có thẻ tín dụng
        "ann_CCAvg": 44.0  # mean + 1 std: 23.3 + 20.9
    }

    test_cases = [
        # Test case 1: Khách hàng trung bình (dựa trên mean values)
        ("Khách hàng trung bình (mean values)", test_case_1),
        # Test case 2: Khách hàng trẻ có thu nhập thấp (dựa trên 25th percentile)
        ("Khách hàng trẻ có thu nhập thấp (25th percentile)", test_case_2),
        # Test case 3: Khách hàng có thu nhập cao (dựa trên 75th percentile)
        ("Khách hàng có thu nhập cao (75th percentile)", test_case_3),
        # Test case 4: Khách hàng có thu nhập rất cao (dựa trên max values)
        ("Khách hàng có thu nhập rất cao (max values)", test_case_4),
        # Test case 5: Khách hàng trẻ có thu nhập thấp (dựa trên min values)
        ("Khách hàng trẻ có thu nhập thấp (min values)", test_case_5),
        # Test case 6: Khách hàng trung bình có tài khoản đầu tư (dựa trên median + có tài khoản)
        ("Khách hàng trung bình có tài khoản đầu tư (median + tài khoản)", test_case_6),
        # Test case 7: Khách hàng có thu nhập cao và chi tiêu thẻ tín dụng cao (dựa trên 75th percentile + tài khoản)
        ("Khách hàng có thu nhập cao + tài khoản (75th percentile + tài khoản)", test_case_7),
        # Test case 8: Khách hàng có thu nhập trung bình cao (dựa trên mean + std)
        ("Khách hàng có thu nhập trung bình cao (mean + std)", test_case_8)
    ]
    
    print("=== TESTING LOAN PREDICTION API ===\n")
    
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
                print(f"   Probabilities: {result['probabilities']}")
            else:
                print(f"❌ Error {response.status_code}: {response.text}")
                
        except requests.exceptions.ConnectionError:
            print("❌ Connection Error: Could not connect to the API. Make sure the server is running.")
        except Exception as e:
            print(f"❌ Unexpected error: {e}")
        
        print("-" * 50)

def test_invalid_data():
    """Test API with invalid data"""
    print("\n=== TESTING INVALID DATA ===\n")
    
    # Test with missing required fields
    invalid_data = {
        "Age": 30,
        "Income": 50000
        # Missing other required fields
    }
    
    print("Test with missing required fields:")
    print(f"Input data: {json.dumps(invalid_data, indent=2)}")
    
    try:
        response = requests.post(f"{BASE_URL}/predict", 
                               json=invalid_data,
                               headers={'Content-Type': 'application/json'})
        
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    # Wait a moment for the server to start
    import time
    print("Waiting for server to start...")
    time.sleep(3)
    
    # Run the tests
    test_api()
    test_invalid_data()
