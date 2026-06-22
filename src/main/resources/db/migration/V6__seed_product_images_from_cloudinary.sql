-- ============================================================
-- Seed product_images from existing Cloudinary images
-- Uses subqueries on slug to avoid hardcoded product IDs
-- ============================================================

-- Asoebi products
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'royal-asoebi-ensemble'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416987/10f4926f-1eff-475e-8128-a1981255a2a4_trih4f.jpg', '10f4926f-1eff-475e-8128-a1981255a2a4_trih4f', 'Royal Asoebi Ensemble', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'elegant-aso-oke-set'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417006/IMG_0701_hkolao.jpg', 'IMG_0701_hkolao', 'Elegant Aso-Oke Set', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'lace-asoebi-blouse-wrapper'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417011/IMG_0697_hebrkh.jpg', 'IMG_0697_hebrkh', 'Lace Asoebi Blouse & Wrapper', true, 0);

-- Bridal Wear products
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'aurora-bridal-gown'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417095/IMG_0683_kqupvx.jpg', 'IMG_0683_kqupvx', 'Aurora Bridal Gown', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'serenity-ball-gown'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417104/IMG_0688_wvbuxl.jpg', 'IMG_0688_wvbuxl', 'Serenity Ball Gown', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'celestial-mermaid-dress'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417145/IMG_7833_a6kbt7.jpg', 'IMG_7833_a6kbt7', 'Celestial Mermaid Dress', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'ivory-dream-aline'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417095/IMG_0683_kqupvx.jpg', 'IMG_0683_kqupvx_13', 'Ivory Dream A-Line', true, 0);

-- Prom Dresses
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'starlight-sequin-gown'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416938/IMG_1370_vm2jn9.jpg', 'IMG_1370_vm2jn9', 'Starlight Sequin Gown', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'starlight-sequin-gown'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416966/IMG_1375_soj7u5.jpg', 'IMG_1375_soj7u5', 'Starlight Sequin Gown - Back View', false, 1);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'velvet-rose-midi'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416972/IMG_1237_ohiuqk.jpg', 'IMG_1237_ohiuqk', 'Velvet Rose Midi', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'velvet-rose-midi'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416977/IMG_1238_racxod.jpg', 'IMG_1238_racxod', 'Velvet Rose Midi - Detail', false, 1);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'tulle-princess-dress'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417086/IMG_9631_wowiyb.jpg', 'IMG_9631_wowiyb', 'Tulle Princess Dress', true, 0);

-- Evening Wear
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'midnight-glamour-gown'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417112/IMG_0693_zonxex.jpg', 'IMG_0693_zonxex', 'Midnight Glamour Gown', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'golden-hour-wrap-dress'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417120/IMG_0692_rk98c0.jpg', 'IMG_0692_rk98c0', 'Golden Hour Wrap Dress', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'emerald-cocktail-dress'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417132/3ee1193e-f527-4e86-8117-acb3c2e46666_omfc01.jpg', '3ee1193e-f527-4e86-8117-acb3c2e46666_omfc01', 'Emerald Cocktail Dress', true, 0);

-- Ankara Styles
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'modern-ankara-jumpsuit'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416987/10f4926f-1eff-475e-8128-a1981255a2a4_trih4f.jpg', '10f4926f-1eff-475e-8128-a1981255a2a4_trih4f_20', 'Modern Ankara Jumpsuit', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'ankara-maxi-skirt-set'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417011/IMG_0697_hebrkh.jpg', 'IMG_0697_hebrkh_21', 'Ankara Maxi Skirt Set', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES ((SELECT id FROM products WHERE slug = 'ankara-blazer-dress'), 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417006/IMG_0701_hkolao.jpg', 'IMG_0701_hkolao_22', 'Contemporary Ankara Blazer Dress', true, 0);
