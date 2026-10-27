-- 删除已改为写死的网关配置键（含可能存在的微信支付网关占位键）
DELETE FROM `dev_config`
WHERE `CONFIG_KEY` IN (
    'RUNXIN_SMS_SMSBAO_REQUEST_URL',
    'ALIPAY_PAY_GATEWAY',
    'ALIPAY_REAL_GATEWAY',
    'RUNXIN_SMS_ALIYUN_END_POINT',
    'WECHAT_PAY_GATEWAY'
);

-- 删除「模块管理」菜单资源（PC 端 /sys/module，与 V1_1 中已逻辑删除的菜单一致，迁移中物理删除）
DELETE FROM `sys_resource` WHERE `ID` = '1548901111999771326';

-- 版本号 1.1.9
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.1.9' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
