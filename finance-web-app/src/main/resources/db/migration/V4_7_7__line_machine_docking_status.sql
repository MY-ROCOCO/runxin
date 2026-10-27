-- 为云服务器线路与独立服务器机型补充是否可对接字段
-- 默认值为 1，兼容现有数据

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'server_local_line'
       AND COLUMN_NAME = 'DOCKING_STATUS') > 0,
    'SELECT 1',
    'ALTER TABLE `server_local_line` ADD COLUMN `DOCKING_STATUS` int(1) DEFAULT 1 COMMENT ''是否可对接: 0 否 1 是'' AFTER `RESOURCE_POOL_ID`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'independent_machine'
       AND COLUMN_NAME = 'DOCKING_STATUS') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_machine` ADD COLUMN `DOCKING_STATUS` int(1) DEFAULT 1 COMMENT ''是否可对接: 0 否 1 是'' AFTER `RESOURCE_POOL_ID`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
