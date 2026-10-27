-- 补全 independent_machine.RESOURCE_POOL_ID
-- 实体 Machine 与产品 API 查询已依赖该列；若 V3_1 未完整执行或中途失败，会导致 Unknown column 'RESOURCE_POOL_ID'
-- 本脚本按列/索引是否已存在条件执行，可重复执行

SET @sql = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'independent_machine'
       AND COLUMN_NAME = 'RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_machine` ADD COLUMN `RESOURCE_POOL_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT ''资源池ID（当type=1资源池时，关联resource_pool.id）'' AFTER `TYPE`'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sqlidx = (
  SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND LOWER(TABLE_NAME) = 'independent_machine'
       AND INDEX_NAME = 'IDX_RESOURCE_POOL_ID') > 0,
    'SELECT 1',
    'ALTER TABLE `independent_machine` ADD INDEX `IDX_RESOURCE_POOL_ID` (`RESOURCE_POOL_ID`) USING BTREE'
  )
);
PREPARE stmtidx FROM @sqlidx;
EXECUTE stmtidx;
DEALLOCATE PREPARE stmtidx;
