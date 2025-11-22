-- Drop all tables first (in reverse order to handle potential foreign key dependencies)
DROP TABLE IF EXISTS `stu_balance`;
DROP TABLE IF EXISTS `event`;
DROP TABLE IF EXISTS `token`;
DROP TABLE IF EXISTS `sta_user`;
DROP TABLE IF EXISTS `stu_user`;

-- Create all tables
CREATE TABLE `token` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `token` varchar(64) NOT NULL UNIQUE COMMENT 'Secure random token (Base64 URL-encoded)',
    `user_id` int unsigned NOT NULL COMMENT 'References either stu_user.id or sta_user.id',
    `user_type` varchar(20) NOT NULL COMMENT 'Either "student" or "staff"',
    `expires_at` timestamp NOT NULL COMMENT 'Token expiration time (rolling 30-minute window)',
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `stu_user` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `user_id` varchar(255) UNIQUE NOT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `sta_user` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `user_id` varchar(255) UNIQUE NOT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `event` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `init_stu_id` varchar(255) DEFAULT NULL,
    `init_sta_id` varchar(255) DEFAULT NULL,
    `recv_stu_id` varchar(255) DEFAULT NULL,
    `recv_sta_id` varchar(255) DEFAULT NULL,
    `point_diff` int NOT NULL DEFAULT '0',
    `credit_diff` int NOT NULL DEFAULT '0',
    `type` varchar(255) NOT NULL,
    `content_html` mediumtext DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `stu_balance` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `user_id` varchar(255) UNIQUE NOT NULL COMMENT 'References stu_user.user_id',
    `accumulated_points` int NOT NULL DEFAULT '0' COMMENT 'Total accumulated points for display',
    `accumulated_credits` int NOT NULL DEFAULT '0' COMMENT 'Total accumulated credits for display',
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
