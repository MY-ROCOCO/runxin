ALTER TABLE `business_server`
    ADD COLUMN `LOCK_REASON` varchar(500) DEFAULT NULL COMMENT '锁定原因' AFTER `SERVER_STATUS`;

ALTER TABLE `business_independent`
    ADD COLUMN `LOCK_REASON` varchar(500) DEFAULT NULL COMMENT '锁定原因' AFTER `server_status`;

-- 版本号 1.4.7
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.4.7' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
