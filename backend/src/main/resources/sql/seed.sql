SET NAMES utf8mb4;

-- Seed data is loaded into the database selected by SPRING_DATASOURCE_URL.

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM recommendations;
DELETE FROM banners;
DELETE FROM shop_order_items;
DELETE FROM shop_orders;
DELETE FROM cart_items;
DELETE FROM products;
DELETE FROM product_categories;
DELETE FROM user_coupons;
DELETE FROM coupons;
DELETE FROM user_addresses;
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
ALTER TABLE user_addresses AUTO_INCREMENT = 1;
ALTER TABLE coupons AUTO_INCREMENT = 1;
ALTER TABLE user_coupons AUTO_INCREMENT = 1;
ALTER TABLE cart_items AUTO_INCREMENT = 1;
ALTER TABLE shop_orders AUTO_INCREMENT = 1;
ALTER TABLE shop_order_items AUTO_INCREMENT = 1;
ALTER TABLE banners AUTO_INCREMENT = 1;
ALTER TABLE recommendations AUTO_INCREMENT = 1;

INSERT INTO users (id, role, phone, nickname, avatar_url, gender, status, bio) VALUES
(1, 'ADMIN', '13900000000', '绯荤粺绠＄悊鍛?, '/static/images/avatar-admin.png', 'UNKNOWN', 'ACTIVE', '璐熻矗骞冲彴鍐呭涓庤繍钀ョ鐞?),
(2, 'USER', '13800000001', '鍥㈠瓙濡?, '/static/images/avatar-user1.png', 'FEMALE', 'ACTIVE', '涓ゅ彧鐚殑閾插睅瀹?),
(3, 'USER', '13800000002', '鏌寸姮鐮旂┒鍛?, '/static/images/avatar-user2.png', 'MALE', 'ACTIVE', '鐑埍瀹犵墿鎶ょ悊鍜岃缁?),
(4, 'USER', '13800000003', '鐚挭瑙傚療鍛?, '/static/images/avatar-user3.png', 'FEMALE', 'ACTIVE', '鍠滄璁板綍姣涘瀛愮殑鏃ュ父');

UPDATE users SET password_hash = '$2a$10$tlnMMJ2lktQPQ6wSxcD5o.AXnU.yP61/.rCjZwO3MhVWfW3NC2WwS' WHERE id = 1;
UPDATE users SET password_hash = '$2b$10$4b1fjzD6eRm7RfYBoPGxMeow/8Ay.9EH9gcet7MdU6at7Dqo16AzW' WHERE id IN (2, 3, 4);

INSERT INTO user_addresses (id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, status) VALUES
(1, 2, '鏉庡コ澹?, '13800000001', '涓婃捣甯?, '涓婃捣甯?, '娴︿笢鏂板尯', '妯辫姳璺?1234 鍙疯悓鐗╁ぇ鍘?A 搴?808 瀹?, 1, 'ACTIVE'),
(2, 2, '鏉庡コ澹?, '13800000001', '涓婃捣甯?, '涓婃捣甯?, '寰愭眹鍖?, '澶╅挜妗ヨ矾 66 鍙?5 妤?, 0, 'ACTIVE'),
(3, 3, '闄堝厛鐢?, '13800000002', '涓婃捣甯?, '涓婃捣甯?, '寰愭眹鍖?, '绀轰緥璺?66 鍙?1201 瀹?, 1, 'ACTIVE'),
(4, 4, '鐜嬪皬濮?, '13800000003', '涓婃捣甯?, '涓婃捣甯?, '闀垮畞鍖?, '绀轰緥璺?88 鍙?2 骞?301 瀹?, 1, 'ACTIVE');

INSERT INTO coupons (id, name, type, discount_amount, min_amount, start_at, end_at, status) VALUES
(1, '鏂颁汉涓撲韩鍒?, 'AMOUNT', 20.00, 100.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE'),
(2, '鐢ㄥ搧婊″噺鍒?, 'AMOUNT', 10.00, 50.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE'),
(3, '楂樺鍗曟姢鐞嗗埜', 'AMOUNT', 50.00, 299.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE');

INSERT INTO user_coupons (id, user_id, coupon_id, status) VALUES
(1, 2, 1, 'UNUSED'),
(2, 2, 2, 'UNUSED'),
(3, 3, 2, 'UNUSED'),
(4, 4, 2, 'UNUSED');

INSERT INTO messages (user_id, type, title, content, is_read) VALUES
(2, 'SYSTEM', '棰嗗吇鐢宠宸叉彁浜?, '浣犳彁浜ょ殑棰嗗吇鐢宠宸茶繘鍏ュ鏍告祦绋嬶紝璇蜂繚鎸佺數璇濈晠閫氥€?, 0),
(2, 'INTERACTION', '鏀跺埌鏂扮殑璇勮', '浣犵殑甯栧瓙銆婂辜鐚柅鑻楁椂闂磋〃鏁寸悊銆嬫敹鍒颁簡鏂拌瘎璁恒€?, 0),
(3, 'SYSTEM', '棰勭害鎻愰啋', '浣犻绾︾殑瀹犵墿娲楁姢鏈嶅姟灏嗗湪鏄庡ぉ涓嬪崍寮€濮嬶紝璇锋彁鍓嶅埌搴椼€?, 1),
(4, 'SYSTEM', '璁㈠崟宸插彂璐?, '浣犺喘涔扮殑鍐诲共闆堕宸插彂璐э紝璇风暀鎰忕墿娴佸姩鎬併€?, 0);

INSERT INTO pets (id, user_id, name, type, breed, gender, birthday, weight, avatar_url, description) VALUES
(1, 2, '鍥㈠瓙', 'CAT', '鑻辩煭钃濈尗', 'MALE', '2024-04-12', 4.20, '/static/images/pet-cat-tuanzi.png', '鎬ф牸绋冲畾锛屽枩娆㈡檼澶槼'),
(2, 2, '绯背', 'CAT', '甯冨伓鐚?, 'FEMALE', '2024-08-01', 3.50, '/static/images/pet-cat-nuomi.png', '寰堥粡浜猴紝鍠滄琚⒊姣?),
(3, 3, '闃挎煷', 'DOG', '鏌寸姮', 'MALE', '2023-11-08', 8.60, '/static/images/pet-dog-achai.png', '娲绘臣濂藉姩锛屽鍑虹簿鍔涙椇鐩?);

INSERT INTO pet_vaccines (pet_id, vaccine_name, vaccinated_at, next_due_at, remark) VALUES
(1, '鐚笁鑱?, '2025-01-10', '2026-01-10', '鍩虹鍏嶇柅宸插畬鎴?),
(2, '鐙傜姮鐤嫍', '2025-02-18', '2026-02-18', '鎺ョ鍚庣姸鎬佹甯?);

INSERT INTO pet_weights (pet_id, weight, recorded_at) VALUES
(1, 4.10, '2026-03-01 09:00:00'),
(1, 4.20, '2026-03-18 09:00:00'),
(2, 3.50, '2026-03-15 20:30:00');

INSERT INTO pet_albums (pet_id, image_url, caption) VALUES
(1, '/static/images/pet-cat-tuanzi.png', '绗竴娆℃檼澶槼'),
(2, '/static/images/pet-cat-nuomi.png', '鍗堢潯鏃堕棿');

INSERT INTO tags (id, name, type, status, sort) VALUES
(1, '鏂版墜鍏诲疇', 'community', 'ACTIVE', 1),
(2, '鐤嫍', 'community', 'ACTIVE', 2),
(3, '娲楁姢', 'community', 'ACTIVE', 3);

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at) VALUES
(1, 2, 'knowledge', '骞肩尗鐤嫍鏃堕棿琛ㄦ暣鐞?, '鏁寸悊浜嗗辜鐚父瑙佺柅鑻楁帴绉嶈妭鐐瑰拰娉ㄦ剰浜嬮」锛岄€傚悎绗竴娆″吇鐚殑鍚屽鏀惰棌銆?, '/static/images/post-cover-vaccine.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 12, 6, 2, '2026-03-18 10:00:00'),
(2, 3, 'daily', '鏌寸姮鏄ュ鎺夋瘺鎶ょ悊缁忛獙', '鏈€杩戝閲屾帀姣涙槑鏄惧澶氾紝鎴戞暣鐞嗕簡鑷繁杩欎袱鍛ㄧ敤杩囩殑姊虫瘺鍜屾竻娲佸姙娉曘€?, '/static/images/post-cover-shedding.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 8, 3, 1, '2026-03-19 09:30:00'),
(3, 4, 'help', '绗竴娆￠鍏荤尗鍜鍑嗗浠€涔?, '鍑嗗鍘婚鍏诲皬鐚紝鎯冲厛浜嗚В鐚爞銆佺尗绮€佺尗绐濆拰鐤嫍杩欎簺鍩虹浜嬮」銆?, '/static/images/post-cover-adopt.png', 'PENDING', NULL, 0, 0, 0, NULL);

INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2), (2, 1), (2, 3), (3, 1);

INSERT INTO post_comments (id, post_id, user_id, content, status, created_at) VALUES
(1, 1, 3, '杩欑瘒鏁寸悊寰楀緢娓呮锛屽凡缁忔敹钘忎簡銆?, 'NORMAL', '2026-03-18 13:20:00'),
(2, 1, 4, '鎯抽棶涓€涓嬬尗涓夎仈鍔犲己閽堢殑鏃堕棿闂撮殧鎬庝箞瀹夋帓锛?, 'NORMAL', '2026-03-18 18:00:00'),
(3, 2, 2, '鎹㈠鐪熺殑瑕佹敞鎰忕毊鑲ゆ姢鐞嗭紝鎰熻阿鍒嗕韩銆?, 'NORMAL', '2026-03-19 11:00:00');

INSERT INTO post_likes (post_id, user_id) VALUES
(1, 3), (1, 4), (2, 2);

INSERT INTO post_favorites (post_id, user_id) VALUES
(1, 3), (1, 4), (2, 2);

INSERT INTO adoption_pets (id, name, type, breed, gender, age_desc, city, health_status, personality, adoption_requirements, story, status, cover_url) VALUES
(1, '濂剁硶', 'CAT', '涓崕鐢板洯鐚?, 'FEMALE', '4涓湀', '涓婃捣', '宸插畬鎴愬熀纭€浣撴鍜岄┍铏?, '浜蹭汉娲绘臣', '闇€绋冲畾灞呬綇鐜锛屾湁灏佺獥鏉′欢', '鍦ㄥ皬鍖鸿鏁戝姪鍚庨€愭笎鎭㈠鍋ュ悍锛岀幇鍦ㄧ姸鎬佸緢濂姐€?, 'ONLINE', '/static/images/adoption-cat-naigao.png'),
(2, '璞嗗寘', 'DOG', '鏌熀', 'MALE', '1宀?, '涓婃捣', '鐤嫍榻愬叏', '鍠滄浜掑姩', '闇€鏈夐仜鐙楁椂闂达紝鎺ュ彈鍥炶', '鍘熶富浜烘惉瀹舵棤娉曠户缁収椤撅紝姝ｅ湪瀵绘壘鏂板搴€?, 'ONLINE', '/static/images/adoption-dog-doubao.png');

INSERT INTO adoption_applications (pet_id, user_id, experience_desc, living_condition_desc, contact_phone, status, review_remark, reviewed_by, reviewed_at, created_at) VALUES
(1, 2, '鏈変袱骞村吇鐚粡楠岋紝鐔熸倝鍠傝嵂鍜屾棩甯告姢鐞嗐€?, '鑷湁浣忔埧锛屽凡灏佺獥锛屽彲闀挎湡鐓ч【銆?, '13800000001', 'PENDING', NULL, NULL, NULL, '2026-03-19 20:00:00'),
(2, 4, '鏇剧粡鐓ч【杩囦腑鍨嬬姮锛岃兘鎺ュ彈璁粌瀹夋帓銆?, '瀹跺涵鎴愬憳鏀寔棰嗗吇锛岄檮杩戞湁瀹犵墿鍖婚櫌銆?, '13800000003', 'APPROVED', '璧勬枡瀹屾暣锛屾矡閫氭儏鍐佃壇濂姐€?, 1, '2026-03-19 16:00:00', '2026-03-18 15:00:00');

INSERT INTO service_categories (id, name, sort, status) VALUES
(1, '娲楁姢', 1, 'ACTIVE'),
(2, '瀵勫吇', 2, 'ACTIVE'),
(3, '浣撴', 3, 'ACTIVE');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status) VALUES
(1, '姣涘瀛愭礂鎶ゅ眿', '娴︿笢鏂板尯', '閿︾唬璺?188 鍙?, '021-55550001', '10:00-20:00', 4.8, 'ACTIVE'),
(2, '瀹夊績瀹犵墿璇婃墍', '寰愭眹鍖?, '澶╅挜妗ヨ矾 66 鍙?, '021-55550002', '09:00-18:00', 4.7, 'ACTIVE');

INSERT INTO merchant_services (id, merchant_id, category_id, name, price, duration_minutes, status) VALUES
(1, 1, 1, '鍩虹娲楁姢', 128.00, 90, 'ACTIVE'),
(2, 1, 2, '鐭湡瀵勫吇', 150.00, 1440, 'ACTIVE'),
(3, 2, 3, '骞村害浣撴', 299.00, 60, 'ACTIVE');

INSERT INTO service_bookings (user_id, merchant_id, merchant_service_id, booking_time, contact_name, contact_phone, status, remark, created_at) VALUES
(3, 1, 1, '2026-03-21 14:00:00', '闄堝厛鐢?, '13800000002', 'CONFIRMED', '鐙楃嫍鑳嗗皬锛岃娓╁拰澶勭悊銆?, '2026-03-19 19:00:00'),
(2, 2, 3, '2026-03-22 10:30:00', '鏉庡コ澹?, '13800000001', 'PENDING', '鎯冲鍔犲彛鑵旀鏌ャ€?, '2026-03-20 09:00:00');

INSERT INTO merchant_reviews (merchant_id, user_id, score, content, created_at) VALUES
(1, 3, 5, '鏈嶅姟寰堢粏鑷达紝瀵圭嫍鐙楃姸鎬佷篃浼氳€愬績璇存槑銆?, '2026-03-18 18:30:00'),
(2, 2, 4, '鍖荤敓瑙ｉ噴寰楀緢璇︾粏锛屼綋妫€寤鸿涔熸瘮杈冨疄鐢ㄣ€?, '2026-03-19 15:20:00');

INSERT INTO product_categories (id, name, pet_type, sort, status) VALUES
(1, '涓荤伯', 'CAT', 1, 'ACTIVE'),
(2, '闆堕', 'DOG', 2, 'ACTIVE'),
(3, '娓呮磥鎶ょ悊', 'ALL', 3, 'ACTIVE');

INSERT INTO products (id, category_id, name, subtitle, image_url, price, stock, pet_type, status, description) VALUES
(1, 1, '骞肩尗鏃犺胺涓荤伯', '閫傚悎 2-12 鏈堥緞骞肩尗', '/static/images/product-cat-food.png', 159.00, 80, 'CAT', 'ON_SALE', '楂樿泲鐧介厤鏂癸紝閫傚悎骞肩尗鎴愰暱闃舵銆?),
(2, 2, '鍐诲共楦¤倝绮?, '璁粌濂栧姳灏忛浂椋?, '/static/images/product-dog-treat.png', 49.00, 120, 'DOG', 'ON_SALE', '鍗曚竴鑲夋簮锛岄€傚彛鎬уソ銆?),
(3, 3, '瀹犵墿鍏嶆礂娉℃搏', '鏃ュ父娓呮磥鎶ょ悊', '/static/images/product-cleaner.png', 69.00, 60, 'ALL', 'ON_SALE', '閫傚悎澶栧嚭鍚庣畝鍗曟竻娲佷娇鐢ㄣ€?);

INSERT INTO cart_items (user_id, product_id, quantity, checked) VALUES
(2, 1, 1, 1),
(2, 3, 2, 1),
(4, 2, 1, 0);

INSERT INTO shop_orders (id, user_id, order_no, total_amount, pay_amount, status, receiver_name, receiver_phone, receiver_address, remark, created_at) VALUES
(1, 4, 'PSP20260319153000DEMO001', 49.00, 49.00, 'SHIPPED', '鐜嬪皬濮?, '13800000003', '涓婃捣甯傞暱瀹佸尯绀轰緥璺?88 鍙?, '宸ヤ綔鏃ラ€佽揪', '2026-03-19 15:30:00');

INSERT INTO shop_order_items (order_id, product_id, product_name, product_image_url, unit_price, quantity, subtotal_amount) VALUES
(1, 2, '鍐诲共楦¤倝绮?, '/static/images/product-dog-treat.png', 49.00, 1, 49.00);

INSERT INTO banners (id, title, image_url, link_url, status, sort, created_by) VALUES
(1, '鏄ュ鍏诲疇鎸囧崡', '/static/images/banner-spring.png', '/community/posts/1', 'ACTIVE', 1, 1),
(2, '棰嗗吇浠ｆ浛璐拱', '/static/images/banner-adoption.png', '/adoption', 'ACTIVE', 2, 1);

INSERT INTO recommendations (biz_type, biz_id, slot_code, status, sort, created_by) VALUES
('post', 1, 'HOME_POST', 'ACTIVE', 1, 1),
('post', 2, 'HOME_POST', 'ACTIVE', 2, 1),
('service', 1, 'HOME_SERVICE', 'ACTIVE', 1, 1),
('service', 2, 'HOME_SERVICE', 'ACTIVE', 2, 1),
('product', 1, 'HOME_PRODUCT', 'ACTIVE', 1, 1),
('product', 3, 'HOME_PRODUCT', 'ACTIVE', 2, 1);

-- Additional demo data for frontend/backend integration testing.
INSERT INTO user_addresses (id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, status) VALUES
(5, 5, '鍛ㄥ厛鐢?, '13800000004', '涓婃捣甯?, '涓婃捣甯?, '闂佃鍖?, '涓冭帢璺?299 鍙烽槼鍏夎姳鍥?12 鏍?601 瀹?, 1, 'ACTIVE'),
(6, 6, '璧靛コ澹?, '13800000005', '涓婃捣甯?, '涓婃捣甯?, '娴︿笢鏂板尯', '寮犳睙璺?88 鍙峰疇鍙嬪叕瀵?3 鏍?210 瀹?, 1, 'ACTIVE'),
(7, 7, '瀛欏コ澹?, '13800000006', '娴欐睙鐪?, '鏉窞甯?, '瑗挎箹鍖?, '鏂囦笁璺?168 鍙?9 妤?, 1, 'ACTIVE');

INSERT INTO user_coupons (id, user_id, coupon_id, status) VALUES
(5, 5, 1, 'UNUSED'),
(6, 5, 2, 'UNUSED'),
(7, 6, 1, 'UNUSED'),
(8, 7, 2, 'UNUSED'),
(9, 7, 3, 'UNUSED');

INSERT INTO users (id, role, phone, password_hash, nickname, avatar_url, gender, status, bio) VALUES
(5, 'USER', '13800000004', '$2b$10$4b1fjzD6eRm7RfYBoPGxMeow/8Ay.9EH9gcet7MdU6at7Dqo16AzW', '甯冧竵鐖哥埜', '/static/images/avatar-user4.png', 'MALE', 'ACTIVE', '鍏虫敞鐙楃嫍璁粌鍜屾棩甯搁櫔浼?),
(6, 'USER', '13800000005', '$2b$10$4b1fjzD6eRm7RfYBoPGxMeow/8Ay.9EH9gcet7MdU6at7Dqo16AzW', '灏忔楗插吇鍛?, '/static/images/avatar-user5.png', 'FEMALE', 'ACTIVE', '璁板綍娴佹氮鐚晳鍔╁拰棰嗗吇鍥炶'),
(7, 'USER', '13800000006', '$2b$10$4b1fjzD6eRm7RfYBoPGxMeow/8Ay.9EH9gcet7MdU6at7Dqo16AzW', '璞嗚眴瀹?, '/static/images/avatar-user6.png', 'UNKNOWN', 'ACTIVE', '绗竴娆″吇瀹犵殑鏂版墜瀹跺涵'),
(8, 'USER', '13800000007', '$2b$10$4b1fjzD6eRm7RfYBoPGxMeow/8Ay.9EH9gcet7MdU6at7Dqo16AzW', '鏆傜鐢ㄦ埛', '/static/images/avatar-user6.png', 'UNKNOWN', 'DISABLED', '鐢ㄤ簬鍚庡彴璐﹀彿鐘舵€佽仈璋?);

INSERT INTO messages (user_id, type, title, content, is_read) VALUES
(2, 'ORDER', '璁㈠崟宸茬鏀?, '浣犵殑涓荤伯璁㈠崟宸插畬鎴愮鏀讹紝娆㈣繋璇勪环鏈璐墿浣撻獙銆?, 0),
(3, 'BOOKING', '棰勭害鐘舵€佸凡鏇存柊', '浣犻绾︾殑鍩虹娲楁姢鏈嶅姟宸茬‘璁わ紝璇锋寜鏃跺埌搴椼€?, 0),
(5, 'SYSTEM', '瀹屽杽瀹犵墿妗ｆ鎻愰啋', '琛ュ厖鐢熸棩銆佷綋閲嶅拰鐤嫍璁板綍鍚庯紝鍙互鑾峰緱鏇村畬鏁寸殑鍋ュ悍鏃堕棿绾裤€?, 1),
(6, 'ADOPTION', '棰嗗吇鍥炶鎻愰啋', '璇峰湪鏈懆鍐呮彁浜ら鍏诲洖璁跨収鐗囷紝甯姪骞冲彴纭瀹犵墿閫傚簲鎯呭喌銆?, 0);

INSERT INTO pets (id, user_id, name, type, breed, gender, birthday, weight, avatar_url, description) VALUES
(4, 5, '甯冧竵', 'DOG', '鏌熀', 'MALE', '2023-06-18', 11.20, '/static/images/pet-dog-buding.png', '绮惧姏鏃虹洓锛屾鍦ㄨ繘琛屽彫鍥炶缁?),
(5, 5, '鍙箰', 'DOG', '鎷夊竷鎷夊', 'FEMALE', '2022-09-02', 24.60, '/static/images/pet-dog-kele.png', '娓╅『浜蹭汉锛岄€傚悎鏈嶅姟棰勭害鑱旇皟'),
(6, 6, '灏忔', 'CAT', '涓崕鐢板洯鐚?, 'MALE', '2024-01-15', 4.00, '/static/images/pet-cat-xiaoju.png', '鏁戝姪鍚庢仮澶嶈壇濂斤紝宸插畬鎴愰┍铏?),
(7, 7, '璞嗚眴', 'DOG', '姣旂唺', 'FEMALE', '2025-02-05', 3.30, '/static/images/pet-dog-doudou.png', '鏂版墜瀹跺涵瀹犵墿锛岄€傚悎妗ｆ鏂板娴嬭瘯');

INSERT INTO pet_vaccines (pet_id, vaccine_name, vaccinated_at, next_due_at, remark) VALUES
(4, '鐙傜姮鐤嫍', '2025-10-12', '2026-10-12', '骞村害鍔犲己閽?),
(5, '鐘叚鑱?, '2025-11-06', '2026-11-06', '浣撴鏃跺悓姝ユ帴绉?),
(6, '鐚笁鑱?, '2025-12-01', '2026-12-01', '鏁戝姪绔欐帴绉嶈褰?),
(7, '鐘洓鑱?, '2026-03-08', '2026-04-08', '骞肩姮绗簩閽堝緟瀹屾垚');

INSERT INTO pet_weights (pet_id, weight, recorded_at) VALUES
(4, 10.80, '2026-03-01 08:20:00'),
(4, 11.20, '2026-03-20 08:30:00'),
(5, 24.10, '2026-03-05 19:10:00'),
(5, 24.60, '2026-03-22 19:00:00'),
(6, 3.80, '2026-03-12 21:30:00'),
(6, 4.00, '2026-03-24 21:00:00'),
(7, 3.10, '2026-03-17 09:00:00');

INSERT INTO pet_albums (pet_id, image_url, caption) VALUES
(4, '/static/images/pet-dog-buding.png', '绗竴娆″畬鎴愬潗涓嬭缁?),
(5, '/static/images/pet-dog-kele.png', '娲楁姢鍚庤摤鏉剧殑涓€澶?),
(6, '/static/images/pet-cat-xiaoju.png', '鏁戝姪鍚庣殑绗竴涓槬澶?),
(7, '/static/images/pet-dog-doudou.png', '鏂板閫傚簲璁板綍');

INSERT INTO tags (id, name, type, status, sort) VALUES
(4, '楗', 'community', 'ACTIVE', 4),
(5, '璁粌', 'community', 'ACTIVE', 5),
(6, '棰嗗吇鍥炶', 'community', 'ACTIVE', 6),
(7, '鍋ュ悍璁板綍', 'community', 'ACTIVE', 7);

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at) VALUES
(4, 5, 'training', '鏌熀鍙洖璁粌涓€鍛ㄨ褰?, '姣忓ぉ鍥哄畾涓ゆ鐭椂闂磋缁冿紝姣旈暱鏃堕棿閲嶅鏇村鏄撲繚鎸佺嫍鐙楁敞鎰忓姏銆?, '/static/images/post-cover-training.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 15, 7, 3, '2026-03-20 10:20:00'),
(5, 6, 'rescue', '灏忔鏁戝姪鍚庣殑閫傚簲杩囩▼', '浠庤翰鍦ㄧ焊绠卞埌涓诲姩韫墜锛屽ぇ姒傜敤浜嗕袱鍛ㄦ椂闂达紝绋冲畾鐜寰堥噸瑕併€?, '/static/images/post-cover-rescue.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 21, 12, 4, '2026-03-21 16:40:00'),
(6, 7, 'daily', '绗竴娆＄粰姣旂唺鍓寚鐢茬殑鍑嗗娓呭崟', '鍏堢啛鎮夎剼鎺岃Е纰帮紝鍐嶅噯澶囨琛€绮夊拰濂栧姳闆堕锛岃繃绋嬩笉瑕佺潃鎬ャ€?, '/static/images/post-cover-nail.png', 'PENDING', NULL, 0, 0, 0, NULL),
(7, 2, 'knowledge', '鐚挭鎹㈢伯杩囨浮姣斾緥鎬庝箞瀹夋帓', '寤鸿鐢?7 鍒?10 澶╅€愭鏇挎崲锛岃瀵熻蒋渚裤€佸憰鍚愬拰椋熸鍙樺寲銆?, '/static/images/post-cover-food.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 18, 9, 2, '2026-03-22 09:15:00'),
(8, 3, 'help', '鐙楃嫍澶栧嚭鍚庤剼鍨彂绾㈡€庝箞鍔?, '鏁ｆ鍥炲鍚庡彂鐜拌剼鍨亸绾紝鎯崇‘璁ゆ槸鍚﹂渶瑕佺珛鍒诲氨鍖汇€?, '/static/images/post-cover-paw.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 6, 2, 2, '2026-03-22 20:10:00'),
(9, 5, 'daily', '瀵勫吇鍓嶉渶瑕佸拰鍟嗗纭鍝簺缁嗚妭', '鍖呮嫭鍠傞娆℃暟銆侀仜鐙楅鐜囥€佹槸鍚﹀崟鐙殧闂翠互鍙婄獊鍙戞儏鍐佃仈绯绘祦绋嬨€?, '/static/images/post-cover-boarding.png', 'APPROVED', '鍐呭绗﹀悎绀惧尯瑙勮寖', 10, 5, 1, '2026-03-23 12:30:00'),
(10, 6, 'knowledge', '棰嗗吇鍥炶鐓х墖鎬庝箞鎷嶆洿娓呮', '寤鸿鎷嶆憚姝ｈ劯銆佸叏韬€侀鐩嗗拰娲诲姩鍖哄煙锛屼究浜庡钩鍙颁簡瑙ｉ€傚簲鎯呭喌銆?, '/static/images/post-cover-followup.png', 'REJECTED', '鍥剧墖淇℃伅涓嶈冻锛岃琛ュ厖鏇存竻鏅扮殑鐜鐓х墖', 0, 0, 0, NULL);

INSERT INTO post_tags (post_id, tag_id) VALUES
(4, 5), (4, 1), (5, 6), (5, 1), (6, 1), (7, 4), (7, 7), (8, 7), (9, 3), (10, 6);

INSERT INTO post_comments (id, post_id, user_id, content, status, created_at) VALUES
(4, 4, 2, '鐭椂闂撮珮棰戣缁冭繖涓柟娉曞緢鏈夌敤锛屾垜瀹剁尗涔熼€傚悎銆?, 'NORMAL', '2026-03-20 11:00:00'),
(5, 4, 3, '鍙洖璁粌鍙互鍔犱笂鍥哄畾鍙ｄ护锛屾晥鏋滀細鏇寸ǔ瀹氥€?, 'NORMAL', '2026-03-20 12:10:00'),
(6, 4, 6, '甯冧竵鐪嬭捣鏉ョ姸鎬佸緢濂斤紝鏈熷緟鍚庣画璁板綍銆?, 'NORMAL', '2026-03-20 14:25:00'),
(7, 5, 2, '鏁戝姪鐚€傚簲鏈熺‘瀹為渶瑕佽€愬績锛岀幆澧冪ǔ瀹氬緢鍏抽敭銆?, 'NORMAL', '2026-03-21 17:20:00'),
(8, 5, 5, '灏忔鐜板湪鑳戒富鍔ㄤ簰鍔ㄥ緢妫掋€?, 'NORMAL', '2026-03-21 18:05:00'),
(9, 5, 7, '鎯宠鏁欐晳鍔╁悗椹辫櫕棰戠巼鎬庝箞瀹夋帓锛?, 'NORMAL', '2026-03-21 19:40:00'),
(10, 5, 3, '鍙互鎶婂洖璁跨収鐗囦篃鏁寸悊鎴愬笘瀛愶紝鏂逛究鏂版墜鍙傝€冦€?, 'NORMAL', '2026-03-21 21:10:00'),
(11, 7, 5, '鎴戜竴鑸寜 25%銆?0%銆?5% 鐨勬瘮渚嬮€愭杩囨浮銆?, 'NORMAL', '2026-03-22 10:00:00'),
(12, 7, 6, '杞究鏃跺缓璁厛鍋滃湪褰撳墠姣斾緥瑙傚療涓ゅぉ銆?, 'NORMAL', '2026-03-22 11:10:00'),
(13, 8, 2, '濡傛灉鏈夌牬鐨垨鎸佺画鑸斿挰锛屽缓璁敖蹇幓鍖婚櫌鐪嬩竴涓嬨€?, 'NORMAL', '2026-03-22 21:00:00'),
(14, 8, 5, '鍙互鍏堟鏌ユ槸鍚︽湁寮傜墿鎴栫儷浼ょ棔杩广€?, 'NORMAL', '2026-03-22 21:30:00'),
(15, 9, 3, '杩樿纭澶滈棿鏄惁鏈変汉鍊煎畧銆?, 'NORMAL', '2026-03-23 13:05:00');

INSERT INTO post_likes (post_id, user_id) VALUES
(4, 2), (4, 3), (4, 6), (5, 2), (5, 3), (5, 5), (5, 7), (7, 3), (7, 5), (8, 2), (9, 2), (9, 6);

INSERT INTO post_favorites (post_id, user_id) VALUES
(4, 2), (4, 6), (5, 2), (5, 5), (5, 7), (7, 3), (7, 5), (8, 5), (9, 2), (9, 6);

INSERT INTO adoption_pets (id, name, type, breed, gender, age_desc, city, health_status, personality, adoption_requirements, story, status, cover_url) VALUES
(3, '鑺卞嵎', 'CAT', '鐙歌姳鐚?, 'MALE', '8涓湀', '涓婃捣', '宸茬粷鑲诧紝鐤嫍榻愬叏', '璀︽儠浣嗕翰浜?, '闇€瑕佹湁鍏荤尗缁忛獙锛屾帴鍙楀畾鏈熷洖璁?, '鍦ㄥ洯鍖鸿鏁戝姪锛岄€傚悎瀹夐潤瀹跺涵銆?, 'ONLINE', '/static/images/adoption-cat-huajuan.png'),
(4, '鏍楀瓙', 'DOG', '娉拌开', 'FEMALE', '2宀?, '鏉窞', '浣撴姝ｅ父锛屽凡椹辫櫕', '娲绘臣榛忎汉', '闇€瑕佹瘡澶╅仜鐙楋紝瀹朵汉鍏卞悓璁ゅ彲棰嗗吇', '鍘熶富浜哄洜宸ヤ綔鍙樺姩濮旀墭骞冲彴閲嶆柊瀵绘壘瀹跺涵銆?, 'ONLINE', '/static/images/adoption-dog-lizi.png'),
(5, '濂剁洊', 'CAT', '甯冨伓娣疯', 'FEMALE', '1宀佸崐', '鑻忓窞', '杞诲井杞究鎭㈠涓?, '娓╅『瀹夐潤', '闇€鎺ュ彈鍚庣画鑲犺儍璋冪悊寤鸿', '鏁戝姪鍚庢仮澶嶄腑锛岄€傚悎鑰愬績鐓ч【銆?, 'OFFLINE', '/static/images/adoption-cat-naigai.png'),
(6, '濂ュ埄濂?, 'DOG', '杈圭墽', 'MALE', '3宀?, '涓婃捣', '鐤嫍榻愬叏', '鑱槑锛岄渶瑕佽繍鍔ㄩ噺', '闇€瑕佹湁璁姮缁忛獙鍜岀ǔ瀹氭埛澶栨椿鍔ㄦ椂闂?, '鍓嶅搴棤娉曟弧瓒宠繍鍔ㄩ渶姹傦紝鐜板鎵炬洿鍚堥€傜幆澧冦€?, 'ONLINE', '/static/images/adoption-dog-aoliao.png');

INSERT INTO adoption_applications (pet_id, user_id, experience_desc, living_condition_desc, contact_phone, status, review_remark, reviewed_by, reviewed_at, created_at) VALUES
(3, 5, '鏈夊骞村吇鐙楃粡楠岋紝涔熺収椤捐繃鏈嬪弸鐨勭尗銆?, '鑷湁浣忔埧锛屽浜烘敮鎸侊紝宸插噯澶囬殧绂绘埧闂淬€?, '13800000004', 'PENDING', NULL, NULL, NULL, '2026-03-21 10:00:00'),
(4, 2, '鐩墠鏈変袱鍙尗锛岀涓€娆＄敵璇烽鍏荤嫍鐙椼€?, '瀹跺涵绌洪棿瓒冲锛屼絾闇€瑕佽繘涓€姝ョ‘璁ら仜鐙楀畨鎺掋€?, '13800000001', 'REJECTED', '鐢宠浜轰笌瀹犵墿闇€姹傚尮閰嶅害涓嶈冻锛屽缓璁ˉ鍏呴仜鐙楀畨鎺掑悗鍐嶇敵璇枫€?, 1, '2026-03-22 14:00:00', '2026-03-21 13:40:00'),
(6, 3, '鐔熸倝杈圭墽璁粌锛屾湁鍥哄畾鎴峰杩愬姩鏃堕棿銆?, '杩戦儕浣忔埧锛岄檮杩戞湁瀹犵墿鍙嬪ソ鍏洯銆?, '13800000002', 'PENDING', NULL, NULL, NULL, '2026-03-23 18:30:00');

INSERT INTO service_categories (id, name, sort, status) VALUES
(4, '璁粌', 4, 'ACTIVE'),
(5, '涓婇棬鎶ょ悊', 5, 'ACTIVE');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status) VALUES
(3, '鐖埅璁粌钀?, '闂佃鍖?, '涓冭帢璺?299 鍙?, '021-55550003', '09:30-19:30', 4.6, 'ACTIVE'),
(4, '鏆栫獫瀵勫吇涓績', '闀垮畞鍖?, '铏规ˉ璺?520 鍙?, '021-55550004', '08:00-21:00', 4.9, 'ACTIVE'),
(5, '钀屽疇涓婇棬鎶ょ悊', '娴︿笢鏂板尯', '寮犳睙璺?88 鍙?, '021-55550005', '10:00-18:00', 4.5, 'DISABLED');

INSERT INTO merchant_services (id, merchant_id, category_id, name, price, duration_minutes, status) VALUES
(4, 3, 4, '骞肩姮鍩虹璁粌', 199.00, 60, 'ACTIVE'),
(5, 3, 4, '琛屼负绾犳鍜ㄨ', 299.00, 90, 'ACTIVE'),
(6, 4, 2, '鍗曠姮璞崕瀵勫吇', 220.00, 1440, 'ACTIVE'),
(7, 4, 3, '鍏ラ┗鍓嶅仴搴锋鏌?, 99.00, 30, 'ACTIVE'),
(8, 5, 5, '涓婇棬鍓寚鐢?, 79.00, 30, 'DISABLED');

INSERT INTO service_bookings (user_id, merchant_id, merchant_service_id, booking_time, contact_name, contact_phone, status, remark, created_at) VALUES
(5, 3, 4, '2026-03-24 15:00:00', '鍛ㄥ厛鐢?, '13800000004', 'PENDING', '鏌熀鍙洖璁粌鍜ㄨ', '2026-03-21 09:00:00'),
(6, 4, 6, '2026-03-25 09:30:00', '璧靛コ澹?, '13800000005', 'CONFIRMED', '闇€瑕佸崟鐙殧闂?, '2026-03-21 11:30:00'),
(7, 1, 1, '2026-03-26 13:00:00', '瀛欏コ澹?, '13800000006', 'CANCELLED', '鐢ㄦ埛涓存椂鍙栨秷', '2026-03-20 16:20:00'),
(2, 3, 5, '2026-03-27 10:00:00', '鏉庡コ澹?, '13800000001', 'COMPLETED', '琛屼负鍜ㄨ宸插畬鎴?, '2026-03-18 12:00:00');

INSERT INTO merchant_reviews (merchant_id, user_id, score, content, created_at) VALUES
(3, 5, 5, '璁粌甯堜細鍏堣瀵熺嫍鐙楃姸鎬侊紝鍐嶇粰鍑哄搴缁冭鍒掋€?, '2026-03-22 18:00:00'),
(4, 6, 5, '瀵勫吇鐜骞插噣锛岀収鐗囧弽棣堝緢鍙婃椂銆?, '2026-03-23 20:00:00'),
(1, 7, 4, '绗竴娆℃礂鎶ゆ暣浣撻『鍒╋紝绛夊緟鍖轰篃姣旇緝鑸掗€傘€?, '2026-03-24 14:20:00');

INSERT INTO product_categories (id, name, pet_type, sort, status) VALUES
(4, '鐜╁叿', 'ALL', 4, 'ACTIVE'),
(5, '鍑鸿', 'DOG', 5, 'ACTIVE'),
(6, '鍋ュ悍鎶ょ悊', 'ALL', 6, 'ACTIVE');

INSERT INTO products (id, category_id, name, subtitle, image_url, price, stock, pet_type, status, description) VALUES
(4, 4, '鑰愬挰姗¤兌鐞?, '閫傚悎涓皬鍨嬬姮浜掑姩璁粌', '/static/images/product-toy-ball.png', 39.00, 150, 'DOG', 'ON_SALE', '寮规€ф潗璐紝閫傚悎鏃ュ父浜掑姩鍜屽彫鍥炶缁冦€?),
(5, 4, '閫楃尗缇芥瘺妫?, '浣庢晱缇芥瘺鏇挎崲澶?, '/static/images/product-feather.png', 29.00, 200, 'CAT', 'ON_SALE', '鎻愬崌鐚挭杩愬姩閲忥紝寤鸿鍦ㄧ湅鎶や笅浣跨敤銆?),
(6, 5, '瀹犵墿澶栧嚭鑳歌儗', '鍙嶅厜缁囧甫锛屽闂存洿閱掔洰', '/static/images/product-harness.png', 89.00, 90, 'DOG', 'ON_SALE', '閫傚悎鏃ュ父鏁ｆ鍜岀煭閫斿嚭琛屻€?),
(7, 6, '瀹犵墿鐩婄敓鑿?, '鑲犺儍璋冪悊鏃ュ父琛ュ厖', '/static/images/product-probiotic.png', 99.00, 70, 'ALL', 'ON_SALE', '鎹㈢伯鏈熷拰杞究鎭㈠鏈熷彲鎸夎鏄庤ˉ鍏呫€?),
(8, 3, '鐚爞闄よ嚟鍠烽浘', '娓╁拰姘斿懗鎺у埗', '/static/images/product-cleaner.png', 45.00, 0, 'CAT', 'OFF_SHELF', '鐢ㄤ簬鍚庡彴涓婁笅鏋跺拰搴撳瓨鑱旇皟銆?);

INSERT INTO cart_items (user_id, product_id, quantity, checked) VALUES
(3, 4, 1, 1),
(3, 7, 2, 1),
(5, 4, 2, 1),
(5, 6, 1, 0),
(6, 5, 3, 1),
(7, 7, 1, 1);

INSERT INTO shop_orders (id, user_id, order_no, total_amount, pay_amount, status, receiver_name, receiver_phone, receiver_address, remark, created_at) VALUES
(2, 2, 'PSP20260320101000DEMO002', 258.00, 258.00, 'PENDING', '鏉庡コ澹?, '13800000001', '涓婃捣甯傛郸涓滄柊鍖虹ず渚嬭矾 188 鍙?, '璇锋斁闂ㄥ崼', '2026-03-20 10:10:00'),
(3, 3, 'PSP20260321150000DEMO003', 237.00, 237.00, 'PAID', '闄堝厛鐢?, '13800000002', '涓婃捣甯傚緪姹囧尯绀轰緥璺?66 鍙?, '鍛ㄦ湯閰嶉€?, '2026-03-21 15:00:00'),
(4, 5, 'PSP20260322183000DEMO004', 167.00, 167.00, 'CANCELLED', '鍛ㄥ厛鐢?, '13800000004', '涓婃捣甯傞椀琛屽尯绀轰緥璺?299 鍙?, '鐢ㄦ埛鍙栨秷', '2026-03-22 18:30:00');

INSERT INTO shop_order_items (order_id, product_id, product_name, product_image_url, unit_price, quantity, subtotal_amount) VALUES
(2, 1, '骞肩尗鏃犺胺涓荤伯', '/static/images/product-cat-food.png', 159.00, 1, 159.00),
(2, 7, '瀹犵墿鐩婄敓鑿?, '/static/images/product-probiotic.png', 99.00, 1, 99.00),
(3, 4, '鑰愬挰姗¤兌鐞?, '/static/images/product-toy-ball.png', 39.00, 1, 39.00),
(3, 7, '瀹犵墿鐩婄敓鑿?, '/static/images/product-probiotic.png', 99.00, 2, 198.00),
(4, 6, '瀹犵墿澶栧嚭鑳歌儗', '/static/images/product-harness.png', 89.00, 1, 89.00),
(4, 5, '閫楃尗缇芥瘺妫?, '/static/images/product-feather.png', 29.00, 1, 29.00),
(4, 2, '鍐诲共楦¤倝绮?, '/static/images/product-dog-treat.png', 49.00, 1, 49.00);

INSERT INTO banners (id, title, image_url, link_url, status, sort, created_by) VALUES
(3, '璁粌钀ヤ綋楠岃', '/static/images/banner-training.png', '/services/merchant/3', 'ACTIVE', 3, 1),
(4, '鑲犺儍鎶ょ悊涓撳尯', '/static/images/banner-health.png', '/shop', 'DISABLED', 4, 1);

INSERT INTO recommendations (biz_type, biz_id, slot_code, status, sort, created_by) VALUES
('post', 5, 'HOME_POST', 'ACTIVE', 3, 1),
('post', 7, 'HOME_POST', 'ACTIVE', 4, 1),
('service', 3, 'HOME_SERVICE', 'ACTIVE', 3, 1),
('service', 4, 'HOME_SERVICE', 'ACTIVE', 4, 1),
('product', 4, 'HOME_PRODUCT', 'ACTIVE', 3, 1),
('product', 7, 'HOME_PRODUCT', 'ACTIVE', 4, 1);

SET FOREIGN_KEY_CHECKS = 1;
