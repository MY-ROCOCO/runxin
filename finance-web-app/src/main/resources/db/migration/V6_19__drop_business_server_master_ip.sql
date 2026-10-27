-- 云服务器/自动任务不再冗余存储宿主机IP、服务器IP，展示均按关联主数据查询
SET @drop_business_server_master_ip = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'business_server'
     AND COLUMN_NAME = 'MASTER_IP') > 0,
  'ALTER TABLE `business_server` DROP COLUMN `MASTER_IP`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_business_server_master_ip;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_auto_task_master_ip = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'auto_task'
     AND LOWER(COLUMN_NAME) = 'master_ip') > 0,
  'ALTER TABLE `auto_task` DROP COLUMN `master_ip`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_auto_task_master_ip;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_auto_task_server_ip = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'auto_task'
     AND LOWER(COLUMN_NAME) = 'server_ip') > 0,
  'ALTER TABLE `auto_task` DROP COLUMN `server_ip`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_auto_task_server_ip;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_independent_work_independent_ip = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'independent_work'
     AND LOWER(COLUMN_NAME) = 'independent_ip') > 0,
  'ALTER TABLE `independent_work` DROP COLUMN `independent_ip`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_independent_work_independent_ip;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 云服务器线路只保留显示状态，去掉允许购买字段
SET @drop_server_local_line_buy_status = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'server_local_line'
     AND COLUMN_NAME = 'BUY_STATUS') > 0,
  'ALTER TABLE `server_local_line` DROP COLUMN `BUY_STATUS`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_server_local_line_buy_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
