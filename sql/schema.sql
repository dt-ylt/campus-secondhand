-- =====================================================================
-- 校园二手闲置交易平台 - 数据库建表脚本
-- 阶段 1：数据库设计
-- 字符集：utf8mb4（支持中文和 emoji）；存储引擎：InnoDB（支持事务）
-- =====================================================================

-- 1. 创建数据库（如果不存在才创建，避免重复执行报错）
CREATE DATABASE IF NOT EXISTS campus_secondhand
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE campus_secondhand;


-- =====================================================================
-- 表 1：sys_user 用户表
-- 说明：存放普通用户和管理员的账号信息。普通用户发商品/收藏/留言，管理员审核商品。
-- 用 sys_ 前缀：因为既有普通用户也有管理员，属于"系统用户"，且避开 MySQL 内置的 user 表名冲突。
-- =====================================================================
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    username    VARCHAR(50)  NOT NULL COMMENT '登录账号，唯一',
    password    VARCHAR(100) NOT NULL COMMENT '密码，存 BCrypt 加密后的密文，绝不存明文',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称，展示用',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像图片URL',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0普通用户，1管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用（被管理员封号）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间，自动填当前时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，修改记录时自动刷新',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表（含普通用户和管理员）';


-- =====================================================================
-- 表 2：category 商品分类表
-- 说明：商品按分类筛选（数码、书籍、日用品...）。独立成表方便后台增删分类。
-- =====================================================================
CREATE TABLE category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称，如 数码/书籍',
    icon        VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序值，数字越小越靠前显示',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';


-- =====================================================================
-- 表 3：product 闲置商品表（核心表）
-- 说明：用户发布的二手商品。status 管理审核上下架，deleted 管理用户删除（逻辑删除）。
-- =====================================================================
CREATE TABLE product (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    user_id         BIGINT        NOT NULL COMMENT '发布者用户ID，关联 sys_user.id',
    title           VARCHAR(100)  NOT NULL COMMENT '商品标题',
    description     TEXT          DEFAULT NULL COMMENT '商品详情描述',
    price           DECIMAL(10,2) NOT NULL COMMENT '售价，DECIMAL 存金额避免浮点误差',
    original_price  DECIMAL(10,2) DEFAULT NULL COMMENT '原价，可空',
    category_id     BIGINT        NOT NULL COMMENT '所属分类ID，关联 category.id',
    condition_level TINYINT       NOT NULL DEFAULT 1 COMMENT '成色：1全新 2几乎全新 3轻微使用痕迹 4明显使用痕迹',
    location        VARCHAR(100)  DEFAULT NULL COMMENT '交易地点/校区',
    status          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1已上架 2已下架 3已售出',
    view_count      INT           NOT NULL DEFAULT 0 COMMENT '浏览量',
    deleted         TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除（MyBatis-Plus @TableLogic 用）',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_category_status (category_id, status),
    KEY idx_status_create (status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闲置商品表';


-- =====================================================================
-- 表 4：product_image 商品图片表
-- 说明：一个商品有多张图，单独建表存储（比逗号拼接更规范，面试也爱问"多图怎么存"）。
-- =====================================================================
CREATE TABLE product_image (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    product_id  BIGINT       NOT NULL COMMENT '所属商品ID，关联 product.id',
    url         VARCHAR(255) NOT NULL COMMENT '图片URL（MinIO 或本地存储返回的地址）',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序，控制图片展示顺序',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';


-- =====================================================================
-- 表 5：favorite 商品收藏表
-- 说明：用户收藏商品。(user_id, product_id) 联合唯一索引防止同一用户重复收藏同一商品。
-- =====================================================================
CREATE TABLE favorite (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    product_id  BIGINT   NOT NULL COMMENT '商品ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';


-- =====================================================================
-- 表 6：product_comment 商品留言表
-- 说明：用户对商品留言。parent_id 支持回复结构（0=顶级留言），本阶段先用顶级留言。
-- =====================================================================
CREATE TABLE product_comment (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '留言ID',
    product_id  BIGINT       NOT NULL COMMENT '商品ID',
    user_id     BIGINT       NOT NULL COMMENT '留言用户ID',
    content     VARCHAR(500) NOT NULL COMMENT '留言内容',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父留言ID，0表示顶级留言（支持回复，预留）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    PRIMARY KEY (id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品留言表';


-- =====================================================================
-- 初始数据：商品分类（方便后续测试）
-- =====================================================================
INSERT INTO category (name, sort) VALUES
    ('数码电子', 1),
    ('书籍教材', 2),
    ('生活用品', 3),
    ('服饰鞋包', 4),
    ('运动器材', 5),
    ('其他',     99);

-- 说明：管理员账号不在这里插入，因为密码需要 BCrypt 加密。
-- 阶段 3 写完注册接口后，我们注册一个普通用户，再用 SQL 把它的 role 改成 1 即可成为管理员：
--   UPDATE sys_user SET role = 1 WHERE username = '你注册的账号';
