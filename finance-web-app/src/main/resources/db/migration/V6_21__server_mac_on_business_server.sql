UPDATE `dev_config` SET `CONFIG_VALUE` = '1.7.6' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';

-- 云服务器 MAC 统一存放在 BUSINESS_SERVER 主表，空字符串先归一为空，避免唯一索引冲突
UPDATE `business_server` SET `PUBLIC_MAC` = NULL WHERE `PUBLIC_MAC` = '';
UPDATE `business_server` SET `PRIVATE_MAC` = NULL WHERE `PRIVATE_MAC` = '';

-- 历史数据来自 IP 池 MAC，可能存在重复；唯一索引前优先保留正常未删除服务器，其余置空
UPDATE `business_server` bs
JOIN (
  SELECT `ID`
  FROM (
    SELECT `ID`,
           ROW_NUMBER() OVER (
             PARTITION BY `PUBLIC_MAC`
             ORDER BY
               CASE
                 WHEN `DELETE_FLAG` = 'NOT_DELETE' AND `BUSINESS_STATUS` = 1 THEN 0
                 WHEN `DELETE_FLAG` = 'NOT_DELETE' THEN 1
                 ELSE 2
               END,
               `ID`
           ) AS rn
    FROM `business_server`
    WHERE `PUBLIC_MAC` IS NOT NULL
  ) ranked
  WHERE ranked.rn > 1
) dup ON bs.`ID` = dup.`ID`
SET bs.`PUBLIC_MAC` = NULL;

UPDATE `business_server` bs
JOIN (
  SELECT `ID`
  FROM (
    SELECT `ID`,
           ROW_NUMBER() OVER (
             PARTITION BY `PRIVATE_MAC`
             ORDER BY
               CASE
                 WHEN `DELETE_FLAG` = 'NOT_DELETE' AND `BUSINESS_STATUS` = 1 THEN 0
                 WHEN `DELETE_FLAG` = 'NOT_DELETE' THEN 1
                 ELSE 2
               END,
               `ID`
           ) AS rn
    FROM `business_server`
    WHERE `PRIVATE_MAC` IS NOT NULL
  ) ranked
  WHERE ranked.rn > 1
) dup ON bs.`ID` = dup.`ID`
SET bs.`PRIVATE_MAC` = NULL;

SET @add_business_server_public_mac_uk = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'business_server'
     AND INDEX_NAME = 'UK_BUSINESS_SERVER_PUBLIC_MAC') = 0,
  'ALTER TABLE `business_server` ADD UNIQUE KEY `UK_BUSINESS_SERVER_PUBLIC_MAC` (`PUBLIC_MAC`)',
  'SELECT 1'
);
PREPARE stmt FROM @add_business_server_public_mac_uk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_business_server_private_mac_uk = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'business_server'
     AND INDEX_NAME = 'UK_BUSINESS_SERVER_PRIVATE_MAC') = 0,
  'ALTER TABLE `business_server` ADD UNIQUE KEY `UK_BUSINESS_SERVER_PRIVATE_MAC` (`PRIVATE_MAC`)',
  'SELECT 1'
);
PREPARE stmt FROM @add_business_server_private_mac_uk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- IP 明细和 IP 池不再保存 MAC
SET @drop_business_server_ip_mac = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'business_server_ip'
     AND COLUMN_NAME = 'MAC') > 0,
  'ALTER TABLE `business_server_ip` DROP COLUMN `MAC`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_business_server_ip_mac;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_public_network_mac = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'public_network'
     AND COLUMN_NAME = 'MAC') > 0,
  'ALTER TABLE `public_network` DROP COLUMN `MAC`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_public_network_mac;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_private_network_mac = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'private_network'
     AND COLUMN_NAME = 'MAC') > 0,
  'ALTER TABLE `private_network` DROP COLUMN `MAC`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_private_network_mac;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_nat_network_mac = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE()
     AND LOWER(TABLE_NAME) = 'nat_network'
     AND COLUMN_NAME = 'MAC') > 0,
  'ALTER TABLE `nat_network` DROP COLUMN `MAC`',
  'SELECT 1'
);
PREPARE stmt FROM @drop_nat_network_mac;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
