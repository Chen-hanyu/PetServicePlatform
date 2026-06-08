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
(1, 'ADMIN', '13900000000', '系统管理员', '/static/images/avatar-admin.png', 'UNKNOWN', 'ACTIVE', '负责平台内容与运营管理'),
(2, 'USER', '13800000001', '团子妈', '/static/images/avatar-user1.png', 'FEMALE', 'ACTIVE', '两只猫的铲屎官'),
(3, 'USER', '13800000002', '柴犬研究员', '/static/images/avatar-user2.png', 'MALE', 'ACTIVE', '热爱宠物护理和训练'),
(4, 'USER', '13800000003', '猫咖观察员', '/static/images/avatar-user3.png', 'FEMALE', 'ACTIVE', '喜欢记录毛孩子的日常'),
(5, 'USER', '13800000004', '布丁爸爸', '/static/images/avatar-user4.png', 'MALE', 'ACTIVE', '关注狗狗训练和日常陪伴'),
(6, 'USER', '13800000005', '小橘饲养员', '/static/images/avatar-user5.png', 'FEMALE', 'ACTIVE', '记录流浪猫救助和领养回访'),
(7, 'USER', '13800000006', '豆豆家', '/static/images/avatar-user6.png', 'UNKNOWN', 'ACTIVE', '第一次养宠的新手家庭'),
(8, 'USER', '13800000007', '暂停用户', '/static/images/avatar-user6.png', 'UNKNOWN', 'DISABLED', '用于后台账号状态联调');

UPDATE users SET password_hash = '$2a$10$tlnMMJ2lktQPQ6wSxcD5o.AXnU.yP61/.rCjZwO3MhVWfW3NC2WwS' WHERE id = 1;
UPDATE users SET password_hash = '$2b$10$4b1fjzD6eRm7RfYBoPGxMeow/8Ay.9EH9gcet7MdU6at7Dqo16AzW' WHERE id IN (2, 3, 4, 5, 6, 7, 8);

INSERT INTO user_addresses (id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, status) VALUES
(1, 2, '李女士', '13800000001', '上海市', '上海市', '浦东新区', '樱花路 1234 号萌物大厦 A 座 808 室', 1, 'ACTIVE'),
(2, 2, '李女士', '13800000001', '上海市', '上海市', '徐汇区', '天钥桥路 66 号 5 楼', 0, 'ACTIVE'),
(3, 3, '陈先生', '13800000002', '上海市', '上海市', '徐汇区', '示例路 66 号 1201 室', 1, 'ACTIVE'),
(4, 4, '王小姐', '13800000003', '上海市', '上海市', '长宁区', '示例路 88 号 2 幢 301 室', 1, 'ACTIVE'),
(5, 5, '周先生', '13800000004', '上海市', '上海市', '闵行区', '七莘路 299 号阳光花园 12 栋 601 室', 1, 'ACTIVE'),
(6, 6, '赵女士', '13800000005', '上海市', '上海市', '浦东新区', '张江路 88 号宠友公寓 3 栋 210 室', 1, 'ACTIVE'),
(7, 7, '孙女士', '13800000006', '浙江省', '杭州市', '西湖区', '文三路 168 号 9 楼', 1, 'ACTIVE');

