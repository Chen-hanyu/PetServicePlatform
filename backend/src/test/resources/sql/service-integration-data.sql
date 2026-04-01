INSERT INTO users (id, role, phone, nickname, avatar_url, gender, status, bio, created_at, updated_at)
VALUES
    (1, 'ADMIN', '13900000000', '系统管理员', NULL, 'UNKNOWN', 'ACTIVE', '后台管理员', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (2, 'USER', '13800000001', '团子妈', NULL, 'FEMALE', 'ACTIVE', '普通用户', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (3, 'USER', '13800000002', '柴犬研究员', NULL, 'MALE', 'ACTIVE', '普通用户', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO service_categories (id, name, sort, status)
VALUES
    (1, '洗护', 1, 'ACTIVE');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status, created_at, updated_at)
VALUES
    (1, '毛孩子洗护屋', '浦东新区', '锦绣路 188 号', '021-55550001', '10:00-20:00', 0.0, 'ACTIVE', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO merchant_services (id, merchant_id, category_id, name, price, duration_minutes, status, created_at)
VALUES
    (1, 1, 1, '基础洗护', 128.00, 90, 'ACTIVE', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO service_bookings (id, user_id, merchant_id, merchant_service_id, booking_time, contact_name, contact_phone, status, remark, created_at, updated_at)
VALUES
    (1, 2, 1, 1, TIMESTAMP '2026-03-20 14:00:00', '李女士', '13800000001', 'PENDING', '初次洗护', TIMESTAMP '2026-03-19 18:00:00', TIMESTAMP '2026-03-19 18:00:00'),
    (2, 3, 1, 1, TIMESTAMP '2026-03-18 10:00:00', '陈先生', '13800000002', 'COMPLETED', '已完成服务', TIMESTAMP '2026-03-17 18:00:00', TIMESTAMP '2026-03-18 12:00:00');
