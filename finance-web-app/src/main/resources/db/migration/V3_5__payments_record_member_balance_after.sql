-- =====================================================================
-- V3.5 合并脚本（原 V3_5、V3_6、V3_7、V3_8）+ 云/独立操作系统表字段精简
-- 应用发布版本：1.2.3（与根 pom version / finance.version 一致）
-- =====================================================================

-- ---------- 原 V3_5：收支明细 ----------
ALTER TABLE `payments_record`
    ADD COLUMN `member_balance_after` decimal(19, 2) DEFAULT NULL COMMENT '本笔发生后会员账户余额' AFTER `pay_type`;

-- ---------- 原 V3_6：独立服务器业务表 B 系统 ID ----------
ALTER TABLE `business_independent`
ADD COLUMN `B_SYSTEM_SERVER_ID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'B系统服务器ID（与云服务器 B_SYSTEM_SERVER_ID 语义一致；默认可等于主键 id）' AFTER `type`;

ALTER TABLE `business_independent`
ADD INDEX `IDX_B_SYSTEM_SERVER_ID` (`B_SYSTEM_SERVER_ID`) USING BTREE COMMENT 'B系统服务器ID索引';

UPDATE `business_independent`
SET `B_SYSTEM_SERVER_ID` = `id`
WHERE `B_SYSTEM_SERVER_ID` IS NULL OR `B_SYSTEM_SERVER_ID` = '';

-- ---------- 原 V3_7：资源池发单产品类型 ----------
ALTER TABLE `RESOURCE_POOL_REQUEST`
    ADD COLUMN `PRODUCT_TYPE` int DEFAULT NULL COMMENT '产品类型：1云服务器 2独立服务器，与订单一致' AFTER `OPERATION_TYPE`;

-- ---------- 原 V3_8：独立操作系统名称允许重复 ----------
SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_class' AND INDEX_NAME = 'OS_CLASS_NAME') > 0,
    'ALTER TABLE `independent_os_class` DROP INDEX `OS_CLASS_NAME`',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_version' AND INDEX_NAME = 'OS_NAME') > 0,
    'ALTER TABLE `independent_os_version` DROP INDEX `OS_NAME`',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ---------- os_version：移除与实体不一致的历史列（按列存在则删）----------
SET @sql = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND UPPER(COLUMN_NAME) = 'RESOURCE_TYPE') > 0,
  'ALTER TABLE `os_version` DROP COLUMN `RESOURCE_TYPE`', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND UPPER(COLUMN_NAME) = 'INSTANCE_AVAILABILITY_CPU') > 0,
  'ALTER TABLE `os_version` DROP COLUMN `INSTANCE_AVAILABILITY_CPU`', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND UPPER(COLUMN_NAME) = 'INSTANCE_AVAILABILITY_RAM') > 0,
  'ALTER TABLE `os_version` DROP COLUMN `INSTANCE_AVAILABILITY_RAM`', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND UPPER(COLUMN_NAME) = 'SYSTEM_DISK_SIZE') > 0,
  'ALTER TABLE `os_version` DROP COLUMN `SYSTEM_DISK_SIZE`', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND UPPER(COLUMN_NAME) = 'RESOURCE_PROVIDER_NAME') > 0,
  'ALTER TABLE `os_version` DROP COLUMN `RESOURCE_PROVIDER_NAME`', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ---------- independent_os_version：移除历史 resource_type（与实体 TYPE 并存时删旧列）----------
SET @sql = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_version' AND LOWER(COLUMN_NAME) = 'resource_type') > 0,
  'ALTER TABLE `independent_os_version` DROP COLUMN `resource_type`', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ---------- 系统版本号（与 V3_0、V3_4 等迁移一致，写入 dev_config）----------
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.2.3' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
