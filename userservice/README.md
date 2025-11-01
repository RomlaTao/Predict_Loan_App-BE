# UserService - User Profile Management Service

## Base URL

- **Local:** `http://localhost:8006`
- **Via API Gateway:** `http://localhost:8080/api/users/profiles`

---

## Postman Test Cases

### Environment Variables

Trong Postman, tạo các variables sau:
- `baseUrl`: `http://localhost:8006` (hoặc `http://localhost:8080/api/users/profiles` nếu qua Gateway)
- `accessToken`: Access token từ AuthService (để gọi qua Gateway)
- `userId`: UUID của user (sẽ được lưu sau khi tạo profile)
- `xUserId`: UUID của user cho header `X-User-Id`

---

### 1. Create Profile - Tạo hồ sơ người dùng

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/user-profiles`
- **Headers:**
  ```
  Content-Type: application/json
  ```
  *Nếu qua Gateway, thêm:*
  ```
  Authorization: Bearer {{accessToken}}
  ```
- **Body (raw JSON):**
  ```json
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "department": "IT",
    "position": "Software Engineer",
    "hireDate": "2023-01-15",
    "phoneNumber": "+84123456789",
    "address": "123 Main Street, Ho Chi Minh City",
    "isActive": true
  }
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON):**
  ```json
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "department": "IT",
    "position": "Software Engineer",
    "hireDate": "2023-01-15",
    "phoneNumber": "+84123456789",
    "address": "123 Main Street, Ho Chi Minh City",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
  ```

**Postman Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains user profile", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('userId');
    pm.expect(jsonData).to.have.property('fullName');
    
    // Lưu userId để dùng cho các request sau
    pm.collectionVariables.set("userId", jsonData.userId);
    pm.collectionVariables.set("xUserId", jsonData.userId);
});
```

---

### 2. Get Current Profile - Lấy hồ sơ hiện tại

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/user-profiles/me`
- **Headers:**
  ```
  X-User-Id: {{xUserId}}
  ```
  *Nếu qua Gateway, thêm:*
  ```
  Authorization: Bearer {{accessToken}}
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON):**
  ```json
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "department": "IT",
    "position": "Software Engineer",
    "hireDate": "2023-01-15",
    "phoneNumber": "+84123456789",
    "address": "123 Main Street, Ho Chi Minh City",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
  ```

**Postman Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains user profile", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('userId');
    pm.expect(jsonData.userId).to.equal(pm.collectionVariables.get("userId"));
});
```

---

### 3. Get Profile by User ID - Lấy hồ sơ theo User ID

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/user-profiles/{{userId}}`
- **Headers:**
  *Nếu qua Gateway:*
  ```
  Authorization: Bearer {{accessToken}}
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON):**
  ```json
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "department": "IT",
    "position": "Software Engineer",
    "hireDate": "2023-01-15",
    "phoneNumber": "+84123456789",
    "address": "123 Main Street, Ho Chi Minh City",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
  ```

