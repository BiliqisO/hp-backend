# Hafsah's Place - Fashion E-Commerce Backend

A comprehensive Spring Boot backend for a Lagos-based fashion e-commerce platform specializing in Asoebi, Bridal wear, Prom dresses, and custom designs.

## Table of Contents
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Quick Start](#quick-start)
- [Setup Instructions](#setup-instructions)
- [Admin Authentication](#admin-authentication)
- [API Endpoints](#api-endpoints)
- [Example Requests](#example-requests)
- [Project Structure](#project-structure)
- [Security Considerations](#security-considerations)
- [Troubleshooting](#troubleshooting)

## Tech Stack

- **Java 17** (compiled with Java 25)
- **Spring Boot 3.3.0**
- **PostgreSQL** - Primary database
- **Spring Data JPA** - ORM
- **Spring Security + JWT** - Authentication & Authorization
- **Flyway** - Database migrations
- **Cloudinary** - Image storage
- **Paystack** - Payment processing
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

## Features

### Core Functionality
- User authentication and authorization (JWT)
- Product catalog management
- Category management
- Shopping cart and orders
- Custom order requests
- Customer measurements
- Payment processing (Paystack integration)
- Product reviews and ratings
- Image upload and management (Cloudinary)

### User Roles
- **CUSTOMER** - Browse products, place orders, manage profile
- **ADMIN** - Full access to manage products, orders, and users
- **DESIGNER** - Handle custom order requests

## Quick Start

```bash
# 1. Clone the repository
git clone <repository-url>
cd hafsahs-place

# 2. Create PostgreSQL database
createdb hafsahsplace

# 3. Configure application.properties (see Setup Instructions)

# 4. Build and run
./mvnw clean install
./mvnw spring-boot:run

# 5. Test admin login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hafsahsplace.com","password":"Admin@123"}'
```

Default admin credentials:
- Email: `admin@hafsahsplace.com`
- Password: `Admin@123`

## Database Schema

The application uses PostgreSQL with the following main entities:
- Users & Roles
- Categories
- Products & Product Variants
- Product Images
- Orders & Order Items
- Custom Orders
- Measurements
- Payments
- Reviews

## Setup Instructions

### Prerequisites
- Java 17+ or Java 25 (recommended)
- PostgreSQL 13+
- Maven 3.8+
- Git

### 1. Database Setup

Create a PostgreSQL database:

```bash
createdb hafsahsplace
```

Or using psql:
```sql
CREATE DATABASE hafsahsplace;
```

### 2. Configure Application Properties

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/hafsahsplace
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# JWT Secret (Generate a secure random string)
jwt.secret=your-secret-key-change-this-in-production-make-it-at-least-256-bits

# Cloudinary Configuration
cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret

# Paystack Configuration
paystack.secret-key=your-paystack-secret-key
paystack.public-key=your-paystack-public-key
```

### 3. Build and Run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Database Migrations

Flyway will automatically run migrations on startup. The migrations create:
- All necessary tables
- Default roles (ROLE_CUSTOMER, ROLE_ADMIN, ROLE_DESIGNER)
- **Default admin account** (see Admin Authentication section below)
- Necessary indexes

### 5. Admin Authentication

**No default admin account is created.** For security reasons, admin accounts must be created manually during deployment.

#### Deployment Strategy

1. **First Deployment**: Technical manager registers as first admin via temporarily open endpoint
2. **Add Business Owner**: Technical manager creates admin account for business owner via API
3. **Ongoing Operations**: Both maintain admin access for their respective responsibilities

**Recommended Approach:**
- Technical manager: System maintenance, troubleshooting, database management
- Business owner: Product management, orders, customer operations

For detailed step-by-step deployment instructions, see [ADMIN_AUTHENTICATION_GUIDE.md](./ADMIN_AUTHENTICATION_GUIDE.md)

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new customer account
- `POST /api/auth/login` - Login (admin or customer) and get JWT token
- `POST /api/auth/register/admin` - Register new admin account (requires admin authentication)

### Products (Public)
- `GET /api/products` - Get all products (paginated)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/slug/{slug}` - Get product by slug
- `GET /api/products/category/{categoryId}` - Get products by category
- `GET /api/products/featured` - Get featured products
- `GET /api/products/latest` - Get latest products
- `GET /api/products/search?keyword={keyword}` - Search products

### Products (Admin Only)
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Example Requests

#### Register Customer
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane@example.com",
    "password": "password123",
    "phoneNumber": "+234801234567",
    "city": "Lagos",
    "state": "Lagos",
    "country": "Nigeria"
  }'
```

#### Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@hafsahsplace.com",
    "password": "Admin@123"
  }'
```

#### Login as Customer
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "password123"
  }'
```

#### Create New Admin (requires admin token)
```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "firstName": "New",
    "lastName": "Admin",
    "email": "newadmin@hafsahsplace.com",
    "password": "SecurePass123!",
    "phoneNumber": "+234809876543"
  }'
```

#### Get Products
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=12"
```

## Project Structure

```
src/main/java/com/biliqis/hafsahs_place/
├── config/             # Configuration classes (Security, Cloudinary)
├── controller/         # REST API controllers
├── dto/               # Data Transfer Objects
├── exception/         # Custom exceptions and handlers
├── model/             # JPA entities
├── repository/        # Spring Data JPA repositories
├── security/          # JWT and security components
├── service/           # Business logic layer
└── util/              # Utility classes

src/main/resources/
├── application.properties      # Application configuration
└── db/migration/              # Flyway migration scripts
```

## Next Steps

### Additional Controllers to Implement
1. **OrderController** - Handle order creation and management
2. **CustomOrderController** - Handle custom design requests
3. **CategoryController** - Manage product categories
4. **UserController** - User profile management
5. **PaymentController** - Payment initialization and verification
6. **ReviewController** - Product reviews and ratings
7. **MeasurementController** - Customer measurements

### Additional Services to Implement
1. **OrderService** - Order processing logic
2. **CategoryService** - Category management
3. **PaymentService** - Payment workflow integration

### Recommended Enhancements
1. Email notifications (order confirmations, password reset)
2. File upload endpoints for product images
3. Admin dashboard endpoints
4. Inventory management
5. Order tracking and status updates
6. Discount/coupon system
7. Wishlist functionality
8. API documentation with Swagger/OpenAPI
9. Unit and integration tests
10. Docker containerization

## Environment Variables

For production, use environment variables instead of hardcoding in application.properties:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/hafsahsplace
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
export PAYSTACK_SECRET_KEY=your_paystack_secret
```

## Security Considerations

1. **Change default admin password** - The default admin password (`Admin@123`) must be changed immediately in production
2. **Change default JWT secret** - Generate a strong random secret (at least 256 bits) for production
3. **Use HTTPS** in production - Never use HTTP for authentication in production
4. **Enable CORS properly** - Update CORS settings in `CorsConfig.java` for your frontend domain
5. **Secure Paystack keys** - Never commit API keys to version control
6. **Database credentials** - Use environment variables or secrets management
7. **JWT Token Expiration** - Configure appropriate token expiration times
8. **Rate Limiting** - Consider implementing rate limiting for authentication endpoints
9. **Input Validation** - All user inputs are validated using Jakarta Bean Validation
10. **Password Encryption** - All passwords are encrypted using BCrypt (strength 10)

### Authentication Flow

1. **Registration**: Users register via `/api/auth/register` and are automatically assigned the `ROLE_CUSTOMER` role
2. **Login**: Users login via `/api/auth/login` and receive a JWT token
3. **Authorization**: JWT token must be included in the `Authorization: Bearer <token>` header for protected routes
4. **Admin Access**: Routes under `/api/admin/**` require `ROLE_ADMIN`
5. **Admin Creation**: Only existing admins can create new admin accounts via `/api/auth/register/admin`

## License

This project is private and proprietary.

## Support

For issues or questions, contact the development team.
