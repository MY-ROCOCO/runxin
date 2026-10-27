CREATE TABLE IF NOT EXISTS `server_master_line` (
  `ID` varchar(60) NOT NULL,
  `MASTER_ID` varchar(60) NOT NULL COMMENT '宿主机ID',
  `LINE_ID` varchar(60) NOT NULL COMMENT '线路ID',
  `DELETE_FLAG` varchar(255) DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_USER` varchar(20) DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_server_master_line` (`MASTER_ID`, `LINE_ID`) USING BTREE,
  KEY `idx_server_master_line_line` (`LINE_ID`) USING BTREE,
  KEY `idx_server_master_line_master` (`MASTER_ID`) USING BTREE
) ENGINE=InnoDB COMMENT='宿主机线路绑定表';

ALTER TABLE `server_master`
    DROP COLUMN `LINE_ID`;