**Postman Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response matches requested userId", function () {
    var jsonData = pm.response.json();
    var requestedUserId = pm.collectionVariables.get("userId");
    pm.expect(jsonData.userId).to.equal(requestedUserId);
});
```

---

### 4. Get All Profiles - Lấy tất cả hồ sơ

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/user-profiles`
- **Headers:**
  *Nếu qua Gateway:*
  ```
  Authorization: Bearer {{accessToken}}
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON Array):**
  ```json
  [
    {
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "fullName": "Nguyen Van A",
      "email": "nguyenvana@example.com",
      "department": "IT",
      "position": "Software Engineer",
      "hireDate": "2023-01-15",
      "phoneNumber": "+84123456789",
      "address": "123 Main Street, Ho Chi Minh City",
      "isActive": true,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    },
    {
      "userId": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
      "fullName": "Tran Thi B",
      "email": "tranthib@example.com",
      "department": "HR",
      "position": "HR Manager",
      "hireDate": "2022-06-01",
      "phoneNumber": "+84987654321",
      "address": "456 Second Street, Hanoi",
      "isActive": true,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
  ```

**Postman Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});
```

---

### 5. Update Profile - Cập nhật hồ sơ

**Request:**
- **Method:** `PUT`
- **URL:** `{{baseUrl}}/api/user-profiles/{{userId}}`
- **Headers:**
  ```
  Content-Type: application/json
  ```
  *Nếu qua Gateway, thêm:*
  ```
  Authorization: Bearer {{accessToken}}
  ```
- **Body (raw JSON):**
  ```json
  {
    "fullName": "Nguyen Van A Updated",
    "email": "nguyenvana.updated@example.com",
    "department": "Engineering",
    "position": "Senior Software Engineer",
    "hireDate": "2023-01-15",
    "phoneNumber": "+84123456789",
    "address": "789 Updated Street, Ho Chi Minh City",
    "isActive": true
  }
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON):**
  ```json
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "fullName": "Nguyen Van A Updated",
    "email": "nguyenvana.updated@example.com",
    "department": "Engineering",
    "position": "Senior Software Engineer",
    "hireDate": "2023-01-15",
    "phoneNumber": "+84123456789",
    "address": "789 Updated Street, Ho Chi Minh City",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T11:00:00"
  }
  ```

**Postman Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Profile updated successfully", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.fullName).to.equal("Nguyen Van A Updated");
    pm.expect(jsonData.updatedAt).to.not.equal(jsonData.createdAt);
});
```

---

## Negative Test Cases

### 6. Create Profile - Thiếu userId

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/user-profiles`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer {{accessToken}}
  ```
- **Body (raw JSON):**
  ```json
  {
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "department": "IT",
    "position": "Software Engineer"
  }
  ```

**Expected Response:**
- **Status:** `400 Bad Request` hoặc `500 Internal Server Error`

---

### 7. Get Current Profile - Thiếu X-User-Id header

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/user-profiles/me`
- **Headers:**
  ```
  Authorization: Bearer {{accessToken}}
  ```
  *Không có header `X-User-Id`*

**Expected Response:**
- **Status:** `400 Bad Request`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Required request header 'X-User-Id' for method parameter type UUID is not present"
  }
  ```

---

### 8. Get Profile - User ID không tồn tại

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/user-profiles/00000000-0000-0000-0000-000000000000`
- **Headers:**
  ```
  Authorization: Bearer {{accessToken}}
  ```

**Expected Response:**
- **Status:** `404 Not Found`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Profile not found for userId: 00000000-0000-0000-0000-000000000000"
  }
  ```

**Postman Script:**
```javascript
pm.test("Status code is 404", function () {
    pm.response.to.have.status(404);
});

pm.test("Error message indicates profile not found", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.include("Profile not found");
});
```

---

### 9. Update Profile - User ID không tồn tại

**Request:**
- **Method:** `PUT`
- **URL:** `{{baseUrl}}/api/user-profiles/00000000-0000-0000-0000-000000000000`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer {{accessToken}}
  ```
- **Body (raw JSON):**
  ```json
  {
    "fullName": "Updated Name",
    "email": "updated@example.com",
    "department": "IT",
    "position": "Engineer"
  }
  ```

**Expected Response:**
- **Status:** `404 Not Found`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Profile not found for userId: 00000000-0000-0000-0000-000000000000"
  }
  ```

---

### 10. Get Current Profile - X-User-Id không hợp lệ (không phải UUID)

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/user-profiles/me`
- **Headers:**
  ```
  X-User-Id: invalid-uuid
  Authorization: Bearer {{accessToken}}
  ```

**Expected Response:**
- **Status:** `400 Bad Request`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'"
  }
  ```

---

### 11. Create Profile - User ID đã tồn tại

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/user-profiles`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer {{accessToken}}
  ```
- **Body (raw JSON):**
  ```json
  {
    "userId": "{{userId}}",
    "fullName": "Duplicate User",
    "email": "duplicate@example.com",
    "department": "IT",
    "position": "Engineer"
  }
  ```
*Sử dụng userId đã tồn tại*

**Expected Response:**
- **Status:** `400 Bad Request` hoặc `409 Conflict`
- **Body:** Error message về duplicate userId

---

### 12. Update Profile - Không có Authorization token (qua Gateway)

**Request:**
- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/users/profiles/{{userId}}`
- **Headers:**
  ```
  Content-Type: application/json
  ```
  *Không có Authorization header*

- **Body (raw JSON):**
  ```json
  {
    "fullName": "Updated Name"
  }
  ```

**Expected Response:**
- **Status:** `401 Unauthorized`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource"
  }
  ```

---

## Postman Collection Setup

### Tạo Collection Variables:

1. Tạo Collection mới: `UserService API`
2. Vào **Variables** tab, thêm các biến:
   - `baseUrl`: `http://localhost:8006` (hoặc `http://localhost:8080/api/users/profiles` nếu qua Gateway)
   - `accessToken`: (lấy từ AuthService login response)
   - `userId`: (để trống, sẽ tự động set sau khi tạo profile)
   - `xUserId`: (giống userId, dùng cho header X-User-Id)

### Test Flow:

1. **Login** (từ AuthService) → Lấy `accessToken`
2. **Create Profile** → Tạo profile mới (tự động lưu `userId`)
3. **Get Current Profile** → Test endpoint `/me` với `X-User-Id` header
4. **Get Profile by ID** → Test lấy profile theo userId
5. **Get All Profiles** → Test lấy danh sách tất cả profiles
6. **Update Profile** → Test cập nhật profile

### Tips:

- Sau khi **Create Profile**, `userId` sẽ tự động được lưu vào collection variables
- Header `X-User-Id` là bắt buộc cho endpoint `/me`
- Khi test qua API Gateway, cần thêm `Authorization: Bearer {{accessToken}}` vào tất cả requests
- Format ngày tháng: `YYYY-MM-DD` (ví dụ: `2023-01-15`)

---

## API Endpoints Summary

| Method | Endpoint | Description | Headers Required |
|--------|----------|-------------|------------------|
| POST | `/api/user-profiles` | Tạo hồ sơ mới | `Content-Type`, `Authorization` (nếu qua Gateway) |
| GET | `/api/user-profiles/me` | Lấy hồ sơ hiện tại | `X-User-Id`, `Authorization` (nếu qua Gateway) |
| GET | `/api/user-profiles/{userId}` | Lấy hồ sơ theo User ID | `Authorization` (nếu qua Gateway) |
| GET | `/api/user-profiles` | Lấy tất cả hồ sơ | `Authorization` (nếu qua Gateway) |
| PUT | `/api/user-profiles/{userId}` | Cập nhật hồ sơ | `Content-Type`, `Authorization` (nếu qua Gateway) |
