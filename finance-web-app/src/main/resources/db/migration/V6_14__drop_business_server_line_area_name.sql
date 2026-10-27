UPDATE `dev_config` SET `CONFIG_VALUE` = '1.7.0' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';

SET @drop_business_server_area_name = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'business_server'
     AND COLUMN_NAME = 'AREA_NAME') > 0,
  'ALTER TABLE `business_server` DROP COLUMN `AREA_NAME`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_business_server_area_name;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_business_server_line_name = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'business_server'
     AND COLUMN_NAME = 'LINE_NAME') > 0,
  'ALTER TABLE `business_server` DROP COLUMN `LINE_NAME`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_business_server_line_name;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
