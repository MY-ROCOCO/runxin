-- 应用发布版本（写入后台「系统版本」配置，供前端/关于页等展示）
-- 若与根 pom 中 finance.version 不一致，请同步修改本脚本中的版本号后再发布
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.2.8' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';

CREATE TABLE IF NOT EXISTS `SERVER_BACKUP_RECORD` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `SERVER_ID` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '云服务器ID',
  `MEMBER_NO` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员ID',
  `BACKUP_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '备份名称',
  `BACKUP_PATH` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '备份存放路径',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `UK_SERVER_BACKUP_SERVER` (`SERVER_ID`) USING BTREE,
  KEY `IDX_SERVER_ID` (`SERVER_ID`) USING BTREE,
  KEY `IDX_MEMBER_NO` (`MEMBER_NO`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='云服务器备份记录表';