INSERT INTO coupons (id, name, type, discount_amount, min_amount, start_at, end_at, status) VALUES
(1, '新人专享券', 'AMOUNT', 20.00, 100.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE'),
(2, '用品满减券', 'AMOUNT', 10.00, 50.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE'),
(3, '高客单护理券', 'AMOUNT', 50.00, 299.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE');

INSERT INTO user_coupons (id, user_id, coupon_id, status) VALUES
(1, 2, 1, 'UNUSED'),
(2, 2, 2, 'UNUSED'),
(3, 3, 2, 'UNUSED'),
(4, 4, 2, 'UNUSED'),
(5, 5, 1, 'UNUSED'),
(6, 5, 2, 'UNUSED'),
(7, 6, 1, 'UNUSED'),
(8, 7, 2, 'UNUSED'),
(9, 7, 3, 'UNUSED');

INSERT INTO messages (user_id, type, title, content, is_read) VALUES
(2, 'SYSTEM', '领养申请已提交', '你提交的领养申请已进入审核流程，请保持电话畅通。', 0),
(2, 'INTERACTION', '收到新的评论', '你的帖子收到了一条新的评论。', 0),
(3, 'SYSTEM', '预约提醒', '你预约的宠物洗护服务将在明天下午开始，请提前到店。', 1),
(4, 'ORDER', '订单已发货', '你购买的冻干零食已发货，请留意物流动态。', 0),
(5, 'SYSTEM', '完善宠物档案提醒', '补充生日、体重和疫苗记录后，可以获得更完整的健康时间线。', 1),
(6, 'ADOPTION', '领养回访提醒', '请在本周内提交领养回访照片，帮助平台确认宠物适应情况。', 0);

INSERT INTO pets (id, user_id, name, type, breed, gender, birthday, weight, avatar_url, description) VALUES
(1, 2, '团子', 'CAT', '英短蓝猫', 'MALE', '2024-04-12', 4.20, '/static/images/pet-cat-tuanzi.png', '性格稳定，喜欢晒太阳'),
(2, 2, '糯米', 'CAT', '布偶猫', 'FEMALE', '2024-08-01', 3.50, '/static/images/pet-cat-nuomi.png', '很黏人，喜欢被梳毛'),
(3, 3, '阿柴', 'DOG', '柴犬', 'MALE', '2023-11-08', 8.60, '/static/images/pet-dog-achai.png', '活泼好动，外出精力旺盛'),
(4, 5, '布丁', 'DOG', '柯基', 'MALE', '2023-06-18', 11.20, '/static/images/pet-dog-buding.png', '精力旺盛，正在进行召回训练'),
(5, 5, '可乐', 'DOG', '拉布拉多', 'FEMALE', '2022-09-02', 24.60, '/static/images/pet-dog-kele.png', '温顺亲人，适合服务预约联调'),
(6, 6, '小橘', 'CAT', '中华田园猫', 'MALE', '2024-01-15', 4.00, '/static/images/pet-cat-xiaoju.png', '救助后恢复良好，已完成驱虫'),
(7, 7, '豆豆', 'DOG', '比熊', 'FEMALE', '2025-02-05', 3.30, '/static/images/pet-dog-doudou.png', '新手家庭宠物，适合档案新增测试');

INSERT INTO pet_vaccines (pet_id, vaccine_name, vaccinated_at, next_due_at, remark) VALUES
(1, '猫三联', '2025-01-10', '2026-01-10', '基础免疫已完成'),
(2, '狂犬疫苗', '2025-02-18', '2026-02-18', '接种后状态正常'),
(4, '狂犬疫苗', '2025-10-12', '2026-10-12', '年度加强针'),
(5, '犬六联', '2025-11-06', '2026-11-06', '体检时同步接种'),
(6, '猫三联', '2025-12-01', '2026-12-01', '救助站接种记录'),
(7, '犬四联', '2026-03-08', '2026-04-08', '幼犬第二针待完成');

INSERT INTO pet_weights (pet_id, weight, recorded_at) VALUES
(1, 4.10, '2026-03-01 09:00:00'),
(1, 4.20, '2026-03-18 09:00:00'),
(2, 3.50, '2026-03-15 20:30:00'),
(4, 10.80, '2026-03-01 08:20:00'),
(4, 11.20, '2026-03-20 08:30:00'),
(5, 24.10, '2026-03-05 19:10:00'),
(5, 24.60, '2026-03-22 19:00:00'),
(6, 3.80, '2026-03-12 21:30:00'),
(6, 4.00, '2026-03-24 21:00:00'),
(7, 3.10, '2026-03-17 09:00:00');

INSERT INTO pet_albums (pet_id, image_url, caption) VALUES
(1, '/static/images/pet-cat-tuanzi.png', '第一次晒太阳'),
(2, '/static/images/pet-cat-nuomi.png', '午睡时间'),
(4, '/static/images/pet-dog-buding.png', '第一次完成坐下训练'),
(5, '/static/images/pet-dog-kele.png', '洗护后蓬松的一天'),
(6, '/static/images/pet-cat-xiaoju.png', '救助后的第一个春天'),
(7, '/static/images/pet-dog-doudou.png', '新家适应记录');

INSERT INTO tags (id, name, type, status, sort) VALUES
(1, '新手养宠', 'community', 'ACTIVE', 1),
(2, '疫苗', 'community', 'ACTIVE', 2),
(3, '洗护', 'community', 'ACTIVE', 3),
(4, '饮食', 'community', 'ACTIVE', 4),
(5, '训练', 'community', 'ACTIVE', 5),
(6, '领养回访', 'community', 'ACTIVE', 6),
(7, '健康记录', 'community', 'ACTIVE', 7);

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at) VALUES
(1, 2, 'knowledge', '幼猫疫苗时间表整理', '整理了幼猫常见疫苗接种节点和注意事项，适合第一次养猫的同学收藏。', '/static/images/post-cover-vaccine.png', 'APPROVED', '内容符合社区规范', 12, 6, 2, '2026-03-18 10:00:00'),
(2, 3, 'daily', '柴犬春季掉毛护理经验', '最近家里掉毛明显增多，我整理了这两周用过的梳毛和清洁办法。', '/static/images/post-cover-shedding.png', 'APPROVED', '内容符合社区规范', 8, 3, 1, '2026-03-19 09:30:00'),
(3, 4, 'help', '第一次领养猫咪要准备什么', '准备去领养小猫，想先了解猫砂、猫粮、猫窝和疫苗这些基础事项。', '/static/images/post-cover-adopt.png', 'PENDING', NULL, 0, 0, 0, NULL),
(4, 5, 'training', '柯基召回训练一周记录', '每天固定两次短时间训练，比长时间重复更容易保持狗狗注意力。', '/static/images/post-cover-training.png', 'APPROVED', '内容符合社区规范', 15, 7, 3, '2026-03-20 10:20:00'),
(5, 6, 'rescue', '小橘救助后的适应过程', '从躲在纸箱到主动蹭手，大概用了两周时间，稳定环境很重要。', '/static/images/post-cover-rescue.png', 'APPROVED', '内容符合社区规范', 21, 12, 4, '2026-03-21 16:40:00'),
(6, 7, 'daily', '第一次给比熊剪指甲的准备清单', '先熟悉脚掌触碰，再准备止血粉和奖励零食，过程不要着急。', '/static/images/post-cover-nail.png', 'PENDING', NULL, 0, 0, 0, NULL),
(7, 2, 'knowledge', '猫咪换粮过渡比例怎么安排', '建议用 7 到 10 天逐步替换，观察软便、呕吐和食欲变化。', '/static/images/post-cover-food.png', 'APPROVED', '内容符合社区规范', 18, 9, 2, '2026-03-22 09:15:00'),
(8, 3, 'help', '狗狗外出后脚垫发红怎么办', '散步回家后发现脚垫偏红，想确认是否需要立刻就医。', '/static/images/post-cover-paw.png', 'APPROVED', '内容符合社区规范', 6, 2, 2, '2026-03-22 20:10:00'),
(9, 5, 'daily', '寄养前需要和商家确认哪些细节', '包括喂食次数、遛狗频率、是否单独隔间以及突发情况联系流程。', '/static/images/post-cover-boarding.png', 'APPROVED', '内容符合社区规范', 10, 5, 1, '2026-03-23 12:30:00'),
(10, 6, 'knowledge', '领养回访照片怎么拍更清晰', '建议拍摄正脸、全身、食盆和活动区域，便于平台了解适应情况。', '/static/images/post-cover-followup.png', 'REJECTED', '图片信息不足，请补充更清晰的环境照片', 0, 0, 0, NULL);

INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2), (2, 1), (2, 3), (3, 1), (4, 5), (4, 1), (5, 6), (5, 1), (6, 1), (7, 4), (7, 7), (8, 7), (9, 3), (10, 6);

INSERT INTO post_comments (id, post_id, user_id, content, status, created_at) VALUES
(1, 1, 3, '这篇整理得很清晰，已经收藏了。', 'NORMAL', '2026-03-18 13:20:00'),
(2, 1, 4, '想问一下猫三联加强针的时间间隔怎么安排。', 'NORMAL', '2026-03-18 18:00:00'),
(3, 2, 2, '换季真的要注意皮肤护理，感谢分享。', 'NORMAL', '2026-03-19 11:00:00'),
(4, 4, 2, '短时间高频训练这个方法很有用。', 'NORMAL', '2026-03-20 11:00:00'),
(5, 4, 3, '召回训练可以加上固定口令，效果会更稳定。', 'NORMAL', '2026-03-20 12:10:00'),
(6, 4, 6, '布丁看起来状态很好，期待后续记录。', 'NORMAL', '2026-03-20 14:25:00'),
(7, 5, 2, '救助猫适应期确实需要耐心。', 'NORMAL', '2026-03-21 17:20:00'),
(8, 5, 5, '小橘现在能主动互动很棒。', 'NORMAL', '2026-03-21 18:05:00'),
(9, 5, 7, '想请教救助后驱虫频率怎么安排。', 'NORMAL', '2026-03-21 19:40:00'),
(10, 5, 3, '可以把回访照片也整理成帖子，方便新手参考。', 'NORMAL', '2026-03-21 21:10:00'),
(11, 7, 5, '我一般按 25、50、75 的比例逐步过渡。', 'NORMAL', '2026-03-22 10:00:00'),
(12, 7, 6, '软便时建议先停在当前比例观察两天。', 'NORMAL', '2026-03-22 11:10:00'),
(13, 8, 2, '如果有破皮或持续舔咬，建议尽快去医院看看。', 'NORMAL', '2026-03-22 21:00:00'),
(14, 8, 5, '可以先检查是否有异物或烫伤痕迹。', 'NORMAL', '2026-03-22 21:30:00'),
(15, 9, 3, '还要确认夜间是否有人值守。', 'NORMAL', '2026-03-23 13:05:00');

INSERT INTO post_likes (post_id, user_id) VALUES
(1, 3), (1, 4), (2, 2), (4, 2), (4, 3), (4, 6), (5, 2), (5, 3), (5, 5), (5, 7), (7, 3), (7, 5), (8, 2), (9, 2), (9, 6);

INSERT INTO post_favorites (post_id, user_id) VALUES
(1, 3), (1, 4), (2, 2), (4, 2), (4, 6), (5, 2), (5, 5), (5, 7), (7, 3), (7, 5), (8, 5), (9, 2), (9, 6);

INSERT INTO adoption_pets (id, name, type, breed, gender, age_desc, city, health_status, personality, adoption_requirements, story, status, cover_url) VALUES
(1, '奶糕', 'CAT', '中华田园猫', 'FEMALE', '4个月', '上海', '已完成基础体检和驱虫', '亲人活泼', '需稳定居住环境，有封窗条件', '在小区被救助后逐渐恢复健康，现在状态很好。', 'ONLINE', '/static/images/adoption-cat-naigao.png'),
(2, '豆包', 'DOG', '柯基', 'MALE', '1岁', '上海', '疫苗齐全', '喜欢互动', '需有遛狗时间，接受回访', '原主人搬家无法继续照顾，正在寻找新家庭。', 'ONLINE', '/static/images/adoption-dog-doubao.png'),
(3, '花卷', 'CAT', '狸花猫', 'MALE', '8个月', '上海', '已绝育，疫苗齐全', '警惕但亲人', '需要有养猫经验，接受定期回访', '在园区被救助，适合安静家庭。', 'ONLINE', '/static/images/adoption-cat-huajuan.png'),
(4, '栗子', 'DOG', '泰迪', 'FEMALE', '2岁', '杭州', '体检正常，已驱虫', '活泼黏人', '需要每天遛狗，家人共同认可领养', '原主人因工作变动委托平台重新寻找家庭。', 'ONLINE', '/static/images/adoption-dog-lizi.png'),
(5, '奶盖', 'CAT', '布偶混血', 'FEMALE', '1岁半', '苏州', '轻微软便恢复中', '温顺安静', '需接受后续肠胃调理建议', '救助后恢复中，适合耐心照顾。', 'OFFLINE', '/static/images/adoption-cat-naigai.png'),
(6, '奥利奥', 'DOG', '边牧', 'MALE', '3岁', '上海', '疫苗齐全', '聪明，需要运动量', '需要有训犬经验和稳定户外活动时间', '前家庭无法满足运动需求，现寻找更合适环境。', 'ONLINE', '/static/images/adoption-dog-aoliao.png');

INSERT INTO adoption_applications (pet_id, user_id, experience_desc, living_condition_desc, contact_phone, status, review_remark, reviewed_by, reviewed_at, created_at) VALUES
(1, 2, '有两年养猫经验，熟悉喂药和日常护理。', '自有住房，已封窗，可长期照顾。', '13800000001', 'PENDING', NULL, NULL, NULL, '2026-03-19 20:00:00'),
(2, 4, '曾经照顾过中型犬，能接受训练安排。', '家庭成员支持领养，附近有宠物医院。', '13800000003', 'APPROVED', '资料完整，沟通情况良好。', 1, '2026-03-19 16:00:00', '2026-03-18 15:00:00'),
(3, 5, '有多年养狗经验，也照顾过朋友的猫。', '自有住房，家人支持，已准备隔离房间。', '13800000004', 'PENDING', NULL, NULL, NULL, '2026-03-21 10:00:00'),
(4, 2, '目前有两只猫，第一次申请领养狗狗。', '家庭空间足够，但需要进一步确认遛狗安排。', '13800000001', 'REJECTED', '申请人与宠物需求匹配度不足，建议补充遛狗安排后再申请。', 1, '2026-03-22 14:00:00', '2026-03-21 13:40:00'),
(6, 3, '熟悉边牧训练，有固定户外运动时间。', '近郊住房，附近有宠物友好公园。', '13800000002', 'PENDING', NULL, NULL, NULL, '2026-03-23 18:30:00');

INSERT INTO service_categories (id, name, sort, status) VALUES
(1, '洗护', 1, 'ACTIVE'),
(2, '寄养', 2, 'ACTIVE'),
(3, '体检', 3, 'ACTIVE'),
(4, '训练', 4, 'ACTIVE'),
(5, '上门护理', 5, 'ACTIVE');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status) VALUES
(1, '毛孩子洗护屋', '浦东新区', '锦绣路 188 号', '021-55550001', '10:00-20:00', 4.8, 'ACTIVE'),
(2, '安心宠物诊所', '徐汇区', '天钥桥路 66 号', '021-55550002', '09:00-18:00', 4.7, 'ACTIVE'),
(3, '爪爪训练营', '闵行区', '七莘路 299 号', '021-55550003', '09:30-19:30', 4.6, 'ACTIVE'),
(4, '暖窝寄养中心', '长宁区', '虹桥路 520 号', '021-55550004', '08:00-21:00', 4.9, 'ACTIVE'),
(5, '萌宠上门护理', '浦东新区', '张江路 88 号', '021-55550005', '10:00-18:00', 4.5, 'DISABLED');

