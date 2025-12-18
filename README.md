# Healthcare Store Backend API

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![MariaDB](https://img.shields.io/badge/Database-MariaDB-blue)
![JWT](https://img.shields.io/badge/Security-JWT-red)

A robust **Spring Boot REST API** for the **Healthcare Store** e-commerce platform. This system manages users, products, orders, and integrates advanced features like Online Payment and AI Chatbot assistance.

---

## Key Features

### User (Client)
- **Authentication:** Register (Email OTP verify), Login (JWT), Forgot Password, Update Profile.
- **Product Discovery:** Search, Filter (Price, Brand, Category), View Details.
- **Shopping:** Manage Cart (Add/Update/Remove), Apply Coupons.
- **Checkout:** Place orders, Online Payment integration (**VNPay**).
- **Order Tracking:** View order history and status.
- **AI Support:** Health advice chatbot powered by **Google Gemini 2.5 Flash**.

### Administrator (Admin)
- **Dashboard:** Statistics on revenue, orders, and new users.
- **Product Management:** CRUD operations, Hide/Show products, Image Upload (**Cloudinary**).
- **Category Management:** Manage product categories.
- **Order Management:** View details, Update status (Confirmed, Shipping, Delivered, Cancelled).
- **Coupon Management:** Create and manage discount codes.
- **User Management:** Ban/Unban user accounts.

---

## Tech Stack

- **Core:** Java 17, Spring Boot 3.x
- **Database:** MariaDB (or MySQL)
- **ORM:** Hibernate / Spring Data JPA
- **Security:** Spring Security, JWT (BCrypt)
- **Storage:** Cloudinary (Image hosting)
- **Payment:** VNPay API
- **AI:** Google Gemini API
- **Build Tool:** Maven

---

## Prerequisites

- **JDK 17+**
- **Maven 3.6+**
- **MariaDB** or **MySQL Server**
- **Docker** (Optional)

---

## Quick Start

### 1. Clone the repository
```bash
git clone [https://github.com/your-username/healthcare-store-backend.git](https://github.com/your-username/healthcare-store-backend.git)
cd healthcare-store-backend

```

### 2. Database Setup

Create a blank database in MariaDB/MySQL:

```sql
CREATE DATABASE healthcare_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

```

### 3. Configuration

Update `src/main/resources/application.properties` with your credentials:

```properties
# --- Database Configuration ---
spring.datasource.url=jdbc:mariadb://localhost:3306/healthcare_store
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# --- JWT Configuration ---
jwt.secret=YOUR_VERY_SECURE_SECRET_KEY
jwt.expiration=86400000

# --- Mail Configuration (For OTP) ---
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=YOUR_APP_PASSWORD

# --- Cloudinary (Image Upload) ---
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET

# --- VNPay Payment ---
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.hash-secret=YOUR_HASH_SECRET
vnpay.url=[https://sandbox.vnpayment.vn/paymentv2/vpcpay.html](https://sandbox.vnpayment.vn/paymentv2/vpcpay.html)

# --- Google Gemini AI ---
gemini.api-key=YOUR_GEMINI_API_KEY

```

### 4. Build and Run

```bash
mvn clean install
mvn spring-boot:run

```

The server will start at: `http://localhost:8386`

---

## API Documentation

Once the server is running, you can access the full API documentation via **Swagger UI**:

**URL:** `http://localhost:8386/swagger-ui/index.html`

---

## Project Structure

```
iuh.fit.se
├── config          # Security, Swagger, CORS configs
├── controllers     # REST Controllers (API endpoints)
├── dtos            # Data Transfer Objects
├── entities        # JPA Entities (Database models)
├── exceptions      # Global Exception Handling
├── repositories    # JPA Repositories
├── services        # Business Logic Layer
│   └── impl        # Service Implementations
└── utils           # Utilities (JwtUtil, VNPayUtil, etc.)

```

---

## Docker (Optional)

Build the image:

```bash
mvn clean package -DskipTests
docker build -t healthcare-store-backend:latest .

```

Run the container:

```bash
docker run -p 8080:8080 healthcare-store-backend:latest

```

---

## Contributors

**Group 5 - Class DHKTPM18A** *Industrial University of Ho Chi Minh City (IUH)*

| ID | Student Name | Role |
| --- | --- | --- |
| **20124551** | **Le Nguyen Sinh** | **Fullstack** (Checkout, Payment, Chatbot AI) |
| **20000325** | **Phan Vo Minh Thinh** | **Database Lead** (Auth, Admin Product/Category/User) |
| **20000735** | **Nguyen Phi Thien** | **UI/UX Lead** (Admin Order/Coupon, Dashboard, Auth) |
| **20000685** | **Dang Thi Mong Tho** | **Tester** (Filter, Cart, Product Detail, Testing) |

