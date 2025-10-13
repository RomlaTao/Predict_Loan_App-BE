## Chạy MySQL bằng Docker

```bash
docker run -d --name userservice-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=userservice -e MYSQL_USER=userservice -e MYSQL_PASSWORD=userservice -p 3308:3306 -v userservice_mysql_data:/var/lib/mysql mysql:8.0
```

## API Endpoints
Base URL (qua API Gateway): `http://localhost:8080/api/users/profiles`

1) Tạo hồ sơ người dùng
- **POST** `/`
- Body (JSON) theo `UserProfileRequestDto`:
```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "Nguyen Van A",
  "age": 30,
  "experience": 5,
  "income": 25000.0,
  "family": 3,
  "ccAvg": 200.5,
  "education": 2,
  "mortgage": 50.0,
  "securitiesAccount": true,
  "cdAccount": false,
  "online": true,
  "creditCard": true
}
```

2) Lấy hồ sơ hiện tại (dựa theo `X-User-Id` do Gateway thêm)
- **GET** `/me`
- Headers yêu cầu: `X-User-Id: <UUID>`

3) Lấy hồ sơ theo `userId`
- **GET** `/{userId}`

4) Cập nhật hồ sơ theo `userId`
- **PUT** `/{userId}`

## cURL testcases (qua API Gateway)

Giả định API Gateway chạy ở `http://localhost:8080`, route userservice là `/api/users/**` và JWT đã hợp lệ.

1) Tạo hồ sơ người dùng
```bash
curl -i -X POST \
  http://localhost:8080/api/users/profiles \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "name": "Nguyen Van A",
    "age": 30,
    "experience": 5,
    "income": 25000.0,
    "family": 3,
    "ccAvg": 200.5,
    "education": 2,
    "mortgage": 50.0,
    "securitiesAccount": true,
    "cdAccount": false,
    "online": true,
    "creditCard": true
  }'
```
Kỳ vọng: `201 Created` hoặc `200 OK` với body `UserProfileResponseDto`.

2) Lấy hồ sơ hiện tại (theo token)
```bash
curl -s \
  http://localhost:8080/api/users/profiles/me \
  -H "X-User-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
```
Kỳ vọng: `200 OK` với thông tin hồ sơ của người dùng từ token (`sub`).

3) Lấy hồ sơ theo userId
```bash
curl -s \
  http://localhost:8080/api/users/profiles/3fa85f64-5717-4562-b3fc-2c963f66afa6 \

```
Kỳ vọng: `200 OK` hoặc `404 Not Found` nếu không tồn tại.

4) Cập nhật hồ sơ theo userId
```bash
curl -i -X PUT \
  http://localhost:8080/api/users/profiles/3fa85f64-5717-4562-b3fc-2c963f66afa6 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nguyen Van B",
    "age": 31,
    "experience": 6,
    "income": 30000.0,
    "family": 4,
    "ccAvg": 250.0,
    "education": 2,
    "mortgage": 45.0,
    "securitiesAccount": true,
    "cdAccount": true,
    "online": true,
    "creditCard": true
  }'
```
Kỳ vọng: `200 OK` với body hồ sơ sau cập nhật.

### Negative cases
- Thiếu header `X-User-Id` khi gọi `/me`:
```bash
curl -i http://localhost:8080/api/users/profiles/me
```
Kỳ vọng: lỗi 400 Bad Request (thiếu header) hoặc lỗi do validation.

- Không tìm thấy hồ sơ:
```bash
curl -i -H "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/api/users/profiles/00000000-0000-0000-0000-000000000000
```
Kỳ vọng: `404 Not Found` với `ErrorResponse`.