INSERT INTO merchant_services (id, merchant_id, category_id, name, price, duration_minutes, status) VALUES
(1, 1, 1, '基础洗护', 128.00, 90, 'ACTIVE'),
(2, 1, 2, '短期寄养', 150.00, 1440, 'ACTIVE'),
(3, 2, 3, '年度体检', 299.00, 60, 'ACTIVE'),
(4, 3, 4, '幼犬基础训练', 199.00, 60, 'ACTIVE'),
(5, 3, 4, '行为纠正咨询', 299.00, 90, 'ACTIVE'),
(6, 4, 2, '单犬豪华寄养', 220.00, 1440, 'ACTIVE'),
(7, 4, 3, '入住前健康检查', 99.00, 30, 'ACTIVE'),
(8, 5, 5, '上门剪指甲', 79.00, 30, 'DISABLED');

INSERT INTO service_bookings (user_id, merchant_id, merchant_service_id, booking_time, contact_name, contact_phone, status, remark, created_at) VALUES
(3, 1, 1, '2026-03-21 14:00:00', '陈先生', '13800000002', 'CONFIRMED', '狗狗胆小，请温和处理。', '2026-03-19 19:00:00'),
(2, 2, 3, '2026-03-22 10:30:00', '李女士', '13800000001', 'PENDING', '想增加口腔检查。', '2026-03-20 09:00:00'),
(5, 3, 4, '2026-03-24 15:00:00', '周先生', '13800000004', 'PENDING', '柯基召回训练咨询。', '2026-03-21 09:00:00'),
(6, 4, 6, '2026-03-25 09:30:00', '赵女士', '13800000005', 'CONFIRMED', '需要单独隔间。', '2026-03-21 11:30:00'),
(7, 1, 1, '2026-03-26 13:00:00', '孙女士', '13800000006', 'CANCELLED', '用户临时取消。', '2026-03-20 16:20:00'),
(2, 3, 5, '2026-03-27 10:00:00', '李女士', '13800000001', 'COMPLETED', '行为咨询已完成。', '2026-03-18 12:00:00');

