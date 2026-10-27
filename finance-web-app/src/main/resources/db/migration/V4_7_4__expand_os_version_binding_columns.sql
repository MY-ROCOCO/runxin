-- 扩容操作系统版本绑定字段，避免多线路/多可用区场景下出现 Data too long

ALTER TABLE `independent_os_version`
MODIFY COLUMN `line_id` MEDIUMTEXT NOT NULL COMMENT '独立服务器线路id，多个ID逗号分隔';

ALTER TABLE `os_version`
MODIFY COLUMN `SUPPORTED_AVAILABILITY_ZONE` MEDIUMTEXT NULL COMMENT '支持的可用区，多个ID逗号分隔';
