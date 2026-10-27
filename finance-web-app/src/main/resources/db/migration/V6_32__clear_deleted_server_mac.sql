-- 已删除云服务器释放 MAC，避免唯一索引占用，新建实例可复用
UPDATE `business_server`
SET `PUBLIC_MAC` = NULL,
    `PRIVATE_MAC` = NULL
WHERE `BUSINESS_STATUS` = -2
  AND (`PUBLIC_MAC` IS NOT NULL OR `PRIVATE_MAC` IS NOT NULL);

-- 存量云服务器 / 独立服务器自动续费一律关闭
UPDATE `business_server` SET `RENEW_AUTO` = 0 WHERE `RENEW_AUTO` IS NULL OR `RENEW_AUTO` <> 0;
UPDATE `business_independent` SET `renew_auto` = 0 WHERE `renew_auto` IS NULL OR `renew_auto` <> 0;
ALTER TABLE `business_independent` MODIFY COLUMN `renew_auto` int NULL DEFAULT 0 COMMENT '是否自动续费： 0 不自动续费 1 自动续费';

INSERT INTO `dev_config` (`ID`, `CONFIG_KEY`, `CONFIG_VALUE`, `CATEGORY`, `REMARK`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
SELECT '2092800000000000020', 'AUTO_RENEW_DAYS_BEFORE', '1', 'SYS_BASE', '自动续费提前天数，到期前N天开始尝试余额扣款', '0', NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `dev_config` WHERE `CONFIG_KEY` = 'AUTO_RENEW_DAYS_BEFORE');

INSERT IGNORE INTO `dev_job` (`ID`, `NAME`, `CODE`, `CATEGORY`, `ACTION_CLASS`, `CRON_EXPRESSION`, `JOB_STATUS`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
VALUES ('2092800000000000010', '云服务器/独立服务器自动续费', 'auto_renew_server', 'BIZ',
        'com.rxinyun.admin.dev.modular.job.task.AutoRenewTaskRunner', '0 0 * * * ?', 'RUNNING', '5', NULL, 'NOT_DELETE', NOW(), '-1', NULL, NULL);

INSERT INTO `tools_email_template` (`id`, `template_code`, `template_name`, `template_subject`, `template_content`, `tag_name`, `scene`, `scene_desc`, `remark`, `delete_flag`, `create_time`, `create_user`, `update_time`, `update_user`)
SELECT '2092800000000000001', '', '用户：云服务器自动续费成功通知', '云服务器自动续费成功',
       '您好！您的云服务器 serverName 已自动续费成功，订单号 orderNo，扣款金额 payAmount，新到期时间 newEndTime。',
       '', 'member_cloud_server_auto_renew_success', '云服务器自动续费成功', '', 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `tools_email_template` WHERE `scene` = 'member_cloud_server_auto_renew_success');

INSERT INTO `tools_email_template` (`id`, `template_code`, `template_name`, `template_subject`, `template_content`, `tag_name`, `scene`, `scene_desc`, `remark`, `delete_flag`, `create_time`, `create_user`, `update_time`, `update_user`)
SELECT '2092800000000000002', '', '用户：云服务器自动续费余额不足通知', '云服务器自动续费失败，余额不足',
       '您好！您的云服务器 serverName 自动续费失败，应付金额 needAmount，当前余额 currentBalance，到期时间 expiryDate，请及时充值。',
       '', 'member_cloud_server_auto_renew_balance_fail', '云服务器自动续费余额不足', '', 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `tools_email_template` WHERE `scene` = 'member_cloud_server_auto_renew_balance_fail');

INSERT INTO `tools_email_template` (`id`, `template_code`, `template_name`, `template_subject`, `template_content`, `tag_name`, `scene`, `scene_desc`, `remark`, `delete_flag`, `create_time`, `create_user`, `update_time`, `update_user`)
SELECT '2092800000000000003', '', '用户：独立服务器自动续费成功通知', '独立服务器自动续费成功',
       '您好！您的独立服务器 serverName 已自动续费成功，订单号 orderNo，扣款金额 payAmount，新到期时间 newEndTime。',
       '', 'member_dedicated_server_auto_renew_success', '独立服务器自动续费成功', '', 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `tools_email_template` WHERE `scene` = 'member_dedicated_server_auto_renew_success');

INSERT INTO `tools_email_template` (`id`, `template_code`, `template_name`, `template_subject`, `template_content`, `tag_name`, `scene`, `scene_desc`, `remark`, `delete_flag`, `create_time`, `create_user`, `update_time`, `update_user`)
SELECT '2092800000000000004', '', '用户：独立服务器自动续费余额不足通知', '独立服务器自动续费失败，余额不足',
       '您好！您的独立服务器 serverName 自动续费失败，应付金额 needAmount，当前余额 currentBalance，到期时间 expiryDate，请及时充值。',
       '', 'member_dedicated_server_auto_renew_balance_fail', '独立服务器自动续费余额不足', '', 'NOT_DELETE', NOW(), '-1', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `tools_email_template` WHERE `scene` = 'member_dedicated_server_auto_renew_balance_fail');

