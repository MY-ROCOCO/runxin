-- 移除「banner管理」菜单（service_banneradmin，/service/banneradmin）及角色/用户对该菜单的授权
DELETE FROM `sys_relation`
WHERE `TARGET_ID` = '1956267935500640257'
  AND `CATEGORY` IN ('SYS_ROLE_HAS_RESOURCE', 'SYS_USER_HAS_RESOURCE');

DELETE FROM `sys_resource` WHERE `ID` = '1956267935500640257';

-- 版本号 1.4.6
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.4.6' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
