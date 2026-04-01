USE pet_service_platform;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM recommendations;
DELETE FROM banners;
DELETE FROM shop_order_items;
DELETE FROM shop_orders;
DELETE FROM cart_items;
DELETE FROM products;
DELETE FROM product_categories;
DELETE FROM merchant_reviews;
DELETE FROM service_bookings;
DELETE FROM merchant_services;
DELETE FROM merchants;
DELETE FROM service_categories;
DELETE FROM adoption_applications;
DELETE FROM adoption_pets;
DELETE FROM post_tags;
DELETE FROM tags;
DELETE FROM post_favorites;
DELETE FROM post_likes;
DELETE FROM post_comments;
DELETE FROM community_posts;
DELETE FROM pet_albums;
DELETE FROM pet_weights;
DELETE FROM pet_vaccines;
DELETE FROM pets;
DELETE FROM messages;
DELETE FROM users;

ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE messages AUTO_INCREMENT = 1;
ALTER TABLE pets AUTO_INCREMENT = 1;
ALTER TABLE pet_vaccines AUTO_INCREMENT = 1;
ALTER TABLE pet_weights AUTO_INCREMENT = 1;
ALTER TABLE pet_albums AUTO_INCREMENT = 1;
ALTER TABLE community_posts AUTO_INCREMENT = 1;
ALTER TABLE post_comments AUTO_INCREMENT = 1;
ALTER TABLE post_likes AUTO_INCREMENT = 1;
ALTER TABLE post_favorites AUTO_INCREMENT = 1;
ALTER TABLE tags AUTO_INCREMENT = 1;
ALTER TABLE post_tags AUTO_INCREMENT = 1;
ALTER TABLE adoption_pets AUTO_INCREMENT = 1;
ALTER TABLE adoption_applications AUTO_INCREMENT = 1;
ALTER TABLE service_categories AUTO_INCREMENT = 1;
ALTER TABLE merchants AUTO_INCREMENT = 1;
ALTER TABLE merchant_services AUTO_INCREMENT = 1;
ALTER TABLE merchant_reviews AUTO_INCREMENT = 1;
ALTER TABLE service_bookings AUTO_INCREMENT = 1;
ALTER TABLE product_categories AUTO_INCREMENT = 1;
ALTER TABLE products AUTO_INCREMENT = 1;
ALTER TABLE cart_items AUTO_INCREMENT = 1;
ALTER TABLE shop_orders AUTO_INCREMENT = 1;
ALTER TABLE shop_order_items AUTO_INCREMENT = 1;
ALTER TABLE banners AUTO_INCREMENT = 1;
ALTER TABLE recommendations AUTO_INCREMENT = 1;

INSERT INTO users (id, role, phone, nickname, avatar_url, gender, status, bio) VALUES
(1, 'ADMIN', '13900000000', '系统管理员', 'https://example.com/admin.jpg', 'UNKNOWN', 'ACTIVE', '负责平台内容与运营管理'),
(2, 'USER', '13800000001', '团子妈', 'https://example.com/user1.jpg', 'FEMALE', 'ACTIVE', '两只猫的铲屎官'),
(3, 'USER', '13800000002', '柴犬研究员', 'https://example.com/user2.jpg', 'MALE', 'ACTIVE', '热爱宠物护理和训练'),
(4, 'USER', '13800000003', '猫咪观察员', 'https://example.com/user3.jpg', 'FEMALE', 'ACTIVE', '喜欢记录毛孩子的日常');

INSERT INTO messages (user_id, type, title, content, is_read) VALUES
(2, 'SYSTEM', '领养申请已提交', '你提交的领养申请已进入审核流程，请保持电话畅通。', 0),
(2, 'INTERACTION', '收到新的评论', '你的帖子《幼猫疫苗时间表整理》收到了新评论。', 0),
(3, 'SYSTEM', '预约提醒', '你预约的宠物洗护服务将在明天下午开始，请提前到店。', 1),
(4, 'SYSTEM', '订单已发货', '你购买的冻干零食已发货，请留意物流动态。', 0);

INSERT INTO pets (id, user_id, name, type, breed, gender, birthday, weight, avatar_url, description) VALUES
(1, 2, '团子', 'CAT', '英短蓝猫', 'MALE', '2024-04-12', 4.20, 'https://example.com/pet-tuanzi.jpg', '性格稳定，喜欢晒太阳'),
(2, 2, '糯米', 'CAT', '布偶猫', 'FEMALE', '2024-08-01', 3.50, 'https://example.com/pet-nuomi.jpg', '很黏人，喜欢被梳毛'),
(3, 3, '阿柴', 'DOG', '柴犬', 'MALE', '2023-11-08', 8.60, 'https://example.com/pet-achai.jpg', '活泼好动，外出精力旺盛');

