-- 版本号 1.1.6
UPDATE `dev_config` SET CONFIG_VALUE = '1.1.6' WHERE CONFIG_KEY = 'RUNXIN_SYS_VERSION';

-- os_version 增加魔方云系统ID
ALTER TABLE os_version
    ADD COLUMN mfc_system_id VARCHAR(64) NULL DEFAULT NULL COMMENT '魔方云系统ID' AFTER controlled_type;

-- server_master 增加魔方云登录账号、密码、节点ID
ALTER TABLE server_master
    ADD COLUMN mfc_login_account VARCHAR(128) NULL DEFAULT NULL COMMENT '魔方云登陆账号' AFTER api_password,
    ADD COLUMN mfc_login_password VARCHAR(256) NULL DEFAULT NULL COMMENT '魔方云登陆密码' AFTER mfc_login_account,
    ADD COLUMN mfc_node_id VARCHAR(64) NULL DEFAULT NULL COMMENT '魔方云节点ID' AFTER mfc_login_password;

-- controlled_type 转为 JSON 数组格式（仅处理未以 [ 开头的值）
UPDATE os_version SET controlled_type = CONCAT('["', controlled_type, '"]') WHERE controlled_type IS NOT NULL AND controlled_type != '' AND controlled_type NOT LIKE '[%';

-- 云服务器表增加魔方云实例ID字段（创建时魔方云API返回的id）
ALTER TABLE `business_server` ADD COLUMN `MFC_CLOUD_ID` varchar(60) DEFAULT NULL COMMENT '魔方云实例ID' AFTER `MASTER_IP`;
