-- Users and Authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'Nigeria',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Measurements
CREATE TABLE measurements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bust DECIMAL(5,2),
    waist DECIMAL(5,2),
    hips DECIMAL(5,2),
    shoulder_width DECIMAL(5,2),
    arm_length DECIMAL(5,2),
    dress_length DECIMAL(5,2),
    neck_circumference DECIMAL(5,2),
    inseam DECIMAL(5,2),
    unit VARCHAR(10) DEFAULT 'cm',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    slug VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2) NOT NULL,
    is_customizable BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE,
    slug VARCHAR(255) NOT NULL UNIQUE,
    sku VARCHAR(100) UNIQUE,
    fabric_type VARCHAR(100),
    care_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product Images
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    cloudinary_public_id VARCHAR(255),
    alt_text VARCHAR(255),
    is_primary BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product Variants (sizes, colors, etc.)
CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    size VARCHAR(20),
    color VARCHAR(50),
    additional_price DECIMAL(10,2) DEFAULT 0.00,
    stock_quantity INT DEFAULT 0,
    sku VARCHAR(100) UNIQUE,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address TEXT NOT NULL,
    shipping_city VARCHAR(100) NOT NULL,
    shipping_state VARCHAR(100) NOT NULL,
    shipping_country VARCHAR(100) DEFAULT 'Nigeria',
    shipping_phone VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order Items
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_variant_id BIGINT REFERENCES product_variants(id),
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    custom_measurements JSONB,
    special_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Custom Orders
CREATE TABLE custom_orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    category_id BIGINT REFERENCES categories(id),
    design_description TEXT NOT NULL,
    reference_images JSONB,
    measurement_id BIGINT REFERENCES measurements(id),
    preferred_fabric VARCHAR(255),
    preferred_color VARCHAR(50),
    budget_range VARCHAR(100),
    event_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    estimated_price DECIMAL(10,2),
    final_price DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payments
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    custom_order_id BIGINT REFERENCES custom_orders(id),
    payment_reference VARCHAR(255) NOT NULL UNIQUE,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    gateway_response JSONB,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT payment_order_check CHECK (
        (order_id IS NOT NULL AND custom_order_id IS NULL) OR
        (order_id IS NULL AND custom_order_id IS NOT NULL)
    )
);

-- Reviews
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    comment TEXT,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, user_id)
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_CUSTOMER'), ('ROLE_ADMIN'), ('ROLE_DESIGNER');

-- Insert default categories
INSERT INTO categories (name, description, slug) VALUES
    ('Asoebi', 'Traditional Nigerian matching outfits for special occasions', 'asoebi'),
    ('Bridal Wear', 'Wedding gowns and bridal accessories', 'bridal-wear'),
    ('Prom Dresses', 'Elegant dresses for prom and formal events', 'prom-dresses'),
    ('Evening Wear', 'Sophisticated evening gowns and cocktail dresses', 'evening-wear'),
    ('Ankara Styles', 'Contemporary African print designs', 'ankara-styles');

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_product_images_product ON product_images(product_id);
CREATE INDEX idx_product_variants_product ON product_variants(product_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_custom_orders_user ON custom_orders(user_id);
CREATE INDEX idx_custom_orders_status ON custom_orders(status);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_custom_order ON payments(custom_order_id);
CREATE INDEX idx_reviews_product ON reviews(product_id);
