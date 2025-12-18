# Smart Travel Booking Platform

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Microservices Details](#microservices-details)
4. [Installation & Setup](#installation--setup)
5. [API Documentation](#api-documentation)
6. [Postman Collection](#postman-collection)

---

## 🎯 Project Overview

The Smart Travel Booking Platform is a distributed, microservices-based travel booking system designed to demonstrate robust inter-service communication using modern Spring Boot 3.2 technologies. The platform is built with a strong focus on scalability, maintainability, and industry best practices, avoiding deprecated components and following modern reactive and declarative communication patterns.

The system consists of multiple independent microservices, each running on a separate port and backed by its own PostgreSQL database to ensure loose coupling and data isolation. Inter-service communication is implemented using Feign Client for synchronous service-to-service calls and WebClient for reactive, non-blocking interactions. All APIs are exposed as RESTful services and documented using Swagger/OpenAPI for ease of testing and integration.

The platform also includes structured exception handling, standardized response models, and clear API contracts, making it a reliable foundation for a real-world smart travel booking solution covering users, flights, hotels, payments, and notifications.


---

## 🏗️ System Architecture

### Architecture Diagram

```
                         ┌─────────────────┐
                         │   User Service  │
                         │   Port: 8091    │
                         └────────┬────────┘
                                  │
                                  │ WebClient
                                  │
         ┌────────────────────────┼────────────────────────┐
         │                        │                        │
         │                        ▼                        │
    ┌────┴─────┐         ┌───────────────┐         ┌───────┴────┐
    │  Flight  │◄────────│   BOOKING     │────────►│   Hotel    │
    │  Service │  Feign  │   SERVICE     │  Feign  │  Service   │
    │  :8092   │         │   :8094       │         │   :8093    │
    └──────────┘         │ (Orchestrator)│         └────────────┘
                         └───────┬───────┘
                                 │
                                 │ WebClient
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
                    ▼            ▼            ▼
            ┌──────────┐  ┌────────────┐  ┌──────────────┐
            │ Payment  │  │Notification│  │   Database   │
            │ Service  │  │  Service   │  │  PostgreSQL  │
            │  :8095   │  │   :8096    │  │              │
            └──────────┘  └────────────┘  └──────────────┘
                 │                │
                 │ WebClient      │
                 │                │
                 └────────────────┘
```

### Communication Flow

```
1. User Request → Booking Service
2. Booking Service → User Service (WebClient - Validate User)
3. Booking Service → Flight Service (Feign Client - Check Availability)
4. Booking Service → Hotel Service (Feign Client - Check Availability)
5. Booking Service → Payment Service (WebClient - Process Payment)
6. Booking Service → Notification Service (WebClient - Send Confirmation)
7. System Response → User
```

---

## 🔧 Microservices Details

### 1. User Service (Port 8091)
**Purpose:** Authenticates and Manages user accounts

**Database:** `user_service_db`

**Endpoints:**
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Soft delete user
- `GET /api/users/{id}/validate` - Validate user for booking

---

### 2. Flight Service (Port 8092)
**Purpose:** Manages flight operations and seat availability

**Database:** `flight_service_db`

**Endpoints:**
- `POST /api/flights` - Create flight
- `GET /api/flights/{id}` - Get flight details
- `GET /api/flights/search` - Search flights
- `GET /api/flights/{id}/availability` - Check seat availability
- `POST /api/flights/{id}/book` - Book seats
- `POST /api/flights/{id}/release` - Release seats


---

### 3. Hotel Service (Port 8093)
**Purpose:** Manages hotel information and room availability

**Database:** `hotel_service_db`

**Endpoints:**
- `POST /api/hotels` - Create hotel
- `GET /api/hotels/{id}` - Get hotel details
- `GET /api/hotels/search` - Search hotels
- `GET /api/hotels/{id}/availability` - Check room availability
- `POST /api/hotels/{id}/book` - Book rooms
- `POST /api/hotels/{id}/release` - Release rooms

---

### 4. Booking Service (Port 8094)
**Purpose:** Coordinates the entire booking workflow

**Database:** `booking_service_db`

**Endpoints:**
- `POST /api/bookings` - Create booking
- `GET /api/bookings/{id}` - Get booking details
- `GET /api/bookings/reference/{reference}` - Get by reference
- `GET /api/bookings/user/{userId}` - Get user bookings
- `POST /api/bookings/{id}/confirm` - Confirm booking
- `DELETE /api/bookings/{id}` - Cancel booking

---

### 5. Payment Service (Port 8095)
**Purpose:** Manages payment processing and transactions

**Database:** `payment_service_db`

**Endpoints:**
- `POST /api/payments` - Process payment
- `GET /api/payments/{id}` - Get payment details
- `GET /api/payments/booking/{bookingId}` - Get booking payments
- `POST /api/payments/{id}/refund` - Process refund

---

### 6. Notification Service (Port 8096)
**Purpose:** Sends notifications to users

**Database:** `notification_service_db`

**Endpoints:**
- `POST /api/notifications` - Send notification
- `POST /api/notifications/bulk` - Send bulk notifications
- `GET /api/notifications/user/{userId}` - Get user notifications
- `GET /api/notifications/{id}` - Get notification details

---

## 🚀 Installation & Setup

### Prerequisites
- **Java 17 or higher**
- **PostgreSQL 12 or higher**
- **Maven 3.6+**
- **Git**
- **Postman**
- **Web Browser**

### Step 1: Clone Repository
```bash
git clone <repository-url>
cd "Smart Travel Booking Platform"
```

### Step 2: Setup PostgreSQL Databases

#### Database Creation
Open pgAdmin or psql and run:
```sql
CREATE DATABASE user_service_db;
CREATE DATABASE flight_service_db;
CREATE DATABASE hotel_service_db;
CREATE DATABASE booking_service_db;
CREATE DATABASE payment_service_db;
CREATE DATABASE notification_service_db;
```

### Step 3: Configure Database Credentials
All services are configured with:
- **Username:** `postgres`
- **Password:** `navindu`
- **Host:** `localhost`
- **Port:** `5432`

If your PostgreSQL uses different credentials, update `application.yml` file in each service according to new credentials.

### Step 4: Build All Services

#### Windows:
```cmd
cd User-Service
mvnw.cmd clean install
cd ..

cd Flight-Service
mvnw.cmd clean install
cd ..

cd Hotel-Service
mvnw.cmd clean install
cd ..

cd Booking-Service
mvnw.cmd clean install
cd ..

cd Payment-Service
mvnw.cmd clean install
cd ..

cd Notification-Service
mvnw.cmd clean install
cd ..
```

---

## ▶️ Running the Application

### ⚠️ Important: Start Order

Services must be started in this specific order:

1. **User Service** (8091) - Required by Booking Service
2. **Flight Service** (8092) - Required by Booking Service
3. **Hotel Service** (8093) - Required by Booking Service
4. **Payment Service** (8095) - Called by Booking Service
5. **Notification Service** (8096) - Called by Booking Service
6. **Booking Service** (8094) - Main Orchestrator

### Starting Services

#### Terminal 1 - User Service:
```cmd
cd User-Service
./mvnw.cmd spring-boot:run
```

#### Terminal 2 - Flight Service:
```cmd
cd Flight-Service
./mvnw.cmd spring-boot:run
```

#### Terminal 3 - Hotel Service:
```cmd
cd Hotel-Service
./mvnw.cmd spring-boot:run
```

#### Terminal 4 - Payment Service:
```cmd
cd Payment-Service
./mvnw.cmd spring-boot:run
```

#### Terminal 5 - Notification Service:
```cmd
cd Notification-Service
./mvnw.cmd spring-boot:run
```

#### Terminal 6 - Booking Service:
```cmd
cd Booking-Service
./mvnw.cmd spring-boot:run
```

### Verify All Services Are Running

Check health endpoints:
```bash
curl http://localhost:8091/actuator/health
curl http://localhost:8092/actuator/health
curl http://localhost:8093/actuator/health
curl http://localhost:8094/actuator/health
curl http://localhost:8095/actuator/health
curl http://localhost:8096/actuator/health
```

All must return: `StatusCode : 200`

---

## 📖 API Documentation (Swagger UI)

Access interactive API documentation for each service:

| Service | Swagger UI URL |
|---------|---------------|
| User Service | http://localhost:8091/swagger-ui.html |
| Flight Service | http://localhost:8092/swagger-ui.html |
| Hotel Service | http://localhost:8093/swagger-ui.html |
| Booking Service | http://localhost:8094/swagger-ui.html |
| Payment Service | http://localhost:8095/swagger-ui.html |
| Notification Service | http://localhost:8096/swagger-ui.html |

---

## 📮 Postman Collection

### Import Postman Collection

1. Open Postman
2. Click **Import**
3. Select file: `Smart Travel Booking Platform.postman_collection.json`
4. Collection will be imported with all endpoints




**END OF README**