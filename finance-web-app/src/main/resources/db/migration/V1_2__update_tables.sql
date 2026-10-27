CREATE TABLE `business_independent`  (
     `id` varchar(255)  NOT NULL COMMENT '服务器标识',
     `region_id` varchar(60)  NULL DEFAULT NULL COMMENT '地域ID',
     `region_name` varchar(100)  NULL DEFAULT NULL COMMENT '区域名称',
     `line_id` varchar(60)  NULL DEFAULT NULL COMMENT '线路ID',
     `line_name` varchar(100)  NULL DEFAULT NULL COMMENT '线路名称',
     `machine_id` varchar(255)  NULL DEFAULT NULL COMMENT '机型ID',
     `machine_name` varchar(255)  NULL DEFAULT NULL COMMENT '机型名称',
     `server_name` varchar(255)  NULL DEFAULT NULL COMMENT '服务器名称',
     `port` int NULL DEFAULT NULL COMMENT '远程端口',
     `server_ip` varchar(255)  NULL DEFAULT NULL COMMENT '远程IP',
     `system_user_name` varchar(255)  NULL DEFAULT NULL COMMENT '远程登录用户名',
     `system_password` varchar(255)  NULL DEFAULT NULL COMMENT '系统密码',
     `remark` varchar(255)  NULL DEFAULT NULL COMMENT '备注',
     `server_status` varchar(50)  NULL DEFAULT NULL COMMENT '主机状态: CREATING 创建中, RUNNING 运行中, STOP 已关机, LOCKED 锁定, REINSTALLING 系统重装中',
     `business_status` int NULL DEFAULT NULL COMMENT '业务状态: 1: 正常, -1: 过期',
     `type` int NULL DEFAULT NULL COMMENT '资源类型: 0 自生产 1 资源池',
     `automation` int NULL DEFAULT NULL COMMENT '自动化: 0 库存自动化 1 无库存人工服务机型',
     `cpu_info` varchar(255)  NULL DEFAULT NULL COMMENT 'cpu规格信息',
     `ram_info` varchar(255)  NULL DEFAULT NULL COMMENT '内存规格信息',
     `disks_info` varchar(255)  NULL DEFAULT NULL COMMENT '硬盘规格信息',
     `ip_size` int NULL DEFAULT NULL COMMENT 'ip数量',
     `defense` int NULL DEFAULT NULL COMMENT '防御',
     `bandwidth` int NULL DEFAULT NULL COMMENT '带宽',
     `upgrades` int NULL DEFAULT NULL COMMENT '升级 0不支持 1支持',
     `buy_time` datetime NULL DEFAULT NULL COMMENT '开通时间',
     `end_time` datetime NULL DEFAULT NULL COMMENT '到期日期',
     `delete_time` datetime NULL DEFAULT NULL COMMENT '到期删除时间',
     `member_no` bigint NULL DEFAULT NULL COMMENT '会员编号',
     `renew_auto` int NULL DEFAULT NULL COMMENT '是否自动续费',
     `renew_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '续费价格',
     `member_remark` varchar(255)  NULL DEFAULT NULL COMMENT '用户备注',
     `delete_flag` varchar(255)  NULL DEFAULT NULL COMMENT '删除标志',
     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
     `create_user` varchar(255)  NULL DEFAULT NULL COMMENT '创建人',
     `update_user` varchar(255)  NULL DEFAULT NULL COMMENT '更新人',
     `system_class_id` varchar(60)  NULL DEFAULT NULL COMMENT '操作类型',
     `system_class` varchar(60)  NULL DEFAULT NULL COMMENT '操作类型',
     `system_id` varchar(60)  NULL DEFAULT NULL COMMENT '操作系统ID',
     `system_name` varchar(60)  NULL DEFAULT NULL COMMENT '操作系统',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '独立服务器表' ROW_FORMAT = Dynamic;

CREATE TABLE `business_independent_ip`  (
    `id` varchar(255)  NOT NULL COMMENT '服务器IP标识',
    `server_id` varchar(255)  NULL DEFAULT NULL COMMENT '服务器ID',
    `ip` varchar(255)  NULL DEFAULT NULL COMMENT '服务器IP',
    `type` varchar(50)  NULL DEFAULT NULL COMMENT '服务器IP类型: 公网：PUBLIC 私网：PRIVATE',
    `mask` varchar(255)  NULL DEFAULT NULL COMMENT '子网掩码',
    `gateway` varchar(255)  NULL DEFAULT NULL COMMENT '网关地址',
    `vlan_id` int NULL DEFAULT NULL COMMENT 'VLAN ID',
    `cname` varchar(255)  NULL DEFAULT NULL COMMENT 'IP别名',
    `mac` varchar(255)  NULL DEFAULT NULL COMMENT 'Mac地址',
    `is_main` int NULL DEFAULT NULL COMMENT '是否主IP',
    `delete_flag` varchar(255)  NULL DEFAULT NULL COMMENT '删除标志',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `create_user` varchar(255)  NULL DEFAULT NULL COMMENT '创建人',
    `update_user` varchar(255)  NULL DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '独立服务器IP表' ROW_FORMAT = Dynamic;

ALTER TABLE `independent_line` ADD COLUMN `DISCOUNT_TEMPLATE_ID` varchar(60)  NULL DEFAULT NULL COMMENT '折扣模板ID' AFTER `WEIGHT`;

ALTER TABLE `independent_machine` ADD COLUMN `MACHINE_MONTH_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '机型月付价格' AFTER `AUTOMATION`;

ALTER TABLE `independent_machine` ADD COLUMN `MACHINE_DAY_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '机型日付价格' AFTER `MACHINE_MONTH_PRICE`;

ALTER TABLE `independent_machine` ADD COLUMN `CPU_INFO` varchar(255)  NULL DEFAULT NULL COMMENT 'CPU规格信息' AFTER `MACHINE_DAY_PRICE`;

ALTER TABLE `independent_machine` ADD COLUMN `RAM_INFO` varchar(255)  NULL DEFAULT NULL COMMENT '内存规格信息' AFTER `CPU_INFO`;

ALTER TABLE `independent_machine` ADD COLUMN `DISKS_INFO` varchar(255)  NULL DEFAULT NULL COMMENT '硬盘规格信息' AFTER `RAM_INFO`;

ALTER TABLE `independent_machine` ADD COLUMN `IP_FREE_SIZE` int NULL DEFAULT NULL COMMENT '赠送IP数量' AFTER `DISKS_INFO`;

ALTER TABLE `independent_machine` ADD COLUMN `DEFENSE_FREE_SIZE` int NULL DEFAULT NULL COMMENT '赠送防御数量G' AFTER `IP_FREE_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `BANDWIDTH_FREE_SIZE` int NULL DEFAULT NULL COMMENT '赠送带宽M' AFTER `DEFENSE_FREE_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `BANDWIDTH_MIN_SIZE` int NULL DEFAULT NULL COMMENT '最小带宽' AFTER `BANDWIDTH_FREE_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `BANDWIDTH_MAX_SIZE` int NULL DEFAULT NULL COMMENT '最大带宽' AFTER `BANDWIDTH_MIN_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `IP_MIN_SIZE` int NULL DEFAULT NULL COMMENT '最小IP数' AFTER `BANDWIDTH_MAX_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `IP_MAX_SIZE` int NULL DEFAULT NULL COMMENT '最大IP数' AFTER `IP_MIN_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `DEFENSE_MAX_SIZE` text  NULL COMMENT '防御峰值 50,100,150' AFTER `IP_MAX_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `BANDWIDTH_MONTH_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '每月带宽价格/M' AFTER `DEFENSE_MAX_SIZE`;

ALTER TABLE `independent_machine` ADD COLUMN `BANDWIDTH_DAY_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '每日带宽价格/M' AFTER `BANDWIDTH_MONTH_PRICE`;

ALTER TABLE `independent_machine` ADD COLUMN `DEFENSE_MONTH_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '每月防御价格/G' AFTER `BANDWIDTH_DAY_PRICE`;

ALTER TABLE `independent_machine` ADD COLUMN `DEFENSE_DAY_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '每日防御价格/G' AFTER `DEFENSE_MONTH_PRICE`;

ALTER TABLE `independent_machine` ADD COLUMN `IP_MONTH_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '弹性IP价格/个' AFTER `DEFENSE_DAY_PRICE`;

ALTER TABLE `independent_machine` ADD COLUMN `IP_DAY_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '弹性IP价格/个' AFTER `IP_MONTH_PRICE`;

ALTER TABLE `independent_machine` DROP COLUMN `MONTHLY_PRICE`;

ALTER TABLE `independent_machine` DROP COLUMN `CPU_TYPE`;

ALTER TABLE `independent_machine` DROP COLUMN `INTERNAL_STORAGE`;

ALTER TABLE `independent_machine` DROP COLUMN `HARD_DISK`;

ALTER TABLE `independent_machine` DROP COLUMN `DISK_SIZE`;

ALTER TABLE `independent_machine` DROP COLUMN `DISK_NUMBER`;

ALTER TABLE `independent_machine` DROP COLUMN `DISK_TYPE`;

ALTER TABLE `independent_machine` DROP COLUMN `IP_QUANTITY`;

ALTER TABLE `independent_machine` DROP COLUMN `DEFENSE`;

ALTER TABLE `independent_machine` DROP COLUMN `BANDWIDTH`;

ALTER TABLE `independent_machine` DROP COLUMN `BANDWIDTH_MIN`;

ALTER TABLE `independent_machine` DROP COLUMN `BANDWIDTH_MAX`;

ALTER TABLE `independent_machine` DROP COLUMN `IP_QUANTITY_MIN`;

ALTER TABLE `independent_machine` DROP COLUMN `IP_QUANTITY_MAX`;

ALTER TABLE `independent_machine` DROP COLUMN `DEFENSE_MAX`;

ALTER TABLE `independent_machine` DROP COLUMN `BANDWIDTH_PRICE`;

ALTER TABLE `independent_machine` DROP COLUMN `DEFENSE_PRICE`;

ALTER TABLE `independent_machine` DROP COLUMN `ELASTICITY_IP_PRICE`;

ALTER TABLE `independent_os_version` ADD COLUMN `os_class_name` varchar(255)  NULL DEFAULT NULL COMMENT '操作系统类型名称' AFTER `OS_CLASS_ID`;

CREATE TABLE `independent_work`  (
     `id` varchar(60)  NULL DEFAULT NULL COMMENT '独立服务器人工处理工单ID',
     `member_no` bigint NULL DEFAULT NULL COMMENT '会员编号',
     `region_id` varchar(60)  NULL DEFAULT NULL COMMENT '地域ID',
     `line_id` varchar(60)  NULL DEFAULT NULL COMMENT '线路ID',
     `independent_id` varchar(60)  NULL DEFAULT NULL COMMENT '独立服务器ID',
     `independent_ip` varchar(60)  NULL DEFAULT NULL COMMENT '服务器IP',
     `operate_type` int NULL DEFAULT NULL COMMENT '操作类型: 1.创建 2.续费 3.升级 4.重建 5.重装系统 6.开机 7.关机 8.重启 9.重置密码 10.新增IP',
     `business_status` int NULL DEFAULT NULL COMMENT '处理状态: 0.未处理 1.处理中 2.处理完成 3.处理失败',
     `independent_user` varchar(255)  NULL DEFAULT NULL COMMENT '处理人',
     `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
     `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
     `ext_json` text  NULL COMMENT '扩展信息',
     `delete_flag` varchar(255)  NULL DEFAULT NULL COMMENT '删除标记',
     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
     `create_user` varchar(255)  NULL DEFAULT NULL COMMENT '创建人',
     `update_user` varchar(255)  NULL DEFAULT NULL COMMENT '更新人',
     `independent_time` datetime NULL DEFAULT NULL
) ENGINE = InnoDB COMMENT = '独立服务器工单' ROW_FORMAT = Dynamic;

CREATE TABLE `order_independent`  (
      `ID` varchar(60)  NOT NULL COMMENT '主键ID',
      `ORDER_NO` varchar(60)  NULL DEFAULT NULL COMMENT '订单编号',
      `SERVER_ID` varchar(60)  NULL DEFAULT NULL COMMENT '服务器ID',
      `SERVER_NAME` varchar(100)  NULL DEFAULT NULL COMMENT '服务器名称',
      PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB  COMMENT = '独立服务器关联表' ROW_FORMAT = Dynamic;

CREATE TABLE `order_independent_config`  (
     `id` varchar(255)  NULL DEFAULT NULL COMMENT '主键ID',
     `order_no` varchar(255)  NULL DEFAULT NULL COMMENT '订单编号',
     `region_id` varchar(60)  NULL DEFAULT NULL COMMENT '地域ID',
     `line_id` varchar(60)  NULL DEFAULT NULL COMMENT '线路ID',
     `machine_id` varchar(255)  NULL DEFAULT NULL COMMENT '机型ID',
     `machine_name` varchar(255)  NULL DEFAULT NULL COMMENT '机型名称',
     `type` int NULL DEFAULT NULL COMMENT '资源类型  0:自生产 1:资源池',
     `automation` int NULL DEFAULT NULL COMMENT '自动化  0库存自动化 1无库存人工服务机型',
     `machine_month_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '机型月付价格',
     `cpu_info` varchar(255)  NULL DEFAULT NULL COMMENT 'cpu规格信息',
     `ram_info` varchar(255)  NULL DEFAULT NULL COMMENT '内存规格信息',
     `disks_info` varchar(255)  NULL DEFAULT NULL COMMENT '硬盘规格信息',
     `ip_size` int NULL DEFAULT NULL COMMENT 'ip数量',
     `ip_free_size` int NULL DEFAULT NULL COMMENT '赠送ip数量',
     `defense` int NULL DEFAULT NULL COMMENT '防御数量 G',
     `defense_free_size` int NULL DEFAULT NULL COMMENT '赠送防御数量 G',
     `bandwidth` int NULL DEFAULT NULL COMMENT '带宽M',
     `bandwidth_free_size` int NULL DEFAULT NULL COMMENT '赠送带宽M',
     `bandwidth_month_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '每月带宽价格/M',
     `defense_month_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '每月防御价格/G',
     `ip_month_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '每月弹性ip价格/个',
     `upgrades` int NULL DEFAULT NULL COMMENT '升级 0不支持  1支持',
     `unit` varchar(10)  NOT NULL COMMENT '单位 时 s，天 d，月 m，年 y',
     `duration` int NULL DEFAULT NULL COMMENT '购买时长',
     `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价',
     `number` int NULL DEFAULT NULL COMMENT '购买数量',
     `total_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '合计',
     `pay_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '实付金额',
     `remark` varchar(255)  NULL DEFAULT NULL COMMENT '备注',
     `system_class_id` varchar(255)  NULL DEFAULT NULL COMMENT '系统分类ID',
     `system_id` varchar(255)  NULL DEFAULT NULL COMMENT '操作系统ID',
     `login_password` varchar(255)  NULL DEFAULT NULL COMMENT '登录密码',
     `renew_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '续费价格'
) ENGINE = InnoDB  COMMENT = '服务器订单配置' ROW_FORMAT = Dynamic;

ALTER TABLE `server_local_line` MODIFY COLUMN `APPEND_IP_DAY_PRICE` decimal(10, 2) NULL DEFAULT NULL COMMENT '弹性IP日价格' AFTER `BANDWIDTH_IN_VALUE`;

ALTER TABLE `server_plus_record` ADD COLUMN `PRODUCT_TYPE` tinyint NOT NULL COMMENT '产品类型： 1 云服务器 2 独立服务器' AFTER `ID`;

update server_plus_record set PRODUCT_TYPE = 1;


INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1970811133925306370', '1954849672690429953', '独立服务器[业务]', NULL, NULL, 'MENU', '1548901111999770525', 'CATALOG', '/business/dedcloudserver', NULL, 'hugeicons:cloud-server', NULL, 'TRUE', 2, '', 'NOT_DELETE', '2025-09-24 19:22:45', '1543837863788879871', '2025-09-24 20:30:37', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1970831789333196801', '1970811133925306370', '独立服务器列表', 'business_dedcloudserver_dedcloudserverlist', NULL, 'MENU', '1548901111999770525', 'MENU', '/business/dedcloudserver/dedcloudserverlist', 'view.business_dedcloudserver_dedcloudserverlist', 'mingcute:menu-fill', NULL, 'TRUE', 1, 'mingcute:menu-fill', 'NOT_DELETE', '2025-09-24 20:44:50', '1543837863788879871', '2025-10-23 17:58:42', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1970832228392939521', '1970811133925306370', '人工服务', 'business_dedcloudserver_ms', NULL, 'MENU', '1548901111999770525', 'MENU', '/business/dedcloudserver/ms', 'view.business_dedcloudserver_ms', 'hugeicons:customer-service-02', NULL, 'TRUE', 2, 'hugeicons:customer-service-02', 'NOT_DELETE', '2025-09-24 20:46:34', '1543837863788879871', NULL, NULL);
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1982686433143468033', '1970811133925306370', '独立云服务器详情', 'business_dedcloudserver_dedcloudedetail', NULL, 'MENU', '1548901111999770525', 'MENU', '/business/dedcloudserver/dedcloudedetail', 'view.business_dedcloudserver_dedcloudedetail', 'tabler:cloud', NULL, 'FALSE', 5, 'tabler:cloud', 'NOT_DELETE', '2025-10-27 13:50:57', '1543837863788879871', NULL, NULL);
