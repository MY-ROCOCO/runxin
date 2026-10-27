UPDATE `dev_config` SET `CONFIG_VALUE` = '1.7.5' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';

-- Tg通知开通：1开启 0关闭
INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
SELECT CAST((UNIX_TIMESTAMP(NOW(3)) * 1000 + FLOOR(RAND() * 1000)) AS CHAR), 'TELEGRAM_NOTIFICATION_ENABLE', '0', 'SYS_BASE', 'Tg通知开通：1开启 0关闭', '3', NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `dev_config` WHERE `CONFIG_KEY` = 'TELEGRAM_NOTIFICATION_ENABLE');

-- 操作系统操作可用性（创建/重装）未接入业务，删除无用字段
SET @drop_os_version_operation_availability = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'os_version'
     AND COLUMN_NAME = 'OPERATION_AVAILABILITY') > 0,
  'ALTER TABLE `os_version` DROP COLUMN `OPERATION_AVAILABILITY`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_os_version_operation_availability;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 云服务器线路无用字段：线路查看权限、续费降配状态
SET @drop_server_local_line_line_auth = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'server_local_line'
     AND COLUMN_NAME = 'LINE_AUTH') > 0,
  'ALTER TABLE `server_local_line` DROP COLUMN `LINE_AUTH`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_server_local_line_line_auth;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_server_local_line_renew_degrade_status = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'server_local_line'
     AND COLUMN_NAME = 'RENEW_DEGRADE_STATUS') > 0,
  'ALTER TABLE `server_local_line` DROP COLUMN `RENEW_DEGRADE_STATUS`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_server_local_line_renew_degrade_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
