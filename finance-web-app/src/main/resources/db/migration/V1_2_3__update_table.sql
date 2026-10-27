UPDATE dev_config set CONFIG_VALUE = '1.0.4' WHERE CONFIG_KEY = 'RUNXIN_SYS_VERSION';

CREATE TABLE `tools_notification` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(50) NOT NULL DEFAULT '' COMMENT '通知类型',
  `description` varchar(100) NOT NULL DEFAULT '' COMMENT '通知场景说明',
  `wechat_status` tinyint NOT NULL DEFAULT '0' COMMENT '公众号模板消息（0：不存在，1：开启，2：关闭）',
  `wechat_template_id` int NOT NULL DEFAULT '0' COMMENT '模板消息id',
  `wx_subscribe_status` tinyint NOT NULL DEFAULT '0' COMMENT '小程序订阅消息（0：不存在，1：开启，2：关闭）',
  `wx_subscribe_template_id` int NOT NULL DEFAULT '0' COMMENT '订阅消息id',
  `sms_status` tinyint NOT NULL DEFAULT '0' COMMENT '发送短信（0：不存在，1：开启，2：关闭）',
  `sms_template_id` int NOT NULL DEFAULT '0' COMMENT '短信id',
  `email_status` tinyint NOT NULL DEFAULT '0' COMMENT '发送邮件(0:不存在,1:开启,2:关闭)',
  `email_template_id` tinyint NOT NULL DEFAULT '0' COMMENT '邮件模板id',
  `ding_status` tinyint NOT NULL DEFAULT '0' COMMENT '钉钉通知(0:不存在,1:开启,2:关闭)',
  `ding_template_id` tinyint NOT NULL DEFAULT '0' COMMENT '钉钉模板id',
  `wx_work_status` tinyint NOT NULL DEFAULT '0' COMMENT '企业微信通知(0:不存在, 1:开启,2:关闭)',
  `wx_work_template_id` tinyint NOT NULL DEFAULT '0' COMMENT '企业微信模板id',
  `target_type` tinyint NOT NULL DEFAULT '1' COMMENT '发送目标类型（1：用户，2：管理员）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：1:开启, 0:禁用',
  `delete_flag` varchar(60) DEFAULT NULL COMMENT '删除标志',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(60) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` varchar(60) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 COMMENT='通知设置表';



CREATE TABLE `tools_sms_template` (
  `id` varchar(60) NOT NULL COMMENT '主键ID',
  `template_name` varchar(100) DEFAULT NULL COMMENT '模板名称',
  `template_code` varchar(100) DEFAULT NULL COMMENT '模板编码',
  `template_content` text COMMENT '模板内容',
  `status` int(11) DEFAULT NULL COMMENT '模板状态 (0:未提交审核,1:通过审核, -1 未通过审核)',
  `scene` varchar(100) DEFAULT NULL COMMENT '应用场景',
  `scene_desc` varchar(200) DEFAULT NULL COMMENT '应用场景描述',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
  `delete_flag` varchar(60) DEFAULT NULL COMMENT '删除标志',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(60)  DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` varchar(60) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='短信模板表';


CREATE TABLE `tools_email_template` (
    `id` varchar(60) NOT NULL COMMENT '主键ID',
    `template_code` varchar(100) DEFAULT NULL COMMENT '模板编码',
    `template_name` varchar(200) DEFAULT NULL COMMENT '模板名称',
    `template_subject` varchar(500) DEFAULT NULL COMMENT '模板主题',
    `template_content` text COMMENT '模板正文',
    `tag_name` varchar(100) DEFAULT NULL COMMENT '标签名',
    `scene` varchar(100) DEFAULT NULL COMMENT '应用场景',
    `scene_desc` varchar(200) DEFAULT NULL COMMENT '应用场景描述',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
    `delete_flag` varchar(60) DEFAULT NULL COMMENT '删除标志',
    `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` varchar(60)  DEFAULT NULL COMMENT '创建人',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user` varchar(60) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='邮件模板表';



