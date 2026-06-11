-- Seed default admin user (password: admin123456)
INSERT INTO users (email, password, first_name, last_name, phone_number, is_active, country, created_at, updated_at)
VALUES (
    'admin@hafsahsplace.com',
    '$2a$10$zs9YBcqoPDvO9Ubf/UJheOU5voQ57ljJ2xPVHPnokZj/ngia1whaq',
    'Admin',
    'Hafsah',
    '08099999999',
    true,
    'Nigeria',
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Assign ROLE_ADMIN to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@hafsahsplace.com' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;
