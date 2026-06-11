-- ── Cart items ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cart_items (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id          BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    product_variant_id  BIGINT REFERENCES product_variants(id) ON DELETE SET NULL,
    quantity            INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items(user_id);

-- ── Wishlist ───────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS wishlist (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_wishlist_user ON wishlist(user_id);

-- ── Coupons ───────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS coupons (
    id                BIGSERIAL PRIMARY KEY,
    code              VARCHAR(50)    NOT NULL UNIQUE,
    type              VARCHAR(20)    NOT NULL CHECK (type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    value             DECIMAL(10,2)  NOT NULL,
    min_order_amount  DECIMAL(10,2)  NOT NULL DEFAULT 0,
    max_uses          INT            DEFAULT NULL,
    used_count        INT            NOT NULL DEFAULT 0,
    expires_at        TIMESTAMP      DEFAULT NULL,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_coupons_code ON coupons(code);

-- ── Coupon usages ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS coupon_usages (
    id          BIGSERIAL PRIMARY KEY,
    coupon_id   BIGINT NOT NULL REFERENCES coupons(id),
    user_id     BIGINT NOT NULL REFERENCES users(id),
    order_id    BIGINT REFERENCES orders(id),
    used_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(coupon_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_coupon_usages_coupon ON coupon_usages(coupon_id);
CREATE INDEX IF NOT EXISTS idx_coupon_usages_user   ON coupon_usages(user_id);

-- ── Add coupon + tracking fields to orders ────────────────────────────────────
ALTER TABLE orders ADD COLUMN IF NOT EXISTS coupon_code      VARCHAR(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount  DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS tracking_number  VARCHAR(100);
