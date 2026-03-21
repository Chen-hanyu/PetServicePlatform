INSERT INTO adoption_pets (id, name, type, breed, gender, age_desc, city, health_status, personality, adoption_requirements, story, status, cover_url, created_at, updated_at)
VALUES
    (1, '团子', 'CAT', '中华田园猫', 'FEMALE', '1岁', '上海', '已绝育已免疫', '温顺亲人', '需稳定居住环境', '从救助站来', 'ONLINE', '/uploads/adoption/1.png', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00');

INSERT INTO adoption_applications (id, pet_id, user_id, experience_desc, living_condition_desc, contact_phone, status, review_remark, reviewed_by, reviewed_at, created_at)
VALUES
    (1, 1, 10, '有两年养猫经验', '已封窗，可长期照顾', '13800000000', 'PENDING', NULL, NULL, NULL, TIMESTAMP '2026-03-15 12:00:00'),
    (2, 1, 11, '已有领养经验', '合租但可单独安置猫咪', '13900000000', 'APPROVED', '已审核通过', 99, TIMESTAMP '2026-03-16 09:00:00', TIMESTAMP '2026-03-15 15:00:00');
