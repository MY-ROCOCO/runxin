ALTER TABLE `news`
    ADD COLUMN `content_format` tinyint NOT NULL DEFAULT 0 COMMENT '内容格式: 0 纯文本 1 HTML' AFTER `content`;
UPDATE `dev_config` SET `CONFIG_VALUE` = '1.9.1' WHERE `CONFIG_KEY` = 'RUNXIN_SYS_VERSION';
