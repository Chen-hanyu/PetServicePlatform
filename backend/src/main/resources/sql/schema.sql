SET NAMES utf8mb4;

-- The application connects to an existing database configured by
-- SPRING_DATASOURCE_URL. Do not create/drop databases here; managed
-- platforms such as Railway provide the database name dynamically.

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(100),
    nickname VARCHAR(50) NOT NULL,
    avatar_url VARCHAR(255),
    gender VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    bio VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_users_phone (phone),
    KEY idx_users_role (role),
    KEY idx_users_status (status),
    KEY idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_messages_user_read_created (user_id, is_read, created_at),
    CONSTRAINT fk_messages_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    breed VARCHAR(50),
    gender VARCHAR(20),
    birthday DATE,
    weight DECIMAL(8, 2),
    avatar_url VARCHAR(255),
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_pets_user_type (user_id, type),
    CONSTRAINT fk_pets_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pet_vaccines (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    vaccine_name VARCHAR(100) NOT NULL,
    vaccinated_at DATE NOT NULL,
    next_due_at DATE,
    remark VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_pet_vaccines_pet_id (pet_id),
    CONSTRAINT fk_pet_vaccines_pet_id FOREIGN KEY (pet_id) REFERENCES pets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pet_weights (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    weight DECIMAL(8, 2) NOT NULL,
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_pet_weights_pet_id (pet_id),
    CONSTRAINT fk_pet_weights_pet_id FOREIGN KEY (pet_id) REFERENCES pets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pet_albums (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    caption VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_pet_albums_pet_id (pet_id),
    CONSTRAINT fk_pet_albums_pet_id FOREIGN KEY (pet_id) REFERENCES pets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS community_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    cover_url VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    review_remark VARCHAR(255),
    like_count INT NOT NULL DEFAULT 0,
    favorite_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    published_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_community_posts_status_category_published (status, category, published_at),
    KEY idx_community_posts_status_like_published (status, like_count, published_at),
    KEY idx_community_posts_user_id (user_id),
    CONSTRAINT fk_community_posts_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_addresses_user_default (user_id, is_default),
    KEY idx_user_addresses_user_status (user_id, status),
    CONSTRAINT fk_user_addresses_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'AMOUNT',
    discount_amount DECIMAL(10, 2) NOT NULL,
    min_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    start_at DATETIME,
    end_at DATETIME,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_coupons_status_time (status, start_at, end_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNUSED',
    used_order_id BIGINT,
    used_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_coupons_user_status (user_id, status),
    KEY idx_user_coupons_coupon_id (coupon_id),
    CONSTRAINT fk_user_coupons_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_coupons_coupon_id FOREIGN KEY (coupon_id) REFERENCES coupons(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_post_comments_post_id (post_id),
    KEY idx_post_comments_user_id (user_id),
    CONSTRAINT fk_post_comments_post_id FOREIGN KEY (post_id) REFERENCES community_posts(id),
    CONSTRAINT fk_post_comments_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_likes_post_user (post_id, user_id),
    KEY idx_post_likes_user_id (user_id),
    CONSTRAINT fk_post_likes_post_id FOREIGN KEY (post_id) REFERENCES community_posts(id),
    CONSTRAINT fk_post_likes_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_favorites_post_user (post_id, user_id),
    KEY idx_post_favorites_user_id (user_id),
    CONSTRAINT fk_post_favorites_post_id FOREIGN KEY (post_id) REFERENCES community_posts(id),
    CONSTRAINT fk_post_favorites_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tags_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    UNIQUE KEY uk_post_tags_post_tag (post_id, tag_id),
    KEY idx_post_tags_tag_id (tag_id),
    CONSTRAINT fk_post_tags_post_id FOREIGN KEY (post_id) REFERENCES community_posts(id),
    CONSTRAINT fk_post_tags_tag_id FOREIGN KEY (tag_id) REFERENCES tags(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS adoption_pets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    breed VARCHAR(50),
    gender VARCHAR(20),
    age_desc VARCHAR(50),
    city VARCHAR(50),
    health_status VARCHAR(255),
    personality VARCHAR(255),
    adoption_requirements TEXT,
    story TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
    cover_url VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_adoption_pets_status_type_city (status, type, city),
    KEY idx_adoption_pets_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @adoption_pets_cover_url_sql := (
    SELECT IF(
        COALESCE(CHARACTER_MAXIMUM_LENGTH, 0) < 1000,
        'ALTER TABLE adoption_pets MODIFY COLUMN cover_url VARCHAR(1000)',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'adoption_pets'
      AND column_name = 'cover_url'
);
PREPARE adoption_pets_cover_url_stmt FROM @adoption_pets_cover_url_sql;
EXECUTE adoption_pets_cover_url_stmt;
DEALLOCATE PREPARE adoption_pets_cover_url_stmt;

CREATE TABLE IF NOT EXISTS adoption_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    experience_desc TEXT NOT NULL,
    living_condition_desc TEXT NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    review_remark VARCHAR(255),
    reviewed_by BIGINT,
    reviewed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_adoption_applications_pet_status (pet_id, status),
    KEY idx_adoption_applications_user_status (user_id, status),
    KEY idx_adoption_applications_reviewed_by (reviewed_by),
    CONSTRAINT fk_adoption_applications_pet_id FOREIGN KEY (pet_id) REFERENCES adoption_pets(id),
    CONSTRAINT fk_adoption_applications_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_adoption_applications_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS service_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    sort INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    UNIQUE KEY uk_service_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS merchants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    district VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    business_hours VARCHAR(100),
    score DECIMAL(3, 1) NOT NULL DEFAULT 0.0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_merchants_district_status (district, status),
    KEY idx_merchants_status_score (status, score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS merchant_services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_merchant_services_merchant_id (merchant_id),
    KEY idx_merchant_services_category_id (category_id),
    CONSTRAINT fk_merchant_services_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    CONSTRAINT fk_merchant_services_category_id FOREIGN KEY (category_id) REFERENCES service_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS merchant_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    content VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_merchant_reviews_merchant_user (merchant_id, user_id),
    KEY idx_merchant_reviews_merchant_created (merchant_id, created_at),
    KEY idx_merchant_reviews_user_id (user_id),
    CONSTRAINT fk_merchant_reviews_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    CONSTRAINT fk_merchant_reviews_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS service_bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    merchant_service_id BIGINT NOT NULL,
    booking_time DATETIME NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_service_bookings_merchant_time_status (merchant_id, booking_time, status),
    KEY idx_service_bookings_merchant_service_time_status (merchant_id, merchant_service_id, booking_time, status),
    KEY idx_service_bookings_user_status_created (user_id, status, created_at),
    CONSTRAINT fk_service_bookings_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_service_bookings_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    CONSTRAINT fk_service_bookings_merchant_service_id FOREIGN KEY (merchant_service_id) REFERENCES merchant_services(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    pet_type VARCHAR(20),
    sort INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    UNIQUE KEY uk_product_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    subtitle VARCHAR(255),
    image_url VARCHAR(1000),
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    pet_type VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ON_SALE',
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_products_category_status_price (category_id, status, price),
    KEY idx_products_status_created (status, created_at),
    KEY idx_products_pet_type (pet_type),
    CONSTRAINT fk_products_category_id FOREIGN KEY (category_id) REFERENCES product_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @products_image_url_sql := (
    SELECT IF(
        COALESCE(CHARACTER_MAXIMUM_LENGTH, 0) < 1000,
        'ALTER TABLE products MODIFY COLUMN image_url VARCHAR(1000)',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'products'
      AND column_name = 'image_url'
);
PREPARE products_image_url_stmt FROM @products_image_url_sql;
EXECUTE products_image_url_stmt;
DEALLOCATE PREPARE products_image_url_stmt;

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    checked TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_cart_items_user_product (user_id, product_id),
    KEY idx_cart_items_product_id (product_id),
    CONSTRAINT fk_cart_items_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_cart_items_product_id FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS shop_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    pay_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    remark VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_shop_orders_order_no (order_no),
    KEY idx_shop_orders_user_status_created (user_id, status, created_at),
    CONSTRAINT fk_shop_orders_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Idempotent migration for databases created before checkout coupon fields were added.
SET @shop_orders_discount_amount_sql := (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE shop_orders ADD COLUMN discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER total_amount',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'shop_orders'
      AND column_name = 'discount_amount'
);
PREPARE shop_orders_discount_amount_stmt FROM @shop_orders_discount_amount_sql;
EXECUTE shop_orders_discount_amount_stmt;
DEALLOCATE PREPARE shop_orders_discount_amount_stmt;

SET @shop_orders_user_coupon_id_sql := (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE shop_orders ADD COLUMN user_coupon_id BIGINT NULL AFTER pay_amount',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'shop_orders'
      AND column_name = 'user_coupon_id'
);
PREPARE shop_orders_user_coupon_id_stmt FROM @shop_orders_user_coupon_id_sql;
EXECUTE shop_orders_user_coupon_id_stmt;
DEALLOCATE PREPARE shop_orders_user_coupon_id_stmt;

SET @shop_orders_user_coupon_index_sql := (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE shop_orders ADD INDEX idx_shop_orders_user_coupon_id (user_coupon_id)',
        'SELECT 1'
    )
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'shop_orders'
      AND index_name = 'idx_shop_orders_user_coupon_id'
);
PREPARE shop_orders_user_coupon_index_stmt FROM @shop_orders_user_coupon_index_sql;
EXECUTE shop_orders_user_coupon_index_stmt;
DEALLOCATE PREPARE shop_orders_user_coupon_index_stmt;

CREATE TABLE IF NOT EXISTS shop_order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_image_url VARCHAR(1000),
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal_amount DECIMAL(10, 2) NOT NULL,
    KEY idx_shop_order_items_order_id (order_id),
    KEY idx_shop_order_items_product_id (product_id),
    CONSTRAINT fk_shop_order_items_order_id FOREIGN KEY (order_id) REFERENCES shop_orders(id),
    CONSTRAINT fk_shop_order_items_product_id FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @shop_order_items_product_image_url_sql := (
    SELECT IF(
        COALESCE(CHARACTER_MAXIMUM_LENGTH, 0) < 1000,
        'ALTER TABLE shop_order_items MODIFY COLUMN product_image_url VARCHAR(1000)',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'shop_order_items'
      AND column_name = 'product_image_url'
);
PREPARE shop_order_items_product_image_url_stmt FROM @shop_order_items_product_image_url_sql;
EXECUTE shop_order_items_product_image_url_stmt;
DEALLOCATE PREPARE shop_order_items_product_image_url_stmt;

CREATE TABLE IF NOT EXISTS banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    link_url VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort INT NOT NULL DEFAULT 0,
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_banners_status_sort (status, sort),
    KEY idx_banners_created_by (created_by),
    CONSTRAINT fk_banners_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    biz_type VARCHAR(30) NOT NULL,
    biz_id BIGINT NOT NULL,
    slot_code VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort INT NOT NULL DEFAULT 0,
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_recommendations_biz_type_biz_id (biz_type, biz_id),
    KEY idx_recommendations_slot_status_sort (slot_code, status, sort),
    KEY idx_recommendations_created_by (created_by),
    CONSTRAINT fk_recommendations_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