INSERT INTO merchant_reviews (merchant_id, user_id, score, content, created_at) VALUES
(1, 3, 5, '服务很细致，对狗狗状态也会耐心说明。', '2026-03-18 18:30:00'),
(2, 2, 4, '医生解释很详细，体检建议也比较实用。', '2026-03-19 15:20:00'),
(3, 5, 5, '训练师会先观察狗狗状态，再给出家庭训练计划。', '2026-03-22 18:00:00'),
(4, 6, 5, '寄养环境干净，照片反馈很及时。', '2026-03-23 20:00:00'),
(1, 7, 4, '第一次洗护整体顺利，等待区也比较舒适。', '2026-03-24 14:20:00');

INSERT INTO product_categories (id, name, pet_type, sort, status) VALUES
(1, '主粮', 'CAT', 1, 'ACTIVE'),
(2, '零食', 'DOG', 2, 'ACTIVE'),
(3, '清洁护理', 'ALL', 3, 'ACTIVE'),
(4, '玩具', 'ALL', 4, 'ACTIVE'),
(5, '出行', 'DOG', 5, 'ACTIVE'),
(6, '健康护理', 'ALL', 6, 'ACTIVE');

INSERT INTO products (id, category_id, name, subtitle, image_url, price, stock, pet_type, status, description) VALUES
(1, 1, '幼猫无谷主粮', '适合 2-12 月龄幼猫', '/static/images/product-cat-food.png', 159.00, 80, 'CAT', 'ON_SALE', '高蛋白配方，适合幼猫成长阶段。'),
(2, 2, '冻干鸡肉粒', '训练奖励小零食', '/static/images/product-dog-treat.png', 49.00, 120, 'DOG', 'ON_SALE', '单一肉源，适口性好。'),
(3, 3, '宠物免洗泡沫', '日常清洁护理', '/static/images/product-cleaner.png', 69.00, 60, 'ALL', 'ON_SALE', '适合外出后简单清洁使用。'),
(4, 4, '耐咬橡胶球', '适合中小型犬互动训练', '/static/images/product-toy-ball.png', 39.00, 150, 'DOG', 'ON_SALE', '弹性材质，适合日常互动和召回训练。'),
(5, 4, '逗猫羽毛棒', '低敏羽毛替换头', '/static/images/product-feather.png', 29.00, 200, 'CAT', 'ON_SALE', '提升猫咪运动量，建议在看护下使用。'),
(6, 5, '宠物外出胸背', '反光织带，夜间更醒目', '/static/images/product-harness.png', 89.00, 90, 'DOG', 'ON_SALE', '适合日常散步和短途出行。'),
(7, 6, '宠物益生菌', '肠胃调理日常补充', '/static/images/product-probiotic.png', 99.00, 70, 'ALL', 'ON_SALE', '换粮期和软便恢复期可按说明补充。'),
(8, 3, '猫砂除臭喷雾', '温和气味控制', '/static/images/product-cleaner.png', 45.00, 0, 'CAT', 'OFF_SHELF', '用于后台上下架和库存联调。');

