ALTER TABLE `public_network`
    DROP INDEX `idx_public_network_query`,
    DROP COLUMN `BIND_TYPE`,
    ADD KEY `idx_public_network_query` (`DELETE_FLAG`, `ENABLE`, `MASTER_ID`, `LINE_ID`) USING BTREE;

ALTER TABLE `private_network`
    DROP INDEX `idx_public_network_query`,
    DROP COLUMN `BIND_TYPE`,
    ADD KEY `idx_public_network_query` (`DELETE_FLAG`, `ENABLE`, `MASTER_ID`, `LINE_ID`) USING BTREE;

ALTER TABLE `nat_network`
    DROP INDEX `idx_public_network_query`,
    DROP COLUMN `BIND_TYPE`,
    ADD KEY `idx_public_network_query` (`DELETE_FLAG`, `ENABLE`, `MASTER_ID`, `LINE_ID`) USING BTREE;

ALTER TABLE `server_master`
    DROP COLUMN `POOL_IP_SUPPORT`;

UPDATE `dev_config` SET `CONFIG_VALUE` = '1.6.7' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
