-- 应用发布版本（写入后台「系统版本」配置，供前端/关于页等展示）
-- V4_7_4 对应版本号补充迁移
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.3.2' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
