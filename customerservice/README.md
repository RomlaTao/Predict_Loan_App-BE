# Customer Service

## Mô tả
Customer Service quản lý thông tin khách hàng vay tiền với các field phù hợp cho ML model dự đoán khoản vay cá nhân.

## Cấu hình

### Database
```sql
CREATE DATABASE customerservice;
CREATE USER 'customerservice'@'%' IDENTIFIED BY 'customerservice';
GRANT ALL PRIVILEGES ON customerservice.* TO 'customerservice'@'%';
FLUSH PRIVILEGES;
```

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3308/customerservice?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=customerservice
SPRING_DATASOURCE_PASSWORD=customerservice
SPRING_JPA_HIBERNATE_DDL_AUTO=update
EUREKA_SERVER_URL=http://localhost:8761/eureka/
```

## API Endpoints

### 1. Tạo Customer Profile
```bash
curl -X POST http://localhost:8007/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "age": 25,
    "experience": 2,
    "income": 50.0,
    "family": 3,
    "ccAvg": 2.5,
    "education": 1,
    "mortgage": 0.0,
    "securitiesAccount": false,
    "cdAccount": false,
    "online": true,
    "creditCard": true,
    "personalLoan": null
  }'
```

### 1.1. Tạo danh sách Customer Profiles (Bulk Create)
```bash
curl -X POST http://localhost:8007/api/customers/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {
      "fullName": "Nguyen Van A",
      "email": "nguyenvana@example.com",
      "age": 25,
      "experience": 2,
      "income": 50.0,
      "family": 3,
      "ccAvg": 2.5,
      "education": 1,
      "mortgage": 0.0,
      "securitiesAccount": false,
      "cdAccount": false,
      "online": true,
      "creditCard": true,
      "personalLoan": null
    },
    {
      "fullName": "Tran Thi B",
      "email": "tranthib@example.com",
      "age": 30,
      "experience": 5,
      "income": 75.0,
      "family": 2,
      "ccAvg": 4.2,
      "education": 2,
      "mortgage": 150.0,
      "securitiesAccount": true,
      "cdAccount": true,
      "online": true,
      "creditCard": true,
      "personalLoan": null
    }
  ]'
```

### 2. Lấy thông tin profile hiện tại
```bash
curl -X GET http://localhost:8007/api/customers/me \
  -H "X-User-Id: {customer-id}"
```

### 3. Lấy thông tin profile theo ID
```bash
curl -X GET http://localhost:8007/api/customers/{customer-id}
```

### 4. Lấy tất cả profiles
```bash
curl -X GET http://localhost:8007/api/customers
```

### 5. Cập nhật profile
```bash
curl -X PUT http://localhost:8007/api/customers/{customer-id} \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyen Van A Updated",
    "email": "nguyenvana@example.com",
    "age": 26,
    "experience": 3,
    "income": 55.0,
    "family": 3,
    "ccAvg": 3.0,
    "education": 1,
    "mortgage": 0.0,
    "securitiesAccount": false,
    "cdAccount": false,
    "online": true,
    "creditCard": true,
    "personalLoan": true
  }'
```

### 6. Xóa profile
```bash
curl -X DELETE http://localhost:8007/api/customers/{customer-id}
```

### 7. Lấy danh sách khách hàng được duyệt
```bash
curl -X GET http://localhost:8007/api/customers/approved
```

### 8. Lấy danh sách khách hàng bị từ chối
```bash
curl -X GET http://localhost:8007/api/customers/rejected
```

### 9. Lấy danh sách khách hàng chờ duyệt
```bash
curl -X GET http://localhost:8007/api/customers/pending
```

### 10. Duyệt khoản vay cho khách hàng
```bash
curl -X POST http://localhost:8007/api/customers/{customer-id}/approve
```

### 11. Từ chối khoản vay cho khách hàng
```bash
curl -X POST http://localhost:8007/api/customers/{customer-id}/reject
```

## Test Cases

### Test Case 1: Tạo Customer Profile thành công
```bash
curl -X POST http://localhost:8007/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Tran Thi B",
    "email": "tranthib@example.com",
    "age": 30,
    "experience": 5,
    "income": 75.0,
    "family": 2,
    "ccAvg": 4.2,
    "education": 2,
    "mortgage": 150.0,
    "securitiesAccount": true,
    "cdAccount": true,
    "online": true,
    "creditCard": true,
    "personalLoan": null
  }'
```

**Expected Response:**
```json
{
  "customerId": "uuid-generated",
  "fullName": "Tran Thi B",
  "email": "tranthib@example.com",
  "age": 30,
  "experience": 5,
  "income": 75.0,
  "family": 2,
  "ccAvg": 4.2,
  "education": 2,
  "mortgage": 150.0,
  "securitiesAccount": true,
  "cdAccount": true,
  "online": true,
  "creditCard": true,
  "personalLoan": null,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Mẫu payload tham khảo (dựa theo field)

1) Mẫu trung bình
```json
{
  "fullName": "Nguyen Van A",
  "email": "nguyenvana@example.com",
  "age": 45,
  "experience": 20,
  "income": 74.0,
  "family": 2,
  "ccAvg": 23.0,
  "education": 2,
  "mortgage": 56.0,
  "securitiesAccount": false,
  "cdAccount": false,
  "online": true,
  "creditCard": false,
  "personalLoan": null
}
```

