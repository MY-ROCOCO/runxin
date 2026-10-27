-- 为云服务器线路与云服务器订单配置补充 IP 数量相关字段
-- 兼容已有数据库，列存在时跳过

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'server_local_line'
       AND COLUMN_NAME = 'IP_MIN_SIZE') > 0,
    'SELECT 1',
    'ALTER TABLE `server_local_line` ADD COLUMN `IP_MIN_SIZE` int DEFAULT 0 COMMENT ''IP数量最小'' AFTER `APPEND_IP_PRICE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'server_local_line'
       AND COLUMN_NAME = 'IP_MAX_SIZE') > 0,
    'SELECT 1',
    'ALTER TABLE `server_local_line` ADD COLUMN `IP_MAX_SIZE` int DEFAULT 0 COMMENT ''IP数量最大'' AFTER `IP_MIN_SIZE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'server_local_line'
       AND COLUMN_NAME = 'IP_FREE_SIZE') > 0,
    'SELECT 1',
    'ALTER TABLE `server_local_line` ADD COLUMN `IP_FREE_SIZE` int DEFAULT 0 COMMENT ''IP赠送数量'' AFTER `IP_MAX_SIZE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'order_server_config'
       AND COLUMN_NAME = 'IP_NUM') > 0,
    'SELECT 1',
    'ALTER TABLE `order_server_config` ADD COLUMN `IP_NUM` int DEFAULT 1 COMMENT ''IP数量'' AFTER `BANDWIDTH_FREE_SIZE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'business_server'
       AND COLUMN_NAME = 'IP_NUM') > 0,
    'SELECT 1',
    'ALTER TABLE `business_server` ADD COLUMN `IP_NUM` int DEFAULT 1 COMMENT ''IP数量'' AFTER `DATA_DISKS`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'order_server_config'
       AND COLUMN_NAME = 'IP_FREE_SIZE') > 0,
    'SELECT 1',
    'ALTER TABLE `order_server_config` ADD COLUMN `IP_FREE_SIZE` int DEFAULT 0 COMMENT ''赠送IP数量'' AFTER `IP_NUM`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'order_server_config'
       AND COLUMN_NAME = 'IP_MONTH_PRICE') > 0,
    'SELECT 1',
    'ALTER TABLE `order_server_config` ADD COLUMN `IP_MONTH_PRICE` decimal(10,2) DEFAULT 0.00 COMMENT ''IP月单价'' AFTER `IP_FREE_SIZE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
