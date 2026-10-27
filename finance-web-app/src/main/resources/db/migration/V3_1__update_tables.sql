-- =============================================================================
-- V3_1：资源池、API 密钥、B 系统对接、资源变更日志等
-- （合并：会员API密钥表、资源池表、资源池请求记录表、为所有相关表添加资源池ID、云服务器上游ID）
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. API 密钥表（原 会员API密钥表.sql）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `API_KEY` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `MEMBER_NO` bigint NOT NULL COMMENT '会员编号（关联client_user.id）',
  `API_KEY` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API密钥（UUID）',
  `API_SECRET` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API密钥Secret（加密存储）',
  `NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密钥名称/备注，如「B平台生产环境」',
  `STATUS` int DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `UK_API_KEY` (`API_KEY`) USING BTREE COMMENT 'API密钥唯一索引',
  KEY `IDX_MEMBER_NO` (`MEMBER_NO`) USING BTREE COMMENT '会员编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='API密钥表';

-- -----------------------------------------------------------------------------
-- 2. 资源池表（原 资源池表.sql）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `RESOURCE_POOL` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `POOL_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源池类型，如：Reckonkvm',
  `API_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API请求地址',
  `API_KEY` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API请求密钥',
  `API_SECRET` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API密钥Secret（加密存储）',
  `NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池名称/备注',
  `STATUS` int DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `IDX_POOL_TYPE` (`POOL_TYPE`) USING BTREE COMMENT '资源池类型索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资源池管理表';

