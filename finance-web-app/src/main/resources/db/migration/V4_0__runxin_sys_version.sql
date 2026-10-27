-- 应用发布版本（写入后台「系统版本」配置，供前端/关于页等展示）
-- 若与根 pom 中 finance.version 不一致，请同步修改本脚本中的版本号后再发布
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.2.4' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
