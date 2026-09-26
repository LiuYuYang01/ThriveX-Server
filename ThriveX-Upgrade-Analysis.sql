-- ThriveX 数据分析增强：升级脚本
-- 适用于已部署的旧版本数据库（新装用户直接导入 ThriveX.sql 即可，无需执行本文件）
-- 本脚本可重复执行（CREATE TABLE IF NOT EXISTS），执行前建议先备份数据库

-- 文章浏览日志：文章详情页每次浏览记一条，用于热门文章排行和单篇浏览趋势分析
CREATE TABLE IF NOT EXISTS `article_view_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `article_id` int NOT NULL COMMENT '文章ID',
  `ip` varchar(50) DEFAULT NULL COMMENT '访客IP',
  `create_time` bigint DEFAULT NULL COMMENT '浏览时间（毫秒时间戳）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_time` (`article_id`,`create_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- 搜索日志：站内搜索每次搜索记一条，用于搜索热词统计
CREATE TABLE IF NOT EXISTS `search_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `create_time` bigint DEFAULT NULL COMMENT '搜索时间（毫秒时间戳）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_keyword` (`keyword`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
