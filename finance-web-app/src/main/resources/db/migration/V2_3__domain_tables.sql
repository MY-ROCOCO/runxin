-- ----------------------------
--  Table structure for `domain_tld_price`
-- ----------------------------
DROP TABLE IF EXISTS `domain_tld_price`;
CREATE TABLE `domain_tld_price` (
  `ID` varchar(60) NOT NULL COMMENT '主键',
  `TLD` varchar(32) NOT NULL COMMENT '后缀，如 com、cn',
  `REGISTER_PRICE` decimal(10,2) NOT NULL COMMENT '注册价格',
  `RENEW_PRICE` decimal(10,2) NOT NULL COMMENT '续费价格',
  `TRANSFER_PRICE` decimal(10,2) DEFAULT NULL COMMENT '转入价格',
  `YEARS` varchar(50) DEFAULT NULL COMMENT '可选年限，如 1,2,3,5',
  `STATUS` int NOT NULL DEFAULT 1 COMMENT '状态：0 关闭 1 开启',
  `REMARK` varchar(500) DEFAULT NULL COMMENT '备注',
  `DELETE_FLAG` varchar(255) DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_tld` (`TLD`) USING BTREE
) ENGINE=InnoDB COMMENT='域名后缀价格表';

-- ----------------------------
--  Table structure for `domain_template`
-- ----------------------------
DROP TABLE IF EXISTS `domain_template`;
CREATE TABLE `domain_template` (
  `ID` varchar(60) NOT NULL COMMENT '主键',
  `MEMBER_ID` varchar(60) DEFAULT NULL COMMENT '会员ID，为空表示全局模板',
  `NAME` varchar(100) NOT NULL COMMENT '模板名称',
  `STATUS` int NOT NULL DEFAULT 1 COMMENT '状态：0 禁用 1 启用',
  `AUDIT_STATUS` int NOT NULL DEFAULT 0 COMMENT '审核状态：0 未提交 1 审核中 2 审核通过 3 审核拒绝',
  `AUDIT_REASON` varchar(500) DEFAULT NULL COMMENT '审核失败原因（上游返回的 reason，审核通过时为空）',
  `EXT_JSON` longtext COMMENT '扩展信息，如资料文件ID等',
  `DELETE_FLAG` varchar(255) DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_member_id` (`MEMBER_ID`) USING BTREE
) ENGINE=InnoDB COMMENT='域名模板表';

-- ----------------------------
--  Table structure for `business_domain`
-- ----------------------------
DROP TABLE IF EXISTS `business_domain`;
CREATE TABLE `business_domain` (
  `ID` varchar(60) NOT NULL COMMENT '主键',
  `MEMBER_ID` varchar(60) NOT NULL COMMENT '会员ID',
  `DOMAIN_NAME` varchar(255) NOT NULL COMMENT '域名',
  `TLD` varchar(32) NOT NULL COMMENT '后缀',
  `REGISTRAR` varchar(64) DEFAULT NULL COMMENT '接口商，如 XINWANG',
  `STATUS` int NOT NULL DEFAULT 1 COMMENT '状态',
  `EXPIRE_TIME` datetime DEFAULT NULL COMMENT '到期时间',
  `TEMPLATE_ID` varchar(60) DEFAULT NULL COMMENT '关联域名模板ID',
  `REMARK` varchar(500) DEFAULT NULL COMMENT '备注',
  `REGISTRATION_STATUS` varchar(32) DEFAULT NULL COMMENT '注册状态（新网 createStatus 返回，点击查询后回写）',
  `DELETE_FLAG` varchar(255) DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_member_id` (`MEMBER_ID`) USING BTREE,
  KEY `idx_domain_name` (`DOMAIN_NAME`) USING BTREE,
  KEY `idx_expire_time` (`EXPIRE_TIME`) USING BTREE
) ENGINE=InnoDB COMMENT='业务域名表';

-- ----------------------------
--  域名订单明细：订单下的每个域名及所选模板
-- ----------------------------
DROP TABLE IF EXISTS `domain_order_item`;
CREATE TABLE `domain_order_item` (
  `ID` varchar(60) NOT NULL COMMENT '主键',
  `ORDER_NO` varchar(60) NOT NULL COMMENT '订单编号',
  `DOMAIN_NAME` varchar(255) NOT NULL COMMENT '域名全称',
  `TLD` varchar(32) NOT NULL COMMENT '后缀',
  `TEMPLATE_ID` varchar(60) NOT NULL COMMENT '选中的审核通过模板ID',
  `YEARS` int NOT NULL DEFAULT 1 COMMENT '注册年限',
  `UNIT_PRICE` decimal(10,2) NOT NULL,
  `TOTAL_PRICE` decimal(10,2) NOT NULL,
  `DELETE_FLAG` varchar(255) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_order_no` (`ORDER_NO`) USING BTREE
) ENGINE=InnoDB COMMENT='域名订单明细';

