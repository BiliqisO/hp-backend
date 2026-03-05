# Hafsah's Place - Fashion E-Commerce Backend

A comprehensive Spring Boot backend for a Lagos-based fashion e-commerce platform specializing in Asoebi, Bridal wear, Prom dresses, and custom designs.

## Tech Stack

- **Java 24**
- **Spring Boot 4.0.3**
- **PostgreSQL** - Primary database
- **Spring Data JPA** - ORM
- **Spring Security + JWT** - Authentication & Authorization
- **Flyway** - Database migrations
- **Cloudinary** - Image storage
- **Paystack** - Payment processing
- **Lombok** - Boilerplate reduction

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
- Java 24+
- PostgreSQL 13+
- Maven 3.8+

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

Flyway will automatically run migrations on startup. The initial schema creates:
- All necessary tables
- Default roles (CUSTOMER, ADMIN, DESIGNER)
- Default product categories
- Necessary indexes

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

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

#### Register User
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

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "password123"
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

1. **Change default JWT secret** - Generate a strong random secret for production
2. **Use HTTPS** in production
3. **Enable CORS properly** - Update CORS settings for your frontend domain
4. **Secure Paystack keys** - Never commit API keys to version control
5. **Database credentials** - Use environment variables or secrets management

## License

This project is private and proprietary.

## Support

For issues or questions, contact the development team.
