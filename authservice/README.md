# AuthService - Authentication & Authorization Service

## Base URL

- **Local:** `http://localhost:8005`
- **Via API Gateway:** `http://localhost:8080/api/auth`

---

## Postman Test Cases

### Environment Variables

Trong Postman, tạo các variables sau:
- `baseUrl`: `http://localhost:8005` (hoặc `http://localhost:8080/api/auth` nếu qua Gateway)
- `accessToken`: Lưu access token sau khi login
- `refreshToken`: Lưu refresh token sau khi login

---

### 1. Signup - Đăng ký tài khoản mới

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/signup`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "passwordConfirm": "password123"
  }
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body:**
  ```
  Signup successful for user: user@example.com
  ```

**Postman Script (để tự động lưu response):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains success message", function () {
    pm.expect(pm.response.text()).to.include("Signup successful");
});
```

---

### 2. Login - Đăng nhập

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON):**
  ```json
  {
    "userId": "30a2cc2f-7d29-4cd9-bd60-f26244a15a78",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "email": "user@example.com",
    "role": "[USER]"
  }
  ```

**Postman Script (tự động lưu tokens):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has tokens", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('accessToken');
    pm.expect(jsonData).to.have.property('refreshToken');
    
    // Lưu tokens vào variables
    pm.collectionVariables.set("accessToken", jsonData.accessToken);
    pm.collectionVariables.set("refreshToken", jsonData.refreshToken);
});
```

---

### 3. Refresh Token - Làm mới access token

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/refresh`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "accessToken": "{{accessToken}}",
    "refreshToken": "{{refreshToken}}"
  }
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body (JSON):**
  ```json
  {
    "newAccessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "newRefreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

**Postman Script (tự động cập nhật tokens):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has new tokens", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('newAccessToken');
    pm.expect(jsonData).to.have.property('newRefreshToken');
    
    // Cập nhật tokens mới
    pm.collectionVariables.set("accessToken", jsonData.newAccessToken);
    pm.collectionVariables.set("refreshToken", jsonData.newRefreshToken);
});
```

---

### 4. Logout - Đăng xuất

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/logout`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer {{accessToken}}
  ```
- **Body (raw JSON):**
  ```json
  {
    "refreshToken": "{{refreshToken}}"
  }
  ```

**Expected Response:**
- **Status:** `200 OK`
- **Body:**
  ```
  Logged out successfully
  ```

**Postman Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Logout successful", function () {
    pm.expect(pm.response.text()).to.include("Logged out successfully");
});
```

---

## Negative Test Cases

### 5. Signup - Email đã tồn tại

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/signup`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "passwordConfirm": "password123"
  }
  ```

**Expected Response:**
- **Status:** `400 Bad Request`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Email already registered"
  }
  ```

---

### 6. Signup - Mật khẩu không khớp

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/signup`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "newuser@example.com",
    "password": "password123",
    "passwordConfirm": "password456"
  }
  ```

**Expected Response:**
- **Status:** `400 Bad Request`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Passwords do not match"
  }
  ```

---

### 7. Login - Sai mật khẩu

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "user@example.com",
    "password": "wrongpassword"
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
    "message": "Invalid email or password"
  }
  ```

---

### 8. Refresh Token - Refresh token không hợp lệ

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/refresh`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "accessToken": "invalid_token",
    "refreshToken": "invalid_refresh_token"
  }
  ```

**Expected Response:**
- **Status:** `403 Forbidden` hoặc `401 Unauthorized`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Invalid or expired refresh token"
  }
  ```

---

### 9. Refresh Token - Refresh token đã bị blacklist

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/refresh`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "accessToken": "{{accessToken}}",
    "refreshToken": "{{refreshToken}}"
  }
  ```
*Lưu ý: Sử dụng refresh token sau khi đã logout*

**Expected Response:**
- **Status:** `403 Forbidden`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Refresh token is blacklisted"
  }
  ```

---

### 10. Logout - Thiếu Authorization header

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/logout`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "refreshToken": "{{refreshToken}}"
  }
  ```

**Expected Response:**
- **Status:** `403 Forbidden`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Missing or invalid Authorization header"
  }
  ```

---

### 11. Logout - Access token không hợp lệ

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/logout`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer invalid_token
  ```
- **Body (raw JSON):**
  ```json
  {
    "refreshToken": "{{refreshToken}}"
  }
  ```

**Expected Response:**
- **Status:** `403 Forbidden`
- **Body:**
  ```json
  {
    "timestamp": "2024-01-01T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Invalid or expired refresh token"
  }
  ```

---

## Postman Collection Setup

### Tạo Collection Variables:

1. Tạo Collection mới: `AuthService API`
2. Vào **Variables** tab, thêm các biến:
   - `baseUrl`: `http://localhost:8005`
   - `accessToken`: (để trống, sẽ tự động set sau khi login)
   - `refreshToken`: (để trống, sẽ tự động set sau khi login)

### Test Flow:

1. **Signup** → Tạo tài khoản mới
2. **Login** → Lấy access token và refresh token (tự động lưu vào variables)
3. **Refresh Token** → Test làm mới token (tự động cập nhật variables)
4. **Logout** → Đăng xuất và invalidate tokens

### Tips:

- Sau khi **Login**, tokens sẽ tự động được lưu vào collection variables
- Sau khi **Refresh Token**, tokens mới sẽ tự động cập nhật
- Sau khi **Logout**, có thể test lại refresh token để xác nhận đã bị blacklist