INSERT INTO cart_items (user_id, product_id, quantity, checked) VALUES
(2, 1, 1, 1),
(2, 3, 2, 1),
(3, 4, 1, 1),
(3, 7, 2, 1),
(4, 2, 1, 0),
(5, 4, 2, 1),
(5, 6, 1, 0),
(6, 5, 3, 1),
(7, 7, 1, 1);

INSERT INTO shop_orders (id, user_id, order_no, total_amount, discount_amount, pay_amount, user_coupon_id, status, receiver_name, receiver_phone, receiver_address, remark, created_at) VALUES
(1, 4, 'PSP20260319153000DEMO001', 49.00, 0.00, 49.00, NULL, 'SHIPPED', '王小姐', '13800000003', '上海市 长宁区 示例路 88 号', '工作日送达', '2026-03-19 15:30:00'),
(2, 2, 'PSP20260320101000DEMO002', 258.00, 20.00, 238.00, 1, 'PENDING', '李女士', '13800000001', '上海市 浦东新区 示例路 188 号', '请放门卫', '2026-03-20 10:10:00'),
(3, 3, 'PSP20260321150000DEMO003', 237.00, 0.00, 237.00, NULL, 'PAID', '陈先生', '13800000002', '上海市 徐汇区 示例路 66 号', '周末配送', '2026-03-21 15:00:00'),
(4, 5, 'PSP20260322183000DEMO004', 167.00, 10.00, 157.00, 6, 'CANCELLED', '周先生', '13800000004', '上海市 闵行区 示例路 299 号', '用户取消', '2026-03-22 18:30:00');

