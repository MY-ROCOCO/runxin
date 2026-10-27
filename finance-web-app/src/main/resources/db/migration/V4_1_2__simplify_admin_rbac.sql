-- 精简后台权限模型为：账号 -> 角色 -> 菜单/按钮权限
-- 保留旧表结构做兼容，但废弃组织、职位、模块相关运行数据

-- 用户不再绑定组织、职位
UPDATE `sys_user`
SET `ORG_ID` = NULL,
    `POSITION_ID` = NULL,
    `POSITION_JSON` = NULL;

-- 角色统一收敛为全局角色
UPDATE `sys_role`
SET `ORG_ID` = NULL,
    `CATEGORY` = 'GLOBAL';

-- 菜单不再使用模块分组
UPDATE `sys_resource`
SET `MODULE` = NULL
WHERE `CATEGORY` = 'MENU';

-- 清理角色/用户对模块资源的授权关系
DELETE FROM `sys_relation`
WHERE `CATEGORY` IN ('SYS_ROLE_HAS_RESOURCE', 'SYS_USER_HAS_RESOURCE')
  AND `TARGET_ID` IN (
      SELECT `ID` FROM `sys_resource` WHERE `CATEGORY` = 'MODULE'
  );

-- 删除模块资源数据
DELETE FROM `sys_resource`
WHERE `CATEGORY` = 'MODULE';