INSERT INTO pet_vaccines (pet_id, vaccine_name, vaccinated_at, next_due_at, remark) VALUES
(1, '猫三联', '2025-01-10', '2026-01-10', '基础免疫已完成'),
(2, '狂犬疫苗', '2025-02-18', '2026-02-18', '接种后状态正常');

INSERT INTO pet_weights (pet_id, weight, recorded_at) VALUES
(1, 4.10, '2026-03-01 09:00:00'),
(1, 4.20, '2026-03-18 09:00:00'),
(2, 3.50, '2026-03-15 20:30:00');

INSERT INTO pet_albums (pet_id, image_url, caption) VALUES
(1, 'https://example.com/pet-album-1.jpg', '第一次晒太阳'),
(2, 'https://example.com/pet-album-2.jpg', '午睡时间');

INSERT INTO tags (id, name, type, status, sort) VALUES
(1, '新手养宠', 'community', 'ACTIVE', 1),
(2, '疫苗', 'community', 'ACTIVE', 2),
(3, '洗护', 'community', 'ACTIVE', 3);

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at) VALUES
(1, 2, 'knowledge', '幼猫疫苗时间表整理', '整理了幼猫常见疫苗接种节点和注意事项，适合第一次养猫的同学收藏。', 'https://example.com/post-cover-1.jpg', 'APPROVED', '内容符合社区规范', 12, 6, 2, '2026-03-18 10:00:00'),
(2, 3, 'daily', '柴犬春季掉毛护理经验', '最近家里掉毛明显增多，我整理了自己这两周用过的梳毛和清洁办法。', 'https://example.com/post-cover-2.jpg', 'APPROVED', '内容符合社区规范', 8, 3, 1, '2026-03-19 09:30:00'),
(3, 4, 'help', '第一次领养猫咪要准备什么', '准备去领养小猫，想先了解猫砂、猫粮、猫窝和疫苗这些基础事项。', 'https://example.com/post-cover-3.jpg', 'PENDING', NULL, 0, 0, 0, NULL);

INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2), (2, 1), (2, 3), (3, 1);

INSERT INTO post_comments (id, post_id, user_id, content, status, created_at) VALUES
(1, 1, 3, '这篇整理得很清楚，已经收藏了。', 'NORMAL', '2026-03-18 13:20:00'),
(2, 1, 4, '想问一下猫三联加强针的时间间隔怎么安排？', 'NORMAL', '2026-03-18 18:00:00'),
(3, 2, 2, '换季真的要注意皮肤护理，感谢分享。', 'NORMAL', '2026-03-19 11:00:00');

INSERT INTO post_likes (post_id, user_id) VALUES
(1, 3), (1, 4), (2, 2);

INSERT INTO post_favorites (post_id, user_id) VALUES
(1, 3), (1, 4), (2, 2);

INSERT INTO adoption_pets (id, name, type, breed, gender, age_desc, city, health_status, personality, adoption_requirements, story, status, cover_url) VALUES
(1, '奶糕', 'CAT', '中华田园猫', 'FEMALE', '4个月', '上海', '已完成基础体检和驱虫', '亲人活泼', '需稳定居住环境，有封窗条件', '在小区被救助后逐渐恢复健康，现在状态很好。', 'ONLINE', 'https://example.com/adoption-pet-1.jpg'),
(2, '豆包', 'DOG', '柯基', 'MALE', '1岁', '上海', '疫苗齐全', '喜欢互动', '需有遛狗时间，接受回访', '原主人搬家无法继续照顾，正在寻找新家庭。', 'ONLINE', 'https://example.com/adoption-pet-2.jpg');

INSERT INTO adoption_applications (pet_id, user_id, experience_desc, living_condition_desc, contact_phone, status, review_remark, reviewed_by, reviewed_at, created_at) VALUES
(1, 2, '有两年养猫经验，熟悉喂药和日常护理。', '自有住房，已封窗，可长期照顾。', '13800000001', 'PENDING', NULL, NULL, NULL, '2026-03-19 20:00:00'),
(2, 4, '曾经照顾过中型犬，能接受训练安排。', '家庭成员支持领养，附近有宠物医院。', '13800000003', 'APPROVED', '资料完整，沟通情况良好。', 1, '2026-03-19 16:00:00', '2026-03-18 15:00:00');

