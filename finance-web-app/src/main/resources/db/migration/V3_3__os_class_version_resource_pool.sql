-- 操作系统类别、操作系统版本表增加资源池来源标识（与线路等资源表一致：TYPE + RESOURCE_POOL_ID）
-- 可重复执行

-- ========== os_class ==========
SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_class' AND COLUMN_NAME = 'TYPE') > 0,
    'SELECT 1',
    'ALTER TABLE `os_class` ADD COLUMN `TYPE` int DEFAULT 0 COMMENT ''资源类型: 0 自生产 1 资源池'' AFTER `STATUS`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_class' AND COLUMN_NAME = 'RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `os_class` ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT ''资源池ID（来自资源池同步时关联 resource_pool.id）'' AFTER `TYPE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sqlidx = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_class' AND INDEX_NAME = 'IDX_RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `os_class` ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE'
  )
);
PREPARE stmtidx FROM @sqlidx;
EXECUTE stmtidx;
DEALLOCATE PREPARE stmtidx;

-- ========== os_version ==========
SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND COLUMN_NAME = 'TYPE') > 0,
    'SELECT 1',
    'ALTER TABLE `os_version` ADD COLUMN `TYPE` int DEFAULT 0 COMMENT ''资源类型: 0 自生产 1 资源池'' AFTER `SYSTEM_DISK_SIZE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND COLUMN_NAME = 'RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `os_version` ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT ''资源池ID（来自资源池同步时关联 resource_pool.id）'' AFTER `TYPE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sqlidx = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'os_version' AND INDEX_NAME = 'IDX_RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `os_version` ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE'
  )
);
PREPARE stmtidx FROM @sqlidx;
EXECUTE stmtidx;
DEALLOCATE PREPARE stmtidx;

-- ========== independent_os_class ==========
SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_class' AND COLUMN_NAME = 'TYPE') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_os_class` ADD COLUMN `TYPE` int DEFAULT 0 COMMENT ''资源类型: 0 自生产 1 资源池'' AFTER `STATUS`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_class' AND COLUMN_NAME = 'RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_os_class` ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT ''资源池ID（来自资源池同步时关联 resource_pool.id）'' AFTER `TYPE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sqlidx = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_class' AND INDEX_NAME = 'IDX_RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_os_class` ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE'
  )
);
PREPARE stmtidx FROM @sqlidx;
EXECUTE stmtidx;
DEALLOCATE PREPARE stmtidx;

-- ========== independent_os_version ==========
SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_version' AND COLUMN_NAME = 'TYPE') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_os_version` ADD COLUMN `TYPE` int DEFAULT 0 COMMENT ''资源类型: 0 自生产 1 资源池'' AFTER `STATUS`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_version' AND COLUMN_NAME = 'RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_os_version` ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT ''资源池ID（来自资源池同步时关联 resource_pool.id）'' AFTER `TYPE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sqlidx = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'independent_os_version' AND INDEX_NAME = 'IDX_RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_os_version` ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE'
  )
);
PREPARE stmtidx FROM @sqlidx;
EXECUTE stmtidx;
DEALLOCATE PREPARE stmtidx;
