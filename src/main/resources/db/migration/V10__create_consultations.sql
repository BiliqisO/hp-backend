-- ── Consultations ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS consultations (
    id                    BIGSERIAL PRIMARY KEY,
    booking_number        VARCHAR(50)  NOT NULL UNIQUE,
    full_name             VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    phone_number          VARCHAR(30),
    consultation_type     VARCHAR(20)  NOT NULL CHECK (consultation_type IN ('ONLINE', 'IN_PERSON')),
    dress_type            VARCHAR(50),
    preferred_date        DATE,
    preferred_time        VARCHAR(10),
    event_date            DATE,
    additional_info       TEXT,
    interested_in_sketches BOOLEAN NOT NULL DEFAULT FALSE,
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED')),
    admin_notes           TEXT,
    user_id               BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_consultations_booking  ON consultations(booking_number);
CREATE INDEX IF NOT EXISTS idx_consultations_email    ON consultations(email);
CREATE INDEX IF NOT EXISTS idx_consultations_status   ON consultations(status);
CREATE INDEX IF NOT EXISTS idx_consultations_user     ON consultations(user_id);
