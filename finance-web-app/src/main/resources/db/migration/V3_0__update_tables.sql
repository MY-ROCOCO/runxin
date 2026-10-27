-- 版本号 1.2.1
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.2.1' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
-- 轮播图默认数据
-- 轮播图菜单数据
INSERT INTO `sys_resource` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('2037115160071770114', '1954729606024032257', '轮播图设置', 'system_slideshowsetting', NULL, 'MENU', '1548901111999770525', 'MENU', '/system/slideshowsetting', 'system/slideshowsetting/index.vue', 'mdi:view-carousel', NULL, 'TRUE', '6', 'mdi:view-carousel', 'NOT_DELETE', '2026-03-26 18:31:17', '1543837863788879871', NULL, NULL);
