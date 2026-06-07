-- 移除 assistant 表的 name 字段（助手名称改由 model 对应的服务商标识展示）
ALTER TABLE `assistant` DROP COLUMN `name`;
