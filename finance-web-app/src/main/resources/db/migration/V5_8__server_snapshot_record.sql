UPDATE `dev_config` SET `CONFIG_VALUE` = '1.5.6' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';

CREATE TABLE IF NOT EXISTS `SERVER_SNAPSHOT_RECORD` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `SERVER_ID` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '云服务器ID',
  `MEMBER_NO` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员ID',
  `SNAPSHOT_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '快照名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `UK_SERVER_SNAPSHOT_NAME` (`SNAPSHOT_NAME`) USING BTREE,
  KEY `IDX_SERVER_ID` (`SERVER_ID`) USING BTREE,
  KEY `IDX_MEMBER_NO` (`MEMBER_NO`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='云服务器快照记录表';

SET @drop_snap_price_buy_month_sql = (
  SELECT IF(
    COUNT(*) > 0,
    'ALTER TABLE `server_local_line` DROP COLUMN `SNAP_PRICE_BUY_MONTH`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'server_local_line'
    AND COLUMN_NAME = 'SNAP_PRICE_BUY_MONTH'
);
PREPARE drop_snap_price_buy_month_stmt FROM @drop_snap_price_buy_month_sql;
EXECUTE drop_snap_price_buy_month_stmt;
DEALLOCATE PREPARE drop_snap_price_buy_month_stmt;

SET @drop_snap_price_buy_day_sql = (
  SELECT IF(
    COUNT(*) > 0,
    'ALTER TABLE `server_local_line` DROP COLUMN `SNAP_PRICE_BUY_DAY`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'server_local_line'
    AND COLUMN_NAME = 'SNAP_PRICE_BUY_DAY'
);
PREPARE drop_snap_price_buy_day_stmt FROM @drop_snap_price_buy_day_sql;
EXECUTE drop_snap_price_buy_day_stmt;
DEALLOCATE PREPARE drop_snap_price_buy_day_stmt;