-- ----------------------------
--  上游注册请求记录：支付成功后请求新网注册的状态
-- ----------------------------
DROP TABLE IF EXISTS `domain_registration_request`;
CREATE TABLE `domain_registration_request` (
  `ID` varchar(60) NOT NULL COMMENT '主键',
  `ORDER_NO` varchar(60) NOT NULL COMMENT '订单编号',
  `DOMAIN_NAME` varchar(255) NOT NULL COMMENT '域名',
  `TEMPLATE_ID` varchar(60) NOT NULL COMMENT '模板ID',
  `TEMPLATE_NAME` varchar(255) DEFAULT NULL COMMENT '新网模板名称（请求参数）',
  `YEARS` int NOT NULL DEFAULT 1 COMMENT '注册年限',
  `UPSTREAM_REQUEST_ID` varchar(100) DEFAULT NULL COMMENT '上游请求ID',
  `STATUS` int NOT NULL DEFAULT 0 COMMENT '0 待提交 1 已成功 2 失败',
  `UPSTREAM_CODE` varchar(32) DEFAULT NULL COMMENT '上游返回 code',
  `UPSTREAM_MSG` varchar(500) DEFAULT NULL COMMENT '上游返回 msg',
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_order_no` (`ORDER_NO`) USING BTREE
) ENGINE=InnoDB COMMENT='域名上游注册请求记录';

-- ----------------------------
--  dev_config 域名相关配置键
-- ----------------------------
INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1874806166665565010', 'DOMAIN_REGISTER_PRIMARY_PROVIDER', 'XINWANG', 'DOMAIN_BASE', '域名注册首选接口商，如 XINWANG', 0, NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL);
INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1874806166665565011', 'DOMAIN_XINWANG_APP_ID', '', 'DOMAIN_BASE', '新网接口商 AppID', 0, NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL);
INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1874806166665565012', 'DOMAIN_XINWANG_APP_KEY', '', 'DOMAIN_BASE', '新网接口商 AppKey', 0, NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL);

-- ----------------------------
--  域名相关定时任务：到期检测、模板状态同步、注册状态同步
-- ----------------------------
INSERT INTO `dev_job` (`ID`, `NAME`, `CODE`, `CATEGORY`, `ACTION_CLASS`, `CRON_EXPRESSION`, `JOB_STATUS`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
VALUES
('2031000000000000001', '域名到期检测', 'domain_expiry', 'BIZ', 'com.rxinyun.admin.dev.modular.job.task.DomainExpiryTaskRunner', '0 0 * * * ?', 'RUNNING', 10, NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL),
('2031000000000000002', '域名信息模板状态检测', 'domain_template_status', 'BIZ', 'com.rxinyun.admin.dev.modular.job.task.DomainTemplateStatusTaskRunner', '0 0 * * * ?', 'RUNNING', 11, NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL),
('2031000000000000003', '域名注册状态检测', 'domain_registration_status', 'BIZ', 'com.rxinyun.admin.dev.modular.job.task.DomainRegistrationStatusTaskRunner', '0 */10 * * * ?', 'RUNNING', 12, NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL);

INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2032725901126709250', '1954853652829073409', '域名[生产]', NULL, NULL, 'MENU', '1548901111999770525', 'CATALOG', '/product/domain', NULL, 'mdi:domain', NULL, 'TRUE', '3', 'mdi:domain', 'NOT_DELETE', '2026-03-14 15:49:56', '1543837863788879871', '2026-03-14 16:18:26', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2032726279025111042', '2032725901126709250', '域名价格设置', 'product_domain_price', NULL, 'MENU', '1548901111999770525', 'MENU', '/product/domain/price', 'product/domain_price/index', 'mdi:currency-usd', NULL, 'TRUE', '1', 'mdi:currency-usd', 'NOT_DELETE', '2026-03-14 15:51:26', '1543837863788879871', NULL, NULL);
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2032731557485404161', '1954849672690429953', '域名[业务]', NULL, NULL, 'MENU', '1548901111999770525', 'CATALOG', '/business/domain', NULL, 'mdi:domain', NULL, 'TRUE', '3', 'mdi:domain', 'NOT_DELETE', '2026-03-14 16:12:24', '1543837863788879871', '2026-03-14 16:18:15', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2032731980619374594', '2032731557485404161', '域名列表', 'domain_list', NULL, 'MENU', '1548901111999770525', 'MENU', '/business/domain/list', 'product/domain_list/index', 'mdi:format-list-bulleted', NULL, 'TRUE', '1', 'mdi:format-list-bulleted', 'NOT_DELETE', '2026-03-14 16:14:05', '1543837863788879871', '2026-03-14 16:17:47', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2032835788108226562', '2032731557485404161', '注册列表', 'business_domain_registration_list', NULL, 'MENU', '1548901111999770525', 'MENU', '/business/domain/registration/list', 'business/domain_registration_list/index.vue', 'mdi:clipboard-list', NULL, 'TRUE', '2', 'mdi:clipboard-list', 'NOT_DELETE', '2026-03-14 23:06:35', '1543837863788879871', '2026-03-14 23:12:45', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2032868732776488961', '2032725901126709250', '模板信息', 'product_domain_template', NULL, 'MENU', '1548901111999770525', 'MENU', '/product/domain/template', 'product/domain_template/index.vue', 'mdi:file-document-outline', NULL, 'TRUE', '2', 'mdi:file-document-outline', 'NOT_DELETE', '2026-03-15 01:17:29', '1543837863788879871', NULL, NULL);

-- 更新系统版本号（与 V2_0/V2_1/V2_2 保持一致）
UPDATE `dev_config` SET CONFIG_VALUE = '1.1.5' WHERE CONFIG_KEY = 'RUNXIN_SYS_VERSION';