INSERT INTO users (id, role, phone, password_hash, nickname, avatar_url, gender, status, bio, created_at, updated_at)
VALUES
    (1, 'ADMIN', '13900000000', '$2a$10$tlnMMJ2lktQPQ6wSxcD5o.AXnU.yP61/.rCjZwO3MhVWfW3NC2WwS', 'Admin', NULL, 'UNKNOWN', 'ACTIVE', 'Administrator', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (2, 'USER', '13800000001', '$2a$10$oQ9TueFaM3LFV8M6jfIh.O0nwr2aC5mlOeUtclblsY9yuoHmXc7VC', 'Alice', NULL, 'FEMALE', 'ACTIVE', 'User', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (3, 'USER', '13800000002', '$2a$10$oQ9TueFaM3LFV8M6jfIh.O0nwr2aC5mlOeUtclblsY9yuoHmXc7VC', 'Bob', NULL, 'MALE', 'ACTIVE', 'User', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at, created_at, updated_at)
VALUES
    (1, 2, 'knowledge', '骞肩尗鐤嫍鏃堕棿琛ㄦ暣鐞?', '鏁寸悊浜嗗父瑙佹帴绉嶆椂闂村拰娉ㄦ剰浜嬮」銆?', '/uploads/post-1.png', 'APPROVED', '鍐呭姝ｅ父', 12, 5, 2, TIMESTAMP '2026-03-18 10:00:00', TIMESTAMP '2026-03-18 10:00:00', TIMESTAMP '2026-03-18 10:00:00'),
    (2, 3, 'daily', '绛夊緟瀹℃牳鐨勫笘瀛?', '杩欐槸涓€鏉″緟瀹℃牳甯栧瓙銆?', '/uploads/post-2.png', 'PENDING', NULL, 0, 0, 0, NULL, TIMESTAMP '2026-03-19 09:00:00', TIMESTAMP '2026-03-19 09:00:00');

INSERT INTO post_comments (id, post_id, user_id, content, status, created_at)
VALUES
    (1, 1, 3, '杩欑瘒鏁寸悊寰楀緢娓呮銆?', 'NORMAL', TIMESTAMP '2026-03-18 11:00:00'),
    (2, 1, 2, '鎯抽棶涓嬪姞寮洪拡澶氫箙鎵撲竴閽堬紵', 'NORMAL', TIMESTAMP '2026-03-18 12:00:00');
