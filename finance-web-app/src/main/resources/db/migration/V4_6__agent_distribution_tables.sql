UPDATE `dev_config` SET `CONFIG_VALUE` = '1.3.0' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';

CREATE TABLE IF NOT EXISTS `agent_info` (
  `id` varchar(20) NOT NULL,
  `agent_code` varchar(64) NOT NULL COMMENT '代理编码',
  `agent_name` varchar(128) NOT NULL COMMENT '代理名称',
  `contact_name` varchar(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `status` varchar(32) NOT NULL COMMENT '状态 ENABLE/DISABLED',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `delete_flag` varchar(32) DEFAULT 'NOT_DELETE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_code` (`agent_code`)
) ENGINE=InnoDB COMMENT='代理主体表';

CREATE TABLE IF NOT EXISTS `agent_user` (
  `id` varchar(20) NOT NULL,
  `agent_id` varchar(20) NOT NULL COMMENT '代理主体ID',
  `login_account` varchar(64) NOT NULL COMMENT '登录账号',
  `password` varchar(255) NOT NULL COMMENT '密码哈希',
  `user_status` varchar(32) NOT NULL COMMENT '状态 ENABLE/DISABLED',
  `last_login_ip` varchar(64) DEFAULT NULL,
  `last_login_address` varchar(255) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `last_login_device` varchar(64) DEFAULT NULL,
  `latest_login_ip` varchar(64) DEFAULT NULL,
  `latest_login_address` varchar(255) DEFAULT NULL,
  `latest_login_time` datetime DEFAULT NULL,
  `latest_login_device` varchar(64) DEFAULT NULL,
  `delete_flag` varchar(32) DEFAULT 'NOT_DELETE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_user_login_account` (`login_account`),
  KEY `idx_agent_user_agent_id` (`agent_id`)
) ENGINE=InnoDB COMMENT='代理登录账号表';

CREATE TABLE IF NOT EXISTS `agent_member_bind` (
  `id` varchar(20) NOT NULL,
  `member_no` bigint NOT NULL COMMENT '会员编号',
  `agent_id` varchar(20) NOT NULL COMMENT '代理主体ID',
  `bind_source` varchar(32) NOT NULL COMMENT '绑定来源 REGISTER_URL/ADMIN_MANUAL',
  `bind_time` datetime NOT NULL COMMENT '绑定时间',
  `status` varchar(32) NOT NULL COMMENT '状态 ENABLE/DISABLED',
  `delete_flag` varchar(32) DEFAULT 'NOT_DELETE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_agent_member_bind_member_no` (`member_no`),
  KEY `idx_agent_member_bind_agent_id` (`agent_id`),
  KEY `idx_agent_member_bind_status` (`status`)
) ENGINE=InnoDB COMMENT='会员绑定代理关系表';

CREATE TABLE IF NOT EXISTS `agent_product_ratio` (
  `id` varchar(20) NOT NULL,
  `agent_id` varchar(20) NOT NULL COMMENT '代理主体ID',
  `product_type` int NOT NULL COMMENT '产品类型',
  `ratio` decimal(5,2) NOT NULL COMMENT '分成比例',
  `status` varchar(32) NOT NULL COMMENT '状态 ENABLE/DISABLED',
  `effective_time` datetime DEFAULT NULL COMMENT '生效时间',
  `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
  `delete_flag` varchar(32) DEFAULT 'NOT_DELETE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_product_ratio` (`agent_id`, `product_type`)
) ENGINE=InnoDB COMMENT='代理产品分成比例表';

CREATE TABLE IF NOT EXISTS `agent_commission_record` (
  `id` varchar(20) NOT NULL,
  `agent_id` varchar(20) NOT NULL COMMENT '代理主体ID',
  `member_no` bigint NOT NULL COMMENT '会员编号',
  `source_order_no` varchar(64) NOT NULL COMMENT '来源订单号',
  `product_type` int NOT NULL COMMENT '产品类型',
  `base_amount` decimal(10,2) NOT NULL COMMENT '计佣基数或退款基数',
  `ratio` decimal(5,2) NOT NULL COMMENT '分成比例',
  `commission_amount` decimal(10,2) NOT NULL COMMENT '分成金额，退款冲减为负数',
  `record_type` varchar(32) NOT NULL COMMENT 'COMMISSION/REFUND_DEDUCT',
  `record_status` varchar(32) NOT NULL COMMENT '状态 EFFECTIVE',
  `related_record_id` varchar(20) DEFAULT NULL COMMENT '关联正向分成记录ID',
  `source_refund_no` varchar(64) DEFAULT NULL COMMENT '退款批次号',
  `idempotent_key` varchar(128) NOT NULL COMMENT '幂等键',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `delete_flag` varchar(32) DEFAULT 'NOT_DELETE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_commission_idempotent_key` (`idempotent_key`),
  KEY `idx_agent_commission_agent_id_time` (`agent_id`, `create_time`),
  KEY `idx_agent_commission_order_no` (`source_order_no`),
  KEY `idx_agent_commission_member_no` (`member_no`)
) ENGINE=InnoDB COMMENT='代理分成流水表';