-- -----------------------------------------------------------------------------
-- 3. 资源池请求表（原 资源池请求记录表.sql + 同步/操作类型字段）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `RESOURCE_POOL_REQUEST` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `ORDER_NO` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '订单号',
  `MEMBER_NO` bigint DEFAULT NULL COMMENT '会员编号',
  `REQUEST_PARAMS` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数（JSON格式）',
  `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID',
  `REQUEST_STATUS` int DEFAULT 0 COMMENT '请求状态：0-待发送，1-已发送，2-成功，3-失败',
  `UPSTREAM_TASK_ID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上游任务ID',
  `CALLBACK_DATA` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '回调数据（JSON格式）',
  `SYNC_STATUS` varchar(20) DEFAULT 'PENDING' COMMENT '同步状态：PENDING-待同步/SYNCED-已同步/FAILED-同步失败',
  `LAST_SYNC_TIME` datetime DEFAULT NULL COMMENT '最后同步时间',
  `UPSTREAM_DATA_VERSION` varchar(64) DEFAULT NULL COMMENT '上游数据版本标识（变更日志 ID）',
  `OPERATION_TYPE` varchar(32) DEFAULT 'CREATE' COMMENT '操作类型：CREATE/RENEW/UPGRADE_DISK/UPGRADE_BANDWIDTH/UPGRADE_CPU_RAM/UPGRADE_DEFENSE/DELETE',
  `A_SERVER_ID` varchar(64) DEFAULT NULL COMMENT 'A系统服务器ID（续费/升级/删除时使用）',
  `MONTHS` int DEFAULT NULL COMMENT '续费月数（RENEW时使用）',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `IDX_ORDER_NO` (`ORDER_NO`) USING BTREE COMMENT '订单号索引',
  KEY `IDX_MEMBER_NO` (`MEMBER_NO`) USING BTREE COMMENT '会员编号索引',
  KEY `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引',
  KEY `IDX_REQUEST_STATUS` (`REQUEST_STATUS`) USING BTREE COMMENT '请求状态索引',
  KEY `IDX_UPSTREAM_TASK_ID` (`UPSTREAM_TASK_ID`) USING BTREE COMMENT '上游任务ID索引',
  KEY `IDX_SYNC_STATUS` (`SYNC_STATUS`, `LAST_SYNC_TIME`) COMMENT '同步状态查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资源池请求表';

-- -----------------------------------------------------------------------------
-- 4. 为所有相关表添加资源池 TYPE、RESOURCE_POOL_ID（原 为所有相关表添加资源池ID字段的SQL语句.sql）
-- 列已存在时会报错，可忽略后单独处理
-- -----------------------------------------------------------------------------

-- 1. 云服务器线路表 (SERVER_LOCAL_LINE)
ALTER TABLE `server_local_line`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `LINE_NAME`;

ALTER TABLE `server_local_line`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当线路来自资源池时，关联resource_pool.id）' AFTER `DISCOUNT_TEMPLATE_ID`;

ALTER TABLE `server_local_line`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 2. 独立服务器机型表 (INDEPENDENT_MACHINE) — TYPE 已在 V1_0 存在，仅增加 RESOURCE_POOL_ID
ALTER TABLE `independent_machine`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当type=1资源池时，关联resource_pool.id）' AFTER `TYPE`;

ALTER TABLE `independent_machine`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 3. 独立服务器业务表 (BUSINESS_INDEPENDENT) — type 列已在 V1_2 存在，仅增加 RESOURCE_POOL_ID
ALTER TABLE `business_independent`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当type=1资源池时，关联resource_pool.id）' AFTER `type`;

ALTER TABLE `business_independent`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 4. 云服务器业务表 (BUSINESS_SERVER)
ALTER TABLE `business_server`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `LINE_NAME`;

ALTER TABLE `business_server`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当type=1资源池时，关联resource_pool.id）' AFTER `TYPE`;

ALTER TABLE `business_server`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 5. 云服务器地域表 (SERVER_REGION)
ALTER TABLE `server_region`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `REGION_NAME`;

ALTER TABLE `server_region`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当地域来自资源池时，关联resource_pool.id）' AFTER `STATUS`;

ALTER TABLE `server_region`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 6. 云服务器可用区表 (SERVER_AVAILABLE_REGION)
ALTER TABLE `server_available_region`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `AVAILABLE_NAME`;

ALTER TABLE `server_available_region`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当可用区来自资源池时，关联resource_pool.id）' AFTER `STATUS`;

ALTER TABLE `server_available_region`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 7. 独立服务器地域表 (INDEPENDENT_REGION)
ALTER TABLE `independent_region`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `NAME`;

ALTER TABLE `independent_region`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当地域来自资源池时，关联resource_pool.id）' AFTER `SORT`;

ALTER TABLE `independent_region`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 8. 独立服务器线路表 (INDEPENDENT_LINE)
ALTER TABLE `independent_line`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `NAME`;

ALTER TABLE `independent_line`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当线路来自资源池时，关联resource_pool.id）' AFTER `WEIGHT`;

ALTER TABLE `independent_line`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 9. 云服务器订单配置表 (order_server_config)
ALTER TABLE `order_server_config`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `LINE_ID`;

ALTER TABLE `order_server_config`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当type=1资源池时，关联resource_pool.id）' AFTER `TYPE`;

ALTER TABLE `order_server_config`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 10. 独立服务器订单配置表 (order_independent_config) — type 列已在 V1_2 存在，仅增加 RESOURCE_POOL_ID
ALTER TABLE `order_independent_config`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当type=1资源池时，关联resource_pool.id）' AFTER `type`;

ALTER TABLE `order_independent_config`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- 11. SKU 表 (server_line_sku)
ALTER TABLE `server_line_sku`
ADD COLUMN `TYPE` int DEFAULT 0 COMMENT '资源类型: 0 自生产 1 资源池' AFTER `LINE_ID`;

ALTER TABLE `server_line_sku`
ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源池ID（当type=1资源池时，关联resource_pool.id）' AFTER `TYPE`;

ALTER TABLE `server_line_sku`
ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE COMMENT '资源池ID索引';

-- -----------------------------------------------------------------------------
-- 5. 云服务器业务表上游云服务器 ID（原 云服务器表增加上游服务器ID字段.sql）
-- -----------------------------------------------------------------------------
ALTER TABLE `business_server`
ADD COLUMN `UPSTREAM_SERVER_ID` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上游云服务器ID（当type=1资源池时，上游系统返回的云服务器ID）' AFTER `RESOURCE_POOL_ID`;

ALTER TABLE `business_server`
ADD INDEX `IDX_UPSTREAM_SERVER_ID` (`UPSTREAM_SERVER_ID`) USING BTREE COMMENT '上游云服务器ID索引';

-- -----------------------------------------------------------------------------
-- 6. 订单表、业务服务器 B 系统字段
-- -----------------------------------------------------------------------------
-- 订单表新增 B 系统服务器 ID 字段
ALTER TABLE `orders`
ADD COLUMN `B_SYSTEM_SERVER_IDS` TEXT COMMENT '下游代理系统服务器 ID 数组（JSON 格式）';

-- 会员服务器表新增 B 系统服务器 ID 字段
ALTER TABLE `business_server`
ADD COLUMN `B_SYSTEM_SERVER_ID` VARCHAR(64) COMMENT '下游代理系统服务器 ID';

-- 添加索引提高查询效率
ALTER TABLE `business_server`
ADD INDEX `IDX_B_SYSTEM_SERVER_ID` (`B_SYSTEM_SERVER_ID`);

-- -----------------------------------------------------------------------------
-- 7. 资源变更日志表
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `RESOURCE_CHANGE_LOG` (
    `ID` VARCHAR(32) PRIMARY KEY COMMENT '主键 ID',
    `ORDER_NO` VARCHAR(64) NOT NULL COMMENT '订单号',
    `MEMBER_NO` BIGINT NOT NULL COMMENT '会员编号（用于区分下游）',
    `B_SYSTEM_SERVER_ID` VARCHAR(64) NULL COMMENT 'B 系统服务器ID',
    `A_SYSTEM_SERVER_ID` VARCHAR(64) NULL COMMENT 'A 系统服务器ID',
    `CHANGE_TYPE` VARCHAR(20) NOT NULL COMMENT '变更类型：CREATE-创建/UPDATE-更新/DELETE-删除',
    `DATA_SNAPSHOT` TEXT COMMENT '数据快照（完整的资源数据 JSON）',
    `PROCESSED` TINYINT DEFAULT 0 COMMENT '是否已被消费：0-未处理/1-已处理',
    `PROCESS_TIME` DATETIME NULL COMMENT '处理时间',
    `CREATE_TIME` DATETIME NOT NULL COMMENT '变更时间',
    `UPDATE_TIME` DATETIME NULL COMMENT '更新时间',
    INDEX IDX_MEMBER_PROCESSED (`MEMBER_NO`, `PROCESSED`, `CREATE_TIME`) COMMENT '会员编号 + 处理状态索引',
    INDEX IDX_ORDER_NO (`ORDER_NO`) COMMENT '订单号索引',
    INDEX IDX_CREATE_TIME (`CREATE_TIME`) COMMENT '变更时间索引',
    INDEX IDX_B_SYSTEM_SERVER_ID (`B_SYSTEM_SERVER_ID`) COMMENT 'B 系统服务器ID 索引',
    INDEX IDX_A_SYSTEM_SERVER_ID (`A_SYSTEM_SERVER_ID`) COMMENT 'A 系统服务器ID 索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源变更日志表';

ALTER TABLE `RESOURCE_CHANGE_LOG`
    ADD COLUMN `DELETE_FLAG` varchar(50) NULL COMMENT '删除标志' AFTER `PROCESS_TIME`,
ADD COLUMN `CREATE_USER` varchar(60) NULL COMMENT '创建用户' AFTER `DELETE_FLAG`,
ADD COLUMN `UPDATE_USER` varchar(60) NULL COMMENT '修改用户' AFTER `CREATE_TIME`;
