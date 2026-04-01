INSERT INTO users (id, role, phone, nickname, avatar_url, gender, status, bio, created_at, updated_at)
VALUES
    (1, 'ADMIN', '13900000000', '系统管理员', NULL, 'UNKNOWN', 'ACTIVE', '后台管理员', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (2, 'USER', '13800000001', '团子妈', NULL, 'FEMALE', 'ACTIVE', '普通用户', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (3, 'USER', '13800000002', '柴犬研究员', NULL, 'MALE', 'ACTIVE', '普通用户', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO community_posts (id, user_id, category, title, content, cover_url, status, review_remark, like_count, favorite_count, comment_count, published_at, created_at, updated_at)
VALUES
    (1, 2, 'knowledge', '幼猫疫苗时间表整理', '整理了常见接种时间和注意事项。', '/uploads/post-1.png', 'APPROVED', '内容正常', 12, 5, 2, TIMESTAMP '2026-03-18 10:00:00', TIMESTAMP '2026-03-18 10:00:00', TIMESTAMP '2026-03-18 10:00:00'),
    (2, 3, 'daily', '等待审核的帖子', '这是一条待审核帖子。', '/uploads/post-2.png', 'PENDING', NULL, 0, 0, 0, NULL, TIMESTAMP '2026-03-19 09:00:00', TIMESTAMP '2026-03-19 09:00:00');

INSERT INTO post_comments (id, post_id, user_id, content, status, created_at)
VALUES
    (1, 1, 3, '这篇整理得很清楚。', 'NORMAL', TIMESTAMP '2026-03-18 11:00:00'),
    (2, 1, 2, '想问下加强针多久打一针？', 'NORMAL', TIMESTAMP '2026-03-18 12:00:00');
