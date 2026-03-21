INSERT INTO pets (id, user_id, name, type, breed, gender, birthday, weight, avatar_url, description, created_at, updated_at)
VALUES
    (1, 1, '奶糖', 'CAT', '英短', 'FEMALE', DATE '2023-01-10', 3.20, '/uploads/pets/1.png', '活泼亲人', TIMESTAMP '2026-03-01 10:00:00', TIMESTAMP '2026-03-01 10:00:00'),
    (2, 2, '豆包', 'DOG', '柯基', 'MALE', DATE '2022-05-06', 8.60, '/uploads/pets/2.png', '隔壁用户的宠物', TIMESTAMP '2026-03-02 10:00:00', TIMESTAMP '2026-03-02 10:00:00');

INSERT INTO pet_vaccines (id, pet_id, vaccine_name, vaccinated_at, next_due_at, remark, created_at)
VALUES
    (1, 1, '猫三联', DATE '2026-03-01', DATE '2027-03-01', '首针接种完成', TIMESTAMP '2026-03-01 09:00:00');

INSERT INTO pet_weights (id, pet_id, weight, recorded_at, created_at)
VALUES
    (1, 1, 3.20, TIMESTAMP '2026-03-05 09:30:00', TIMESTAMP '2026-03-05 09:30:00');

INSERT INTO pet_albums (id, pet_id, image_url, caption, created_at)
VALUES
    (1, 1, '/uploads/pets/album-1.png', '第一次去体检', TIMESTAMP '2026-03-10 14:00:00');
