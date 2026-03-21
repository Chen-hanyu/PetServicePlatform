INSERT INTO messages (id, user_id, type, title, content, is_read, created_at)
VALUES
    (1, 1, 'SYSTEM', '欢迎来到平台', '请完善你的宠物资料', FALSE, TIMESTAMP '2026-03-20 09:00:00'),
    (2, 1, 'ORDER', '订单已发货', '你的商品已经出库', TRUE, TIMESTAMP '2026-03-19 08:00:00'),
    (3, 2, 'SYSTEM', '别人的消息', '当前用户不应看到这条消息', FALSE, TIMESTAMP '2026-03-18 07:00:00');
