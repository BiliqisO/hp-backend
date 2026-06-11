-- ============================================================
-- Testimonials table
-- ============================================================
CREATE TABLE testimonials (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    client_role VARCHAR(100),
    text TEXT NOT NULL,
    rating INT NOT NULL DEFAULT 5 CHECK (rating >= 1 AND rating <= 5),
    is_featured BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- FAQs table
-- ============================================================
CREATE TABLE faqs (
    id BIGSERIAL PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(100) DEFAULT 'general',
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Seed testimonials
-- ============================================================
INSERT INTO testimonials (client_name, client_role, text, rating, is_featured, display_order) VALUES
('Sarah Emmanuel', 'Bridal Client', 'Absolutely stunning! The dress arrived in just 3 days and fit perfectly. The quality is exceptional and I felt like a princess at my wedding.', 5, true, 1),
('Amaka Okafor', 'Asoebi Client', 'Hafsah''s Place transformed my vision into reality. The asoebi outfit was the talk of the party — everyone wanted to know where I got it!', 5, true, 2),
('Chiamaka Nwosu', 'Prom Client', 'I was blown away by the attention to detail. My prom dress was absolutely perfect and I received so many compliments. Will definitely be coming back!', 5, true, 3),
('Fatima Bello', 'Bridal Client', 'From consultation to delivery, the experience was seamless. The team truly listened to what I wanted and delivered beyond my expectations.', 5, true, 4),
('Ngozi Adeyemi', 'Evening Wear Client', 'The craftsmanship is unmatched. My evening gown was a showstopper at the gala. Thank you Hafsah''s Place for making me feel so special!', 5, true, 5),
('Aisha Mohammed', 'Traditional Wear Client', 'I ordered a traditional outfit for my engagement ceremony and it was perfection. The fabric quality and stitching were top-notch.', 5, true, 6);

-- ============================================================
-- Seed FAQs — Consultation
-- ============================================================
INSERT INTO faqs (question, answer, category, display_order, is_active) VALUES
('How long does the consultation take?', 'The Initial Session lasts about 30 minutes. Extended Sessions can run longer depending on your needs.', 'consultation', 1, true),
('What should I prepare for the consultation?', 'Bring reference images of styles you love, any fabric swatches you have in mind, and your event details (date, venue, dress code).', 'consultation', 2, true),
('Can I bring someone with me?', 'Yes! You''re welcome to bring a friend or family member to your in-person consultation for a second opinion.', 'consultation', 3, true),
('Can I book a virtual consultation?', 'Yes! We offer both in-person and virtual consultations. Virtual sessions are available via video call and work great for initial design discussions.', 'consultation', 4, true),
('Is the design sketch included in all packages?', 'A free custom sketch is included with the Initial Session. Extended Sessions include up to 2 sketch revisions. Essential Sessions can add sketches at an additional cost.', 'consultation', 5, true);

-- ============================================================
-- Seed FAQs — General
-- ============================================================
INSERT INTO faqs (question, answer, category, display_order, is_active) VALUES
('How long does production take?', 'Standard production takes 2-4 weeks depending on the complexity of the design. We also offer 72-hour express delivery for select styles with submitted measurements.', 'general', 1, true),
('What payment methods do you accept?', 'We accept bank transfers, card payments via Paystack, and installment plans for orders above ₦200,000.', 'general', 2, true),
('Do you ship internationally?', 'Yes! We ship worldwide. International delivery typically takes 5-10 business days depending on the destination.', 'general', 3, true),
('What is your return policy?', 'Custom-made pieces are non-returnable, but we offer free alterations within 14 days of delivery to ensure the perfect fit.', 'general', 4, true);

-- ============================================================
-- Update categories with better descriptions & image placeholders
-- ============================================================
UPDATE categories SET description = 'Traditional Nigerian matching outfits for special occasions — coordinated elegance for owambe and celebrations' WHERE slug = 'asoebi';
UPDATE categories SET description = 'Exquisite wedding gowns and bridal couture — timeless elegance for your special day' WHERE slug = 'bridal-wear';
UPDATE categories SET description = 'Elegant and glamorous dresses for prom and formal events — make your entrance unforgettable' WHERE slug = 'prom-dresses';
UPDATE categories SET description = 'Sophisticated evening gowns and cocktail dresses — refined elegance for the night' WHERE slug = 'evening-wear';
UPDATE categories SET description = 'Contemporary African print designs — vibrant, bold, and uniquely you' WHERE slug = 'ankara-styles';

-- ============================================================
-- Seed products with realistic data
-- ============================================================
INSERT INTO products (category_id, name, description, base_price, is_customizable, is_featured, is_available, slug, sku, fabric_type, care_instructions) VALUES
-- Asoebi (category_id = 1)
(1, 'Royal Asoebi Ensemble', 'Luxurious matching asoebi set featuring intricate beadwork and premium lace overlay. Perfect for owambe celebrations.', 85000.00, true, true, true, 'royal-asoebi-ensemble', 'ASO-001', 'French Lace', 'Dry clean only. Store in a garment bag away from direct sunlight.'),
(1, 'Elegant Aso-Oke Set', 'Hand-woven aso-oke fabric styled into a modern silhouette with traditional accents. Includes headtie and ipele.', 120000.00, true, true, true, 'elegant-aso-oke-set', 'ASO-002', 'Aso-Oke', 'Dry clean only. Iron on low heat with protective cloth.'),
(1, 'Lace Asoebi Blouse & Wrapper', 'Premium cord lace blouse paired with matching wrapper. Embellished with sequins and stone accents.', 65000.00, true, false, true, 'lace-asoebi-blouse-wrapper', 'ASO-003', 'Cord Lace', 'Hand wash in cold water. Dry clean recommended.'),

-- Bridal Wear (category_id = 2)
(2, 'Aurora Bridal Gown', 'Breathtaking cathedral-length bridal gown featuring Chantilly lace bodice with hand-sewn pearl details and flowing tulle skirt.', 850000.00, true, true, true, 'aurora-bridal-gown', 'BRD-001', 'Chantilly Lace & Tulle', 'Professional dry clean only. Store hanging in breathable garment bag.'),
(2, 'Serenity Ball Gown', 'Classic ball gown silhouette with sweetheart neckline, corset back, and cascading organza layers adorned with crystal beading.', 750000.00, true, true, true, 'serenity-ball-gown', 'BRD-002', 'Organza & Satin', 'Professional dry clean only. Handle crystals with care.'),
(2, 'Celestial Mermaid Dress', 'Figure-hugging mermaid silhouette with dramatic trumpet flare. Featuring embroidered floral appliqués and illusion back.', 680000.00, true, false, true, 'celestial-mermaid-dress', 'BRD-003', 'Crepe & Lace', 'Dry clean only. Steam to remove wrinkles.'),
(2, 'Ivory Dream A-Line', 'Timeless A-line wedding dress with bateau neckline, three-quarter sleeves, and chapel train. Delicate lace throughout.', 550000.00, true, true, true, 'ivory-dream-aline', 'BRD-004', 'French Lace & Silk', 'Professional dry clean only.'),

-- Prom Dresses (category_id = 3)
(3, 'Starlight Sequin Gown', 'Show-stopping sequined prom dress with thigh-high slit and open back. Fully lined with built-in boning for support.', 180000.00, false, true, true, 'starlight-sequin-gown', 'PRM-001', 'Sequin Mesh', 'Dry clean only. Do not iron directly on sequins.'),
(3, 'Velvet Rose Midi', 'Rich velvet midi dress with sweetheart neckline and puff sleeves. Elegant yet youthful — perfect for prom night.', 120000.00, false, true, true, 'velvet-rose-midi', 'PRM-002', 'Stretch Velvet', 'Dry clean recommended. Steam to remove wrinkles.'),
(3, 'Tulle Princess Dress', 'Multi-layered tulle ball gown with fitted corset top and full skirt. Available in blush, champagne, and ivory.', 150000.00, true, false, true, 'tulle-princess-dress', 'PRM-003', 'Tulle & Satin', 'Dry clean only. Store hanging.'),

-- Evening Wear (category_id = 4)
(4, 'Midnight Glamour Gown', 'Sleek floor-length evening gown in luxurious velvet with plunging neckline and dramatic cape detail.', 250000.00, true, true, true, 'midnight-glamour-gown', 'EVE-001', 'Velvet', 'Dry clean only. Hang to prevent creasing.'),
(4, 'Golden Hour Wrap Dress', 'Flowing silk wrap dress with metallic gold undertones. Flattering on all body types with adjustable waist tie.', 195000.00, false, true, true, 'golden-hour-wrap-dress', 'EVE-002', 'Silk Charmeuse', 'Dry clean only. Iron on silk setting with protective cloth.'),
(4, 'Emerald Cocktail Dress', 'Structured cocktail dress in rich emerald green with asymmetric hemline and statement bow detail at the shoulder.', 145000.00, false, false, true, 'emerald-cocktail-dress', 'EVE-003', 'Duchess Satin', 'Dry clean recommended.'),

-- Ankara Styles (category_id = 5)
(5, 'Modern Ankara Jumpsuit', 'Bold African print jumpsuit with wide-leg silhouette, cinched waist, and statement balloon sleeves.', 75000.00, true, true, true, 'modern-ankara-jumpsuit', 'ANK-001', 'Premium Ankara Cotton', 'Machine wash cold on gentle cycle. Iron on medium heat.'),
(5, 'Ankara Maxi Skirt Set', 'Two-piece set featuring high-waisted maxi skirt and fitted crop top in coordinating Ankara prints.', 65000.00, true, true, true, 'ankara-maxi-skirt-set', 'ANK-002', 'Ankara Wax Print', 'Machine wash cold. Air dry recommended.'),
(5, 'Contemporary Ankara Blazer Dress', 'Structured blazer dress in vibrant Ankara print. Features double-breasted closure and padded shoulders for a power look.', 85000.00, false, false, true, 'ankara-blazer-dress', 'ANK-003', 'Ankara Cotton Blend', 'Dry clean recommended. Iron on medium heat.');

-- Create indexes for new tables
CREATE INDEX idx_testimonials_featured ON testimonials(is_featured);
CREATE INDEX idx_faqs_category ON faqs(category);
CREATE INDEX idx_faqs_active ON faqs(is_active);