INSERT INTO service_categories (id, name, sort, status) VALUES
(1, '洗护', 1, 'ACTIVE'),
(2, '寄养', 2, 'ACTIVE'),
(3, '体检', 3, 'ACTIVE');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status) VALUES
(1, '毛孩子洗护屋', '浦东新区', '锦绣路 188 号', '021-55550001', '10:00-20:00', 4.8, 'ACTIVE'),
(2, '安心宠物诊所', '徐汇区', '天钥桥路 66 号', '021-55550002', '09:00-18:00', 4.7, 'ACTIVE');

INSERT INTO merchant_services (id, merchant_id, category_id, name, price, duration_minutes, status) VALUES
(1, 1, 1, '基础洗护', 128.00, 90, 'ACTIVE'),
(2, 1, 2, '短期寄养', 150.00, 1440, 'ACTIVE'),
(3, 2, 3, '年度体检', 299.00, 60, 'ACTIVE');

INSERT INTO service_bookings (user_id, merchant_id, merchant_service_id, booking_time, contact_name, contact_phone, status, remark, created_at) VALUES
(3, 1, 1, '2026-03-21 14:00:00', '陈先生', '13800000002', 'CONFIRMED', '狗狗胆小，请温和处理。', '2026-03-19 19:00:00'),
(2, 2, 3, '2026-03-22 10:30:00', '李女士', '13800000001', 'PENDING', '想增加口腔检查。', '2026-03-20 09:00:00');

INSERT INTO merchant_reviews (merchant_id, user_id, score, content, created_at) VALUES
(1, 3, 5, '服务很细致，对狗狗状态也会耐心说明。', '2026-03-18 18:30:00'),
(2, 2, 4, '医生解释得很详细，体检建议也比较实用。', '2026-03-19 15:20:00');

INSERT INTO product_categories (id, name, pet_type, sort, status) VALUES
(1, '主粮', 'CAT', 1, 'ACTIVE'),
(2, '零食', 'DOG', 2, 'ACTIVE'),
(3, '清洁护理', 'ALL', 3, 'ACTIVE');

INSERT INTO products (id, category_id, name, subtitle, image_url, price, stock, pet_type, status, description) VALUES
(1, 1, '幼猫无谷主粮', '适合 2-12 月龄幼猫', 'https://example.com/product-1.jpg', 159.00, 80, 'CAT', 'ON_SALE', '高蛋白配方，适合幼猫成长阶段。'),
(2, 2, '冻干鸡肉粒', '训练奖励小零食', 'https://example.com/product-2.jpg', 49.00, 120, 'DOG', 'ON_SALE', '单一肉源，适口性好。'),
(3, 3, '宠物免洗泡沫', '日常清洁护理', 'https://example.com/product-3.jpg', 69.00, 60, 'ALL', 'ON_SALE', '适合外出后简单清洁使用。');

INSERT INTO cart_items (user_id, product_id, quantity, checked) VALUES
(2, 1, 1, 1),
(2, 3, 2, 1),
(4, 2, 1, 0);

INSERT INTO shop_orders (id, user_id, order_no, total_amount, pay_amount, status, receiver_name, receiver_phone, receiver_address, remark, created_at) VALUES
(1, 4, 'PSP20260319153000DEMO001', 49.00, 49.00, 'SHIPPED', '王小姐', '13800000003', '上海市长宁区示例路 88 号', '工作日送达', '2026-03-19 15:30:00');

INSERT INTO shop_order_items (order_id, product_id, product_name, product_image_url, unit_price, quantity, subtotal_amount) VALUES
(1, 2, '冻干鸡肉粒', 'https://example.com/product-2.jpg', 49.00, 1, 49.00);

INSERT INTO banners (id, title, image_url, link_url, status, sort, created_by) VALUES
(1, '春季养宠指南', 'https://example.com/banner-1.jpg', '/community/posts/1', 'ACTIVE', 1, 1),
(2, '领养代替购买', 'https://example.com/banner-2.jpg', '/adoption', 'ACTIVE', 2, 1);

INSERT INTO recommendations (biz_type, biz_id, slot_code, status, sort, created_by) VALUES
('post', 1, 'HOME_POST', 'ACTIVE', 1, 1),
('post', 2, 'HOME_POST', 'ACTIVE', 2, 1),
('service', 1, 'HOME_SERVICE', 'ACTIVE', 1, 1),
('service', 2, 'HOME_SERVICE', 'ACTIVE', 2, 1),
('product', 1, 'HOME_PRODUCT', 'ACTIVE', 1, 1),
('product', 3, 'HOME_PRODUCT', 'ACTIVE', 2, 1);

SET FOREIGN_KEY_CHECKS = 1;
