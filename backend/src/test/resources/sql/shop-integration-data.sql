INSERT INTO product_categories (id, name, pet_type, sort, status)
VALUES
    (1, '主粮', 'CAT', 1, 'ACTIVE');

INSERT INTO products (id, category_id, name, subtitle, image_url, price, stock, pet_type, status, description, created_at, updated_at)
VALUES
    (1, 1, '幼猫主粮', '高蛋白配方', '/uploads/product-1.png', 49.90, 10, 'CAT', 'ON_SALE', '适合幼猫成长', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (2, 1, '冻干零食', '训练奖励', '/uploads/product-2.png', 29.90, 5, 'CAT', 'ON_SALE', '适口性好', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO cart_items (id, user_id, product_id, quantity, checked, created_at, updated_at)
VALUES
    (1, 2, 1, 2, TRUE, TIMESTAMP '2026-03-20 09:00:00', TIMESTAMP '2026-03-20 09:00:00'),
    (2, 2, 2, 1, TRUE, TIMESTAMP '2026-03-20 09:05:00', TIMESTAMP '2026-03-20 09:05:00');
