CREATE DATABASE IF NOT EXISTS family_finance_manager;
USE family_finance_manager;

CREATE TABLE IF NOT EXISTS t_test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO t_test(name) VALUES ('Docker-MySQL-OK');

-- 家庭表
CREATE TABLE IF NOT EXISTS family (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    family_name VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    family_id BIGINT NOT NULL,
    name VARCHAR(30) NOT NULL,
    sex CHAR(1) NOT NULL CHECK (sex IN ('M', 'F')),
    phone VARCHAR(20),
    password CHAR(60) NOT NULL,
    role CHAR(1) NOT NULL DEFAULT 'U' CHECK (role IN ('A', 'U')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY fk_family (family_id),
    KEY idx_phone (phone),
    CONSTRAINT fk_user_family FOREIGN KEY (family_id) REFERENCES family(id)
);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    type CHAR(1) NOT NULL CHECK(type IN ('I', 'E')),
    name VARCHAR(50) NOT NULL,
    is_system TINYINT(1) NOT NULL DEFAULT 0,
    created_by BIGINT,
    visibility CHAR(1) NOT NULL DEFAULT 'F' CHECK(visibility IN ('F', 'P')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_parent (parent_id),
    KEY idx_type (type),
    KEY fk_user (created_by),
    CONSTRAINT fk_category_user FOREIGN KEY (created_by) REFERENCES user(id)
);

-- 收入表
CREATE TABLE IF NOT EXISTS income (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inc_no VARCHAR(20) NOT NULL UNIQUE,
    owner_type CHAR(1) NOT NULL CHECK(owner_type IN ('F', 'M')),
    owner_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    inc_time DATETIME NOT NULL,
    remark VARCHAR(500),
    is_draft TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_inc_no(inc_no),
    KEY idx_owner_id(owner_id),
    KEY idx_cate(category_id),
    KEY idx_inc_time(inc_time),
    CONSTRAINT fk_income_category FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 支出表
CREATE TABLE IF NOT EXISTS expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exp_no VARCHAR(20) NOT NULL UNIQUE,
    owner_type CHAR(1) NOT NULL CHECK(owner_type IN ('F', 'M')),
    owner_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    exp_time DATETIME NOT NULL,
    remark VARCHAR(500),
    is_draft TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_exp_no(exp_no),
    KEY idx_owner_id(owner_id),
    KEY idx_cate(category_id),
    KEY idx_exp_time(exp_time),
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES category(id)
);