INSERT INTO `tools_notification` (`id`, `type`, `config_type`, `description`, `wechat_status`, `wechat_template_id`, `wx_subscribe_status`, `wx_subscribe_template_id`, `sms_status`, `sms_template_id`, `email_status`, `email_template_id`, `ding_status`, `ding_template_id`, `wx_work_status`, `wx_work_template_id`, `telegram_status`, `telegram_template_id`, `target_type`, `scene_params`, `status`, `delete_flag`, `create_time`, `create_user`)
SELECT 17, 'member_cloud_server_auto_renew_success', 'NOTICE', '云服务器自动续费成功通知', 0, '0', 0, '0', 0, '0', 2, '2092800000000000001', 0, '0', 0, '0', 0, '0', 1,
       '{"serverName":"服务器名称","serverId":"服务器ID","orderNo":"订单号","payAmount":"扣款金额","newEndTime":"新到期时间"}', 1, 'NOT_DELETE', NOW(), '-1'
WHERE NOT EXISTS (SELECT 1 FROM `tools_notification` WHERE `type` = 'member_cloud_server_auto_renew_success');

INSERT INTO `tools_notification` (`id`, `type`, `config_type`, `description`, `wechat_status`, `wechat_template_id`, `wx_subscribe_status`, `wx_subscribe_template_id`, `sms_status`, `sms_template_id`, `email_status`, `email_template_id`, `ding_status`, `ding_template_id`, `wx_work_status`, `wx_work_template_id`, `telegram_status`, `telegram_template_id`, `target_type`, `scene_params`, `status`, `delete_flag`, `create_time`, `create_user`)
SELECT 18, 'member_cloud_server_auto_renew_balance_fail', 'NOTICE', '云服务器自动续费余额不足通知', 0, '0', 0, '0', 0, '0', 2, '2092800000000000002', 0, '0', 0, '0', 0, '0', 1,
       '{"serverName":"服务器名称","serverId":"服务器ID","needAmount":"应付金额","currentBalance":"当前余额","expiryDate":"到期时间"}', 1, 'NOT_DELETE', NOW(), '-1'
WHERE NOT EXISTS (SELECT 1 FROM `tools_notification` WHERE `type` = 'member_cloud_server_auto_renew_balance_fail');

INSERT INTO `tools_notification` (`id`, `type`, `config_type`, `description`, `wechat_status`, `wechat_template_id`, `wx_subscribe_status`, `wx_subscribe_template_id`, `sms_status`, `sms_template_id`, `email_status`, `email_template_id`, `ding_status`, `ding_template_id`, `wx_work_status`, `wx_work_template_id`, `telegram_status`, `telegram_template_id`, `target_type`, `scene_params`, `status`, `delete_flag`, `create_time`, `create_user`)
SELECT 19, 'member_dedicated_server_auto_renew_success', 'NOTICE', '独立服务器自动续费成功通知', 0, '0', 0, '0', 0, '0', 2, '2092800000000000003', 0, '0', 0, '0', 0, '0', 1,
       '{"serverName":"服务器名称","serverId":"服务器ID","orderNo":"订单号","payAmount":"扣款金额","newEndTime":"新到期时间"}', 1, 'NOT_DELETE', NOW(), '-1'
WHERE NOT EXISTS (SELECT 1 FROM `tools_notification` WHERE `type` = 'member_dedicated_server_auto_renew_success');

INSERT INTO `tools_notification` (`id`, `type`, `config_type`, `description`, `wechat_status`, `wechat_template_id`, `wx_subscribe_status`, `wx_subscribe_template_id`, `sms_status`, `sms_template_id`, `email_status`, `email_template_id`, `ding_status`, `ding_template_id`, `wx_work_status`, `wx_work_template_id`, `telegram_status`, `telegram_template_id`, `target_type`, `scene_params`, `status`, `delete_flag`, `create_time`, `create_user`)
SELECT 20, 'member_dedicated_server_auto_renew_balance_fail', 'NOTICE', '独立服务器自动续费余额不足通知', 0, '0', 0, '0', 0, '0', 2, '2092800000000000004', 0, '0', 0, '0', 0, '0', 1,
       '{"serverName":"服务器名称","serverId":"服务器ID","needAmount":"应付金额","currentBalance":"当前余额","expiryDate":"到期时间"}', 1, 'NOT_DELETE', NOW(), '-1'
WHERE NOT EXISTS (SELECT 1 FROM `tools_notification` WHERE `type` = 'member_dedicated_server_auto_renew_balance_fail');

UPDATE `dev_config` SET `CONFIG_VALUE` = '1.8.7' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
