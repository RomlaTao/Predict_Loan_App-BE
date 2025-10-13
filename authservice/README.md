# Script setup container

## MySQL (map cổng 3307 -> 3306)
docker run -d --name authservice-mysql -e MYSQL_DATABASE=authdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=authuser -e MYSQL_PASSWORD=authpass -p 3307:3306 -v auth_mysql_data:/var/lib/mysql mysql:8.4

# Postman testcases

1) Signup
- Method: POST
- URL: {{baseUrl}}/api/auth/signup
- Body (raw JSON):
```
{
  "email": "example@gmail.com",
  "password": "12345",
  "passwordConfirm": "12345"
}
```
- Response (String):
```
Signup successful for user: example@gmail.com
```

2) Login
- Method: POST
- URL: {{baseUrl}}/api/auth/login
- Body (raw JSON):
```
{
    "email": "example@gmail.com",
    "password": "12345"
}
```
- Response (raw JSON):
```
{
    "userId": "30a2cc2f-7d29-4cd9-bd60-f26244a15a78",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlQGdtYWlsLmNvbSIsInJvbGVzIjpbIlNUVURFTlQiXSwiaWF0IjoxNzYwMTQ5NDcwLCJleHAiOjE3NjAyMzU4NzB9.L9Rg4dmOtzWtL9EbXwF6zB6GiW2yc1uLfaOW91UzJLo",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlQGdtYWlsLmNvbSIsImlhdCI6MTc2MDE0OTQ3MCwiZXhwIjoxNzYwNTgxNDcwfQ.um6ljpblLhK8IPxFY8uDpYt__GUS7hhNmE6Ie5rKwsw",
    "tokenType": "Bearer",
    "email": "example@gmail.com",
    "role": "[STUDENT]"
}
```

3) Refresh
- Method: POST
- URL: {{baseUrl}}/api/auth/refresh
- Body (raw JSON):
```
{
  "refreshToken": "{Lấy refreshToken}"
}
```
- Response (raw JSON):
```
{
"userId": "30a2cc2f-7d29-4cd9-bd60-f26244a15a78",
"accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlQGdtYWlsLmNvbSIsInJvbGVzIjpbIlNUVURFTlQiXSwiaWF0IjoxNzYwMTQ5NjIwLCJleHAiOjE3NjAyMzYwMjB9.2MOMEcC7MGUNCBFB54cD6KdJuaVm4J3gVQBrHnZEhQI",
"refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlQGdtYWlsLmNvbSIsImlhdCI6MTc2MDE0OTQ3MCwiZXhwIjoxNzYwNTgxNDcwfQ.um6ljpblLhK8IPxFY8uDpYt__GUS7hhNmE6Ie5rKwsw",
"tokenType": "Bearer",
"email": "example@gmail.com",
"role": "[STUDENT]"
}
```

4) Logout
- Method: POST
- URL: {{baseUrl}}/api/auth/logout
- Headers: Authorization: Bearer {{accessToken}}

- Response (String):
```
Logged out successfully
```

## cURL testcases (qua API Gateway)

Giả định API Gateway chạy ở `http://localhost:8080`.

1) Signup
```bash
curl -i -X POST \
  http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "example@gmail.com",
    "password": "12345",
    "passwordConfirm": "12345"
  }'
```
Kỳ vọng: `200 OK` và body chứa chuỗi "Signup successful for user: example@gmail.com".

2) Login (nhận Access/Refresh token)
```bash
curl -s -X POST \
  http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "example@gmail.com",
    "password": "12345"
  }'
```
Kỳ vọng: JSON gồm `userId`, `accessToken`, `refreshToken`, `tokenType`, `email`, `role`.

3) Refresh (dùng refreshToken để lấy accessToken mới)
```bash
curl -s -X POST \
  http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```
Kỳ vọng: JSON chứa accessToken mới.

4) Logout (đưa access token vào blacklist)
```bash
curl -i -X POST \
  http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```
Kỳ vọng: `200 OK` và body "Logged out successfully".

### Negative cases
- Sai mật khẩu:
```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"example@gmail.com","password":"wrong"}'
```
Kỳ vọng: `401 Unauthorized` hoặc lỗi định nghĩa sẵn.

- Token bị blacklist khi gọi service khác:
  - Sau khi logout, dùng lại `<ACCESS_TOKEN>` để gọi `/api/users/**` qua Gateway sẽ nhận `401 Unauthorized`.