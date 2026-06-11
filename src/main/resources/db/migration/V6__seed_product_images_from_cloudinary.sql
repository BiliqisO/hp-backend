-- ============================================================
-- Seed product_images from existing Cloudinary images
-- Maps images from Cloudinary folders to seeded products
-- ============================================================

-- Asoebi products (category: Asoebi, folder: hp-frontend/collections/aso-ebi)
-- Product 7: Royal Asoebi Ensemble
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (7, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416987/10f4926f-1eff-475e-8128-a1981255a2a4_trih4f.jpg', '10f4926f-1eff-475e-8128-a1981255a2a4_trih4f', 'Royal Asoebi Ensemble', true, 0);

-- Product 8: Elegant Aso-Oke Set
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (8, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417006/IMG_0701_hkolao.jpg', 'IMG_0701_hkolao', 'Elegant Aso-Oke Set', true, 0);

-- Product 9: Lace Asoebi Blouse & Wrapper
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (9, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417011/IMG_0697_hebrkh.jpg', 'IMG_0697_hebrkh', 'Lace Asoebi Blouse & Wrapper', true, 0);

-- Older Asoebi products (4, 5) — use traditional folder image + products folder image
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (4, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416993/dc1f3cfe-cb08-4a02-a398-abe66fad9e18_y86li6.jpg', 'dc1f3cfe-cb08-4a02-a398-abe66fad9e18_y86li6', 'Elegant Asoebi Dress', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (5, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417192/IMG_0683_qy2ssv.jpg', 'IMG_0683_qy2ssv', 'Aurora Evening Gown', true, 0);

-- Bridal Wear products (category: Bridal Wear, folder: hp-frontend/collections/wedding)
-- Product 10: Aurora Bridal Gown
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (10, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417095/IMG_0683_kqupvx.jpg', 'IMG_0683_kqupvx', 'Aurora Bridal Gown', true, 0);

-- Product 11: Serenity Ball Gown
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (11, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417104/IMG_0688_wvbuxl.jpg', 'IMG_0688_wvbuxl', 'Serenity Ball Gown', true, 0);

-- Prom / Party products (category: Prom Dresses, folder: hp-frontend/collections/party)
-- Product 14: Starlight Sequin Gown
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (14, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416938/IMG_1370_vm2jn9.jpg', 'IMG_1370_vm2jn9', 'Starlight Sequin Gown', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (14, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416966/IMG_1375_soj7u5.jpg', 'IMG_1375_soj7u5', 'Starlight Sequin Gown - Back View', false, 1);

-- Product 15: Velvet Rose Midi
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (15, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416972/IMG_1237_ohiuqk.jpg', 'IMG_1237_ohiuqk', 'Velvet Rose Midi', true, 0);

INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (15, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416977/IMG_1238_racxod.jpg', 'IMG_1238_racxod', 'Velvet Rose Midi - Detail', false, 1);

-- Product 16: Tulle Princess Dress
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (16, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417086/IMG_9631_wowiyb.jpg', 'IMG_9631_wowiyb', 'Tulle Princess Dress', true, 0);

-- Evening Wear products — use remaining party folder images
-- Product 17: Midnight Glamour Gown
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (17, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417112/IMG_0693_zonxex.jpg', 'IMG_0693_zonxex', 'Midnight Glamour Gown', true, 0);

-- Product 18: Golden Hour Wrap Dress
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (18, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417120/IMG_0692_rk98c0.jpg', 'IMG_0692_rk98c0', 'Golden Hour Wrap Dress', true, 0);

-- Product 19: Emerald Cocktail Dress
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (19, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417132/3ee1193e-f527-4e86-8117-acb3c2e46666_omfc01.jpg', '3ee1193e-f527-4e86-8117-acb3c2e46666_omfc01', 'Emerald Cocktail Dress', true, 0);

-- Bridal Wear — extra images from party folder for products without images
-- Product 12: Celestial Mermaid Dress
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (12, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417145/IMG_7833_a6kbt7.jpg', 'IMG_7833_a6kbt7', 'Celestial Mermaid Dress', true, 0);

-- Product 13: Ivory Dream A-Line (re-use a wedding folder image as secondary angle)
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (13, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417095/IMG_0683_kqupvx.jpg', 'IMG_0683_kqupvx_13', 'Ivory Dream A-Line', true, 0);

-- Ankara Styles — no dedicated folder exists, reuse some available images
-- Product 20: Modern Ankara Jumpsuit
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (20, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773416987/10f4926f-1eff-475e-8128-a1981255a2a4_trih4f.jpg', '10f4926f-1eff-475e-8128-a1981255a2a4_trih4f_20', 'Modern Ankara Jumpsuit', true, 0);

-- Product 21: Ankara Maxi Skirt Set
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (21, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417011/IMG_0697_hebrkh.jpg', 'IMG_0697_hebrkh_21', 'Ankara Maxi Skirt Set', true, 0);

-- Product 22: Contemporary Ankara Blazer Dress
INSERT INTO product_images (product_id, image_url, cloudinary_public_id, alt_text, is_primary, display_order)
VALUES (22, 'https://res.cloudinary.com/dhiwfue0h/image/upload/v1773417006/IMG_0701_hkolao.jpg', 'IMG_0701_hkolao_22', 'Contemporary Ankara Blazer Dress', true, 0);
