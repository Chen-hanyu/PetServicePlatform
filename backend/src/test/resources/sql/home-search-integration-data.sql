INSERT INTO users (id, role, phone, nickname, avatar_url, gender, status, bio, created_at, updated_at) VALUES
    (1, 'USER', '13800000001', 'Alice', '/uploads/u1.png', 'FEMALE', 'ACTIVE', 'Pet lover', '2026-03-01 09:00:00', '2026-03-01 09:00:00');

INSERT INTO banners (id, title, image_url, link_url, status, sort, created_by, created_at, updated_at) VALUES
    (1, 'Spring Care Week', '/uploads/banner-1.png', '/campaign/spring', 'ACTIVE', 1, 1, '2026-03-20 08:00:00', '2026-03-20 08:00:00'),
    (2, 'Adoption Festival', '/uploads/banner-2.png', '/campaign/adoption', 'ACTIVE', 2, 1, '2026-03-20 08:30:00', '2026-03-20 08:30:00'),
    (3, 'Hidden Banner', '/uploads/banner-3.png', '/campaign/hidden', 'INACTIVE', 3, 1, '2026-03-20 09:00:00', '2026-03-20 09:00:00');

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at, created_at, updated_at) VALUES
    (1, 1, 'CARE', 'Spring Grooming Tips', 'Spring grooming checklist for cats and dogs.', '/uploads/post-1.png', 'APPROVED', NULL, 12, 4, 3, '2026-03-20 09:00:00', '2026-03-20 09:00:00', '2026-03-20 09:00:00'),
    (2, 1, 'DAILY', 'Cozy Pet Morning', 'Warm morning feeding notes.', '/uploads/post-2.png', 'APPROVED', NULL, 5, 2, 1, '2026-03-19 09:00:00', '2026-03-19 09:00:00', '2026-03-19 09:00:00'),
    (3, 1, 'CARE', 'Pending Spring Draft', 'Should not appear in public data.', '/uploads/post-3.png', 'PENDING', NULL, 0, 0, 0, NULL, '2026-03-18 09:00:00', '2026-03-18 09:00:00');

INSERT INTO adoption_pets (id, name, type, breed, gender, age_desc, city, health_status, personality, adoption_requirements, story, status, cover_url, created_at, updated_at) VALUES
    (1, 'Spring Dumpling', 'CAT', 'British Shorthair', 'FEMALE', '2 years', 'Shanghai', 'Vaccinated', 'Gentle', 'Secure windows', 'Spring rescue story', 'ONLINE', '/uploads/adoption-1.png', '2026-03-20 10:00:00', '2026-03-20 10:00:00'),
    (2, 'Mochi', 'CAT', 'Ragdoll', 'MALE', '1 year', 'Shanghai', 'Vaccinated', 'Playful', 'Indoor home', 'Friendly cat', 'ONLINE', '/uploads/adoption-2.png', '2026-03-19 10:00:00', '2026-03-19 10:00:00');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status, created_at, updated_at) VALUES
    (1, 'Spring Paws House', 'Pudong', '188 Jinxiu Road', '13800000000', '10:00-20:00', 4.8, 'ACTIVE', '2026-03-20 09:00:00', '2026-03-20 09:00:00'),
    (2, 'Warm Vet Clinic', 'Xuhui', '99 Tianping Road', '13800000002', '09:00-18:00', 4.7, 'ACTIVE', '2026-03-19 09:00:00', '2026-03-19 09:00:00');

INSERT INTO products (id, category_id, name, subtitle, image_url, price, stock, pet_type, status, description, created_at, updated_at) VALUES
    (1, 1, 'Spring Cat Food', 'Balanced nutrition', '/uploads/product-1.png', 59.90, 20, 'CAT', 'ON_SALE', 'Spring formula for cats.', '2026-03-20 09:00:00', '2026-03-20 09:00:00'),
    (2, 1, 'Daily Dog Snacks', 'Low fat snack', '/uploads/product-2.png', 29.90, 30, 'DOG', 'ON_SALE', 'Healthy training snack.', '2026-03-19 09:00:00', '2026-03-19 09:00:00');

INSERT INTO recommendations (id, biz_type, biz_id, slot_code, status, sort, created_by, created_at) VALUES
    (1, 'post', 2, 'HOME_POST', 'ACTIVE', 1, 1, '2026-03-20 11:00:00'),
    (2, 'post', 1, 'HOME_POST', 'ACTIVE', 2, 1, '2026-03-20 11:00:00'),
    (3, 'service', 2, 'HOME_SERVICE', 'ACTIVE', 1, 1, '2026-03-20 11:00:00'),
    (4, 'service', 1, 'HOME_SERVICE', 'ACTIVE', 2, 1, '2026-03-20 11:00:00'),
    (5, 'product', 2, 'HOME_PRODUCT', 'ACTIVE', 1, 1, '2026-03-20 11:00:00'),
    (6, 'product', 1, 'HOME_PRODUCT', 'ACTIVE', 2, 1, '2026-03-20 11:00:00');

INSERT INTO product_categories (id, name, pet_type, sort, status) VALUES
    (1, 'Staple Food', 'CAT', 1, 'ACTIVE');
