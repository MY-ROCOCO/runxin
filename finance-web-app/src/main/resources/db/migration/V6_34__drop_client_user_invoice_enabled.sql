-- 不再按会员控制发票开通，全员可申请；若 V6_33 已加过该列则删除
SET @exist := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'client_user'
      AND COLUMN_NAME = 'INVOICE_ENABLED'
);
SET @sqlstmt := IF(@exist > 0, 'ALTER TABLE `client_user` DROP COLUMN `INVOICE_ENABLED`', 'SELECT 1');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `dev_config` SET `CONFIG_VALUE` = '1.8.8' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
