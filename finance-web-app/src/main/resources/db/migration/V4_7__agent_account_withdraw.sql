ALTER TABLE `agent_info`
ADD COLUMN `balance` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '代理可提现余额' AFTER `status`;

CREATE TABLE IF NOT EXISTS `agent_withdraw_record` (
  `id` varchar(20) NOT NULL,
  `agent_id` varchar(20) NOT NULL COMMENT '代理主体ID',
  `withdraw_no` varchar(64) NOT NULL COMMENT '提现单号',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `status` varchar(32) NOT NULL COMMENT '状态 PENDING/APPROVED/REJECTED',
  `account_name` varchar(64) DEFAULT NULL COMMENT '收款人',
  `account_no` varchar(128) DEFAULT NULL COMMENT '收款账号',
  `bank_name` varchar(128) DEFAULT NULL COMMENT '开户行',
  `remark` varchar(255) DEFAULT NULL COMMENT '申请备注',
  `review_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_user` varchar(64) DEFAULT NULL COMMENT '审核人',
  `delete_flag` varchar(32) DEFAULT 'NOT_DELETE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_withdraw_no` (`withdraw_no`),
  KEY `idx_agent_withdraw_agent_id` (`agent_id`),
  KEY `idx_agent_withdraw_status` (`status`),
  KEY `idx_agent_withdraw_create_time` (`create_time`)
) ENGINE=InnoDB COMMENT='代理提现记录表';