2) Giá trị biên (tuổi nhỏ nhất, thu nhập thấp)
```json
{
  "fullName": "Pham Thi D",
  "email": "phamthid@example.com",
  "age": 23,
  "experience": 0,
  "income": 8.0,
  "family": 1,
  "ccAvg": 0.0,
  "education": 1,
  "mortgage": 0.0,
  "securitiesAccount": false,
  "cdAccount": false,
  "online": false,
  "creditCard": false,
  "personalLoan": null
}
```

3) Thu nhập cao + có tài khoản (securities, CD)
```json
{
  "fullName": "Le Van C",
  "email": "levanc@example.com",
  "age": 55,
  "experience": 30,
  "income": 98.0,
  "family": 3,
  "ccAvg": 30.0,
  "education": 3,
  "mortgage": 101.0,
  "securitiesAccount": true,
  "cdAccount": true,
  "online": true,
  "creditCard": true,
  "personalLoan": true
}
```

4) Thiếu trường không bắt buộc (bỏ personalLoan)
```json
{
  "fullName": "Tran Thi B",
  "email": "tranthib@example.com",
  "age": 30,
  "experience": 5,
  "income": 75.0,
  "family": 2,
  "ccAvg": 4.2,
  "education": 2,
  "mortgage": 150.0,
  "securitiesAccount": true,
  "cdAccount": true,
  "online": true,
  "creditCard": true
}
```

### Test Case 2: Lấy profile không tồn tại
```bash
curl -X GET http://localhost:8007/api/customers/00000000-0000-0000-0000-000000000000
```

**Expected Response:**
```json
{
  "error": "Customer profile not found"
}
```

### Test Case 3: Cập nhật profile thành công
```bash
curl -X PUT http://localhost:8007/api/customers/{existing-customer-id} \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Tran Thi B Updated",
    "email": "tranthib@example.com",
    "age": 31,
    "experience": 6,
    "income": 80.0,
    "family": 2,
    "ccAvg": 4.5,
    "education": 2,
    "mortgage": 160.0,
    "securitiesAccount": true,
    "cdAccount": true,
    "online": true,
    "creditCard": true,
    "personalLoan": true
  }'
```

### Test Case 4: Xóa profile thành công
```bash
curl -X DELETE http://localhost:8007/api/customers/{existing-customer-id}
```

**Expected Response:**
```
HTTP 200 OK
```

### Test Case 5: Lấy danh sách khách hàng được duyệt
```bash
curl -X GET http://localhost:8007/api/customers/approved
```

**Expected Response:**
```json
[
  {
    "customerId": "uuid-1",
    "fullName": "Nguyen Van A",
    "personalLoan": true,
    ...
  }
]
```

### Test Case 6: Duyệt khoản vay cho khách hàng
```bash
curl -X POST http://localhost:8007/api/customers/{customer-id}/approve
```

**Expected Response:**
```json
{
  "customerId": "uuid",
  "fullName": "Customer Name",
  "personalLoan": true,
  ...
}
```

### Test Case 7: Từ chối khoản vay cho khách hàng
```bash
curl -X POST http://localhost:8007/api/customers/{customer-id}/reject
```

**Expected Response:**
```json
{
  "customerId": "uuid",
  "fullName": "Customer Name",
  "personalLoan": false,
  ...
}
```

### Test Case 8: Tạo danh sách khách hàng hàng loạt
```bash
curl -X POST http://localhost:8007/api/customers/bulk \
  -H "Content-Type: application/json" \
  -d '[
    {
      "fullName": "Le Van C",
      "email": "levanc@example.com",
      "age": 28,
      "experience": 3,
      "income": 60.0,
      "family": 4,
      "ccAvg": 3.0,
      "education": 1,
      "mortgage": 0.0,
      "securitiesAccount": false,
      "cdAccount": false,
      "online": true,
      "creditCard": true,
      "personalLoan": null
    },
    {
      "fullName": "Pham Thi D",
      "email": "phamthid@example.com",
      "age": 35,
      "experience": 8,
      "income": 90.0,
      "family": 2,
      "ccAvg": 5.5,
      "education": 2,
      "mortgage": 200.0,
      "securitiesAccount": true,
      "cdAccount": true,
      "online": true,
      "creditCard": true,
      "personalLoan": null
    }
  ]'
```

**Expected Response:**
```json
[
  {
    "customerId": "uuid-1",
    "fullName": "Le Van C",
    "email": "levanc@example.com",
    "age": 28,
    "experience": 3,
    "income": 60.0,
    "family": 4,
    "ccAvg": 3.0,
    "education": 1,
    "mortgage": 0.0,
    "securitiesAccount": false,
    "cdAccount": false,
    "online": true,
    "creditCard": true,
    "personalLoan": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  {
    "customerId": "uuid-2",
    "fullName": "Pham Thi D",
    "email": "phamthid@example.com",
    "age": 35,
    "experience": 8,
    "income": 90.0,
    "family": 2,
    "ccAvg": 5.5,
    "education": 2,
    "mortgage": 200.0,
    "securitiesAccount": true,
    "cdAccount": true,
    "online": true,
    "creditCard": true,
    "personalLoan": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

## Chạy ứng dụng

```bash
cd customerservice
mvn spring-boot:run
```

Ứng dụng sẽ chạy trên port 8007 và đăng ký với Eureka Server.