INSERT INTO shop_order_items (order_id, product_id, product_name, product_image_url, unit_price, quantity, subtotal_amount) VALUES
(1, 2, '冻干鸡肉粒', '/static/images/product-dog-treat.png', 49.00, 1, 49.00),
(2, 1, '幼猫无谷主粮', '/static/images/product-cat-food.png', 159.00, 1, 159.00),
(2, 7, '宠物益生菌', '/static/images/product-probiotic.png', 99.00, 1, 99.00),
(3, 4, '耐咬橡胶球', '/static/images/product-toy-ball.png', 39.00, 1, 39.00),
(3, 7, '宠物益生菌', '/static/images/product-probiotic.png', 99.00, 2, 198.00),
(4, 6, '宠物外出胸背', '/static/images/product-harness.png', 89.00, 1, 89.00),
(4, 5, '逗猫羽毛棒', '/static/images/product-feather.png', 29.00, 1, 29.00),
(4, 2, '冻干鸡肉粒', '/static/images/product-dog-treat.png', 49.00, 1, 49.00);

INSERT INTO banners (id, title, image_url, link_url, status, sort, created_by) VALUES
(1, '春季养宠指南', '/static/images/banner-spring.png', '/community/posts/1', 'ACTIVE', 1, 1),
(2, '领养代替购买', '/static/images/banner-adoption.png', '/adoption', 'ACTIVE', 2, 1),
(3, '训练营体验课', '/static/images/banner-training.png', '/services/merchant/3', 'ACTIVE', 3, 1),
(4, '肠胃护理专区', '/static/images/banner-health.png', '/shop', 'DISABLED', 4, 1);

INSERT INTO recommendations (biz_type, biz_id, slot_code, status, sort, created_by) VALUES
('post', 1, 'HOME_POST', 'ACTIVE', 1, 1),
('post', 2, 'HOME_POST', 'ACTIVE', 2, 1),
('post', 5, 'HOME_POST', 'ACTIVE', 3, 1),
('post', 7, 'HOME_POST', 'ACTIVE', 4, 1),
('service', 1, 'HOME_SERVICE', 'ACTIVE', 1, 1),
('service', 2, 'HOME_SERVICE', 'ACTIVE', 2, 1),
('service', 3, 'HOME_SERVICE', 'ACTIVE', 3, 1),
('service', 4, 'HOME_SERVICE', 'ACTIVE', 4, 1),
('product', 1, 'HOME_PRODUCT', 'ACTIVE', 1, 1),
('product', 3, 'HOME_PRODUCT', 'ACTIVE', 2, 1),
('product', 4, 'HOME_PRODUCT', 'ACTIVE', 3, 1),
('product', 7, 'HOME_PRODUCT', 'ACTIVE', 4, 1);

SET FOREIGN_KEY_CHECKS = 1;
