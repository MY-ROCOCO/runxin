-- 独立服务器资源池：与 BUSINESS_SERVER.UPSTREAM_SERVER_ID 语义一致，存 A 侧实例主键，供 B 调上游退款/运维等
ALTER TABLE `business_independent`
    ADD COLUMN `UPSTREAM_SERVER_ID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上游独立服务器ID（资源池：与 A 系统实例主键一致）' AFTER `RESOURCE_POOL_ID`;

ALTER TABLE `business_independent`
    ADD INDEX `IDX_UPSTREAM_SERVER_ID` (`UPSTREAM_SERVER_ID`) USING BTREE COMMENT '上游服务器ID索引';
