INSERT INTO users (id, role, phone, password_hash, nickname, avatar_url, gender, status, bio, created_at, updated_at)
VALUES
    (1, 'ADMIN', '13900000000', '$2a$10$tlnMMJ2lktQPQ6wSxcD5o.AXnU.yP61/.rCjZwO3MhVWfW3NC2WwS', 'Admin', NULL, 'UNKNOWN', 'ACTIVE', 'Administrator', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (2, 'USER', '13800000001', '$2a$10$oQ9TueFaM3LFV8M6jfIh.O0nwr2aC5mlOeUtclblsY9yuoHmXc7VC', 'Alice', NULL, 'FEMALE', 'ACTIVE', 'User', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (3, 'USER', '13800000002', '$2a$10$oQ9TueFaM3LFV8M6jfIh.O0nwr2aC5mlOeUtclblsY9yuoHmXc7VC', 'Bob', NULL, 'MALE', 'ACTIVE', 'User', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO service_categories (id, name, sort, status)
VALUES
    (1, '娲楁姢', 1, 'ACTIVE');

INSERT INTO merchants (id, name, district, address, phone, business_hours, score, status, created_at, updated_at)
VALUES
    (1, '姣涘瀛愭礂鎶ゅ眿', '娴︿笢鏂板尯', '閿︾唬璺?188 鍙?', '021-55550001', '10:00-20:00', 0.0, 'ACTIVE', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO merchant_services (id, merchant_id, category_id, name, price, duration_minutes, status, created_at)
VALUES
    (1, 1, 1, '鍩虹娲楁姢', 128.00, 90, 'ACTIVE', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO service_bookings (id, user_id, merchant_id, merchant_service_id, booking_time, contact_name, contact_phone, status, remark, created_at, updated_at)
VALUES
    (1, 2, 1, 1, TIMESTAMP '2026-03-20 14:00:00', '鏉庡コ澹?', '13800000001', 'PENDING', '鍒濇娲楁姢', TIMESTAMP '2026-03-19 18:00:00', TIMESTAMP '2026-03-19 18:00:00'),
    (2, 3, 1, 1, TIMESTAMP '2026-03-18 10:00:00', '闄堝厛鐢?', '13800000002', 'COMPLETED', '宸插畬鎴愭湇鍔?', TIMESTAMP '2026-03-17 18:00:00', TIMESTAMP '2026-03-18 12:00:00');
