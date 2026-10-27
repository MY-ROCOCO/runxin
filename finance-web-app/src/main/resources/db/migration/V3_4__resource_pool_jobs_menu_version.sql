-- =============================================================================
-- V3_4：资源池定时任务、资源池管理菜单、资源池请求日志、API 密钥管理菜单
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 定时任务（资源池请求发送、资源池业务状态同步）
-- -----------------------------------------------------------------------------
INSERT INTO `dev_job` (`ID`, `NAME`, `CODE`, `CATEGORY`, `ACTION_CLASS`, `CRON_EXPRESSION`, `JOB_STATUS`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2038581680262209537', '资源池请求定时任务', '37IF4a3rNV', 'BIZ', 'com.rxinyun.admin.dev.modular.api.task.ResourcePoolRequestTaskRunner', '5 * * * * ?', 'RUNNING', '1', '1', 'NOT_DELETE', '2026-03-30 19:38:42', '1543837863788879871', '2026-03-30 20:11:02', '1543837863788879871');
INSERT INTO `dev_job` (`ID`, `NAME`, `CODE`, `CATEGORY`, `ACTION_CLASS`, `CRON_EXPRESSION`, `JOB_STATUS`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2038581764316061698', '资源池业务状态定时任务', 'xpfl1pfjdq', 'BIZ', 'com.rxinyun.admin.dev.modular.api.task.ResourcePoolSyncTaskRunner', '5 * * * * ?', 'RUNNING', '1', '1', 'NOT_DELETE', '2026-03-30 19:39:02', '1543837863788879871', '2026-03-30 19:53:34', '1543837863788879871');

-- -----------------------------------------------------------------------------
-- 菜单资源
-- -----------------------------------------------------------------------------
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2038567154124894210', '0', '资源池管理', NULL, NULL, 'MENU', '1548901111999770525', 'CATALOG', '/resourcepool', NULL, 'ic:baseline-crop-7-5', NULL, 'TRUE', '1', 'ic:baseline-crop-7-5', 'NOT_DELETE', '2026-03-30 18:40:59', '1543837863788879871', NULL, NULL);
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2038567261276778498', '2038567154124894210', '资源池列表', 'resourcepool_list', NULL, 'MENU', '1548901111999770525', 'MENU', '/resourcepool/list', 'view:resourcepool_list', 'ic:baseline-class', NULL, 'TRUE', '1', 'ic:baseline-class', 'NOT_DELETE', '2026-03-30 18:41:25', '1543837863788879871', NULL, NULL);
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2038996305625493505', '2038567154124894210', '资源池请求日志', 'resourcepool_request', NULL, 'MENU', '1548901111999770525', 'MENU', '/resourcepool/request', 'view:resourcepool_request', 'mdi:history', NULL, 'TRUE', '2', 'mdi:history', 'NOT_DELETE', '2026-03-31 23:06:17', '1543837863788879871', '2026-03-31 23:07:35', '1543837863788879871');
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2038998690997481473', '1953652677103611906', 'API密钥管理', 'vip_apimanage', NULL, 'MENU', '1548901111999770525', 'MENU', '/vip/apimanage', 'view:vip_apimanage', 'ic:baseline-format-align-center', NULL, 'TRUE', '2', 'ic:baseline-format-align-center', 'NOT_DELETE', '2026-03-31 23:15:45', '1543837863788879871', NULL, NULL);

-- -----------------------------------------------------------------------------
-- 系统版本号（与 V3_0 等迁移一致，写入 dev_config）
-- -----------------------------------------------------------------------------
-- 版本号 1.2.2
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.2.2' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
