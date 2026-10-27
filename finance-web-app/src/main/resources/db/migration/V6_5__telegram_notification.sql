ALTER TABLE `tools_notification`
    ADD COLUMN `TELEGRAM_STATUS` tinyint NOT NULL DEFAULT 0 COMMENT 'Telegram通知(0:不存在,1:开启,2:关闭)' AFTER `WX_WORK_TEMPLATE_ID`,
    ADD COLUMN `TELEGRAM_TEMPLATE_ID` varchar(60) NOT NULL DEFAULT '0' COMMENT 'Telegram模板id' AFTER `TELEGRAM_STATUS`;

CREATE TABLE IF NOT EXISTS `tools_telegram_template` (
    `ID` varchar(60) NOT NULL COMMENT '主键ID',
    `TEMPLATE_NAME` varchar(100) DEFAULT NULL COMMENT '模板名称',
    `TEMPLATE_CONTENT` text COMMENT '模板内容',
    `STATUS` int DEFAULT NULL COMMENT '模板状态 (0:未提交审核,1:通过审核,-1:未通过审核)',
    `SCENE` varchar(100) DEFAULT NULL COMMENT '应用场景',
    `SCENE_DESC` varchar(200) DEFAULT NULL COMMENT '应用场景描述',
    `REMARK` varchar(500) DEFAULT NULL COMMENT '备注说明',
    `DELETE_FLAG` varchar(60) DEFAULT NULL COMMENT '删除标志',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
    `CREATE_USER` varchar(60) DEFAULT NULL COMMENT '创建人',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
    `UPDATE_USER` varchar(60) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB COMMENT='Telegram通知模板表';

CREATE TABLE IF NOT EXISTS `dev_telegram` (
    `ID` varchar(60) NOT NULL COMMENT '主键ID',
    `CHAT_ID` varchar(64) NOT NULL COMMENT 'Telegram chat id',
    `CONTENT` longtext COMMENT '消息内容',
    `TEMPLATE_NAME` varchar(100) DEFAULT NULL COMMENT '模板名称',
    `TEMPLATE_PARAM` longtext COMMENT '发送参数',
    `RECEIPT_INFO` longtext COMMENT '回执信息',
    `SEND_STATUS` varchar(32) DEFAULT 'PENDING' COMMENT '发送状态：PENDING-待发送/SENDING-发送中/SUCCESS-发送成功/FAILED-发送失败',
    `SEND_TIME` datetime DEFAULT NULL COMMENT '发送完成时间',
    `FAIL_REASON` longtext COMMENT '失败原因',
    `RETRY_COUNT` int DEFAULT 0 COMMENT '重试次数',
    `EXT_JSON` longtext COMMENT '扩展信息',
    `DELETE_FLAG` varchar(60) DEFAULT NULL COMMENT '删除标志',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
    `CREATE_USER` varchar(60) DEFAULT NULL COMMENT '创建人',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
    `UPDATE_USER` varchar(60) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `idx_dev_telegram_send_status_create_time` (`SEND_STATUS`, `CREATE_TIME`)
) ENGINE=InnoDB COMMENT='Telegram发送记录表';

CREATE TABLE IF NOT EXISTS `dev_telegram_binding` (
    `ID` varchar(60) NOT NULL COMMENT '主键ID',
    `USER_TYPE` varchar(20) NOT NULL COMMENT '用户类型：MEMBER/ADMIN',
    `USER_ID` varchar(60) NOT NULL COMMENT '用户ID',
    `CHAT_ID` varchar(64) NOT NULL COMMENT 'Telegram chat id',
    `TELEGRAM_USER_ID` varchar(64) DEFAULT NULL COMMENT 'Telegram用户ID',
    `TELEGRAM_USERNAME` varchar(128) DEFAULT NULL COMMENT 'Telegram用户名',
    `STATUS` tinyint DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `DELETE_FLAG` varchar(60) DEFAULT NULL COMMENT '删除标志',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
    `CREATE_USER` varchar(60) DEFAULT NULL COMMENT '创建人',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
    `UPDATE_USER` varchar(60) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `uk_dev_telegram_binding_user` (`USER_TYPE`, `USER_ID`),
    UNIQUE KEY `uk_dev_telegram_binding_chat` (`CHAT_ID`)
) ENGINE=InnoDB COMMENT='Telegram绑定关系表';

INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
SELECT CAST((UNIX_TIMESTAMP(NOW(3)) * 1000 + FLOOR(RAND() * 1000)) AS CHAR), 'TELEGRAM_BOT_TOKEN', '', 'TELEGRAM_BOT', 'Telegram机器人Token', '0', NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `dev_config` WHERE `CONFIG_KEY` = 'TELEGRAM_BOT_TOKEN');

INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
SELECT CAST((UNIX_TIMESTAMP(NOW(3)) * 1000 + FLOOR(RAND() * 1000)) AS CHAR), 'TELEGRAM_BOT_USERNAME', '', 'TELEGRAM_BOT', 'Telegram机器人用户名，不含@', '1', NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `dev_config` WHERE `CONFIG_KEY` = 'TELEGRAM_BOT_USERNAME');

INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
SELECT CAST((UNIX_TIMESTAMP(NOW(3)) * 1000 + FLOOR(RAND() * 1000)) AS CHAR), 'TELEGRAM_WEBHOOK_SECRET', '', 'TELEGRAM_BOT', 'Telegram Webhook secret token', '2', NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `dev_config` WHERE `CONFIG_KEY` = 'TELEGRAM_WEBHOOK_SECRET');
