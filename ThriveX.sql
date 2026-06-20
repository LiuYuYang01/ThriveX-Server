-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: test_app
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `description` varchar(200) DEFAULT NULL COMMENT '文章介绍',
  `content` text NOT NULL COMMENT '文章主要内容',
  `cover` varchar(300) DEFAULT NULL COMMENT '文章封面',
  `view` int DEFAULT '0' COMMENT '文章浏览量',
  `comment` int DEFAULT '0' COMMENT '评论数量',
  `create_time` bigint DEFAULT NULL COMMENT '文章创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (1,'Hello World','当你看到这篇文章时就意味着安装成功，一切就绪！','当你看到这篇文章时就意味着安装成功，一切就绪！\n',NULL,12,0,1729224230508),(2,'🎉 ThriveX 现代化博客管理系统','ThriveX 是一个简而不简单的现代化博客管理系统，专注于分享技术文章和知识，为技术爱好者和从业者提供一个分享、交流和学习的平台。用户可以在平台上发表自己的技术文章，或浏览其他用户分享的文章，并与他们进行讨论和互动。','<p align=\"center\">\n    <a href=\"https://liuyuyang.net\" target=\"_blank\">\n        <img width=\"120\" src=\"https://bu.dusays.com/2024/11/17/6739adf188f64.png\" alt=\"ThriveX logo\" />\n    </a>\n</p>\n<h1 align=\"center\" style=\"margin: 20px 0; font-weight: 700; padding-bottom:10px;\">ThriveX Admin</h1>\n\n\n<p align=\"center\">\n    <a href=\"https://github.com/LiuYuYang01/ThriveX-Admin/blob/main/LICENSE\" target=\"_blank\">\n        <img alt=\"License: AGPL-3.0\" src=\"https://img.shields.io/badge/License-AGPL--3.0-blue.svg?style=flat-square&logo=gnu\" />\n    </a>\n    <a href=\"https://github.com/LiuYuYang01/ThriveX-Admin/stargazers\" target=\"_blank\">\n        <img alt=\"Stars\" src=\"https://img.shields.io/github/stars/LiuYuYang01/ThriveX-Admin?style=flat-square&logo=github&color=gold\" />\n    </a>\n    <a href=\"https://github.com/LiuYuYang01/ThriveX-Admin/network\" target=\"_blank\">\n        <img alt=\"Forks\" src=\"https://img.shields.io/github/forks/LiuYuYang01/ThriveX-Admin?style=flat-square&logo=github\" />\n    </a>\n</p>\n\n## 📖 项目简介\n\n**ThriveX Admin** 是 ThriveX 博客系统的现代化管理后台，采用 Next.js 15 + React 19 + Ant Design 构建，提供全功能的内容管理、用户管理、系统配置等一站式解决方案。\n\n作为 ThriveX 全栈解决方案的核心组成部分，Admin 控制端与前端博客（[ThriveX-Blog](https://github.com/LiuYuYang01/ThriveX-Blog)）和后端服务（[ThriveX-Server](https://github.com/LiuYuYang01/ThriveX-Server)）共同构成了一个完整的开源博客生态系统。\n\n\n\n## ✨ 核心特性\n\n- 🎨 **现代化 UI**：基于 Ant Design 6.x + Tailwind CSS 4.x，提供优雅的视觉体验\n- 📝 **富文本编辑器**：集成 WangEditor + ByteMD，支持 Markdown/HTML 双模式\n- 📊 **数据可视化**：ECharts 图表展示访问统计、文章分类、用户增长等关键指标\n- 🎯 **权限管理**：细粒度的 RBAC 权限控制，支持角色分配与权限配置\n- 📁 **文件管理**：支持本地存储与多种 OSS 服务商（阿里云、腾讯云、七牛云等）\n- 📧 **邮件系统**：内置 SMTP 邮件服务，支持评论回复、通知推送\n- 📱 **响应式设计**：完美适配桌面端与平板设备\n- 🌓 **暗色模式**：支持主题切换，保护用户视力\n- ⚡ **性能优化**：代码分割、懒加载、缓存策略，确保极致性能\n\n\n\n## 📸 界面预览\n\n<div align=\"center\">\n    <img src=\"https://bu.dusays.com/2026/03/08/69ad27c95c51f.jpg\" alt=\"ThriveX Admin Dashboard\" style=\"border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1)\" />\n</div>\n\n\n## 🚀 快速开始\n\nhttps://docs.liuyuyang.net/docs/项目部署/1Panel.html\n\n\n\n## 📂 项目结构\n\n```\nThriveX-Admin/\n├── src/\n│   ├── api/              # API 接口定义\n│   ├── components/       # 公共组件\n│   ├── hooks/            # 自定义 Hooks\n│   ├── layout/           # 布局组件\n│   ├── pages/            # 页面路由\n│   │   ├── article/      # 文章管理\n│   │   ├── cate/         # 分类管理\n│   │   ├── comment/      # 评论管理\n│   │   ├── config/       # 系统配置\n│   │   ├── dashboard/    # 仪表盘\n│   │   ├── file/         # 文件管理\n│   │   ├── storage/      # 存储配置\n│   │   └── user/         # 用户管理\n│   ├── services/         # 服务层\n│   ├── stores/           # 状态管理\n│   ├── styles/           # 样式文件\n│   ├── types/            # TypeScript 类型定义\n│   └── utils/            # 工具函数\n├── public/               # 静态资源\n├── .env                  # 环境变量配置\n├── package.json          # 项目配置\n└── vite.config.js        # Vite 配置\n```\n\n\n\n## 🌐 项目链接\n\n| 名称        | 链接                                                                                         | 说明         |\n| ----------- | -------------------------------------------------------------------------------------------- | ------------ |\n| 博客预览    | [https://liuyuyang.net](https://liuyuyang.net)                                               | 前端博客展示 |\n| 官网地址    | [https://thrivex.liuyuyang.net](https://thrivex.liuyuyang.net)                               | 项目官网     |\n| 文档中心    | [https://docs.liuyuyang.net](https://docs.liuyuyang.net)                                     | 使用文档     |\n| GitHub 主页 | [https://github.com/LiuYuYang01/ThriveX-Admin](https://github.com/LiuYuYang01/ThriveX-Admin) | 源码仓库     |\n\n\n\n## 📝 开源协议\n\n本项目采用 **AGPL-3.0** 许可证。\n\n**使用须知**：\n\n- ✅ 允许商业使用、修改、分发\n- ✅ 必须保留原始版权说明\n- ✅ 修改后的版本必须开源\n- ❌ 禁止任何闭源商业行为\n\n在项目 Star 突破 2K 后，您可以自由选择保留或删除版权信息。\n\n\n\n## ⭐ Star History\n\n[![Star History Chart](https://api.star-history.com/svg?repos=LiuYuYang01/ThriveX-Admin&type=Date)](https://star-history.com/#LiuYuYang01/ThriveX-Admin&Date)\n\n\n\n## 👨‍💻 作者信息\n\n**刘宇阳**\n\n- GitHub: [@LiuYuYang01](https://github.com/LiuYuYang01)\n- 我的博客: [https://liuyuyang.net](https://liuyuyang.net)\n- 关于我：[https://my.liuyuyang.net](https://my.liuyuyang.net)\n- 邮箱: [liuyuyang1024@yeah.net](mailto:liuyuyang1024@yeah.net)\n\n\n\n## 💬 交流群\n\n欢迎加入 ThriveX 官方交流群，与开发者和其他用户交流：\n\n<div align=\"center\">\n    <img src=\"https://bu.dusays.com/2025/06/03/683e96eb43ad8.jpg\" alt=\"WeChat Group\" style=\"width: 300px; border-radius: 8px;\" />\n</div>\n\n**加群方式**：添加微信 `liuyuyang2023`，备注 \"ThriveX\"\n\n\n\n## 🙏 鸣谢\n\n感谢所有为 ThriveX 项目做出贡献的开发者和用户！\n\n特别感谢以下项目提供的灵感与技术支持：\n\n- [zw-blog](https://blog.zwying.com/)\n- [blatr](https://www.blatr.cn/)\n- [poetize](https://poetize.cn/)\n\n\n\n## 🔒 免责声明\n\n本项目仅供学习交流使用，不提供任何技术咨询或技术支持服务。使用者在使用本项目时应遵守当地法律法规，不得用于任何违法活动。\n','https://bu.dusays.com/2025/08/29/68b16f22981d4.jpg',100000,0,1731833778995),(5,'Markdown 文章样式','','# Markdown 样式\n\n## 一、字符效果\n\n| 类型 | 使用方法 | 效果 |  \n| :--: | :--: | :--: |\n| 删除线 | \\~\\~文本\\~\\~ | ~~文本效果~~ |\n| 斜体字 | \\_文本\\_ | _文本效果_ |\n| 粗体字 | \\*\\*文本\\*\\* | **文本效果** |\n| 上标 | \\~文本\\~ | ~文本效果~ |\n| 下标 | \\^文本\\^ | ^文本效果^ |\n| 标记 | \\=\\=文本\\=\\= | ==文本效果== |\n\n## 二、列表\n\n### 1、无序列表\n\n- 福建\n  - 厦门\n  - 福州\n- 浙江\n- 江苏\n\n### 2、有序列表\n\n1. 动物\n   1. 人类\n   2. 犬类\n2. 植物\n3. 微生物\n\n### 3、任务列表\n\n- [x] 预习计算机网络\n- [ ] 复习现代控制理论\n- [ ] 刷现代控制理论历年卷\n  - [ ] 2019 年期末试卷\n  - [ ] 2020 年期末试卷\n\n# 三、链接\n\n## 1、超链接\n\n1. 使用方法：\\[普通链接\\]\\(链接地址)\n2. 效果展示：[ThriveX 官网](https://thrivex.liuyuyang.net/)\n3. 在新窗口打开（待完善）：<a href=\"https://docs.liuyuyang.net/\" target=\"_blank\">ThriveX 文档</a>\n\n## 2、图片链接\n\n1. 使用方法：\\[图片名称\\]\\(图片地址)\n2. 效果展示：![星空宇航员](https://bu.dusays.com/2024/04/24/6628990012b51.jpg)\n\n## 四、引用\n\n1. 使用方法：\\> 这里写引用的内容\n2. 效果展示：\n> 这里写引用的内容\n\n## 五、脚注\n1. 使用方法：\\[^1\\]\n2. 效果展示：\n这是一个简单的脚注 [^1] 而这是一个更长的脚注 [^bignote].\n\n[^1]: 这是第一个脚注.\n[^bignote]: 这是一个非常长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长的脚注.\n\n## 六、代码\n\n### 1、行内代码\n\n1. 使用方法：\\` 代码 \\`\n2. 效果展示：`npm install marked`\n\n### 2、代码片段\n\n1. 使用方法：\n    1. 以\\`\\`\\` 开头  以\\`\\`\\` 结尾\n    2. \n2. 效果展示：\n```html\n<!DOCTYPE html>\n<html>\n    <head>\n        <mate charest=\"utf-8\" />\n        <title>Hello world!</title>\n    </head>\n    <body>\n        <h1>Hello world!</h1>\n    </body>\n</html>\n```\n\n## 七、数学公式\n\n### 1、行间公式：\n$\n\\sin(\\alpha)^{\\theta}=\\sum_{i=0}^{n}(x^i + \\cos(f))\n$\n\n### 2、行内公式\n$E=mc^2$\n\n## 八、特殊符号\n\n&copy; & &uml; &trade; &iexcl; &pound;\n&amp; &lt; &gt; &yen; &euro; &reg; &plusmn; &para; &sect; &brvbar; &macr; &laquo; &middot;\n\nX&sup2; Y&sup3; &frac34; &frac14; &times; &divide; &raquo;\n\n18&ordm;C &quot; &apos;\n\n## 九、Emoji 表情 🎉\n\n- 马：🐎\n- 星星：✨\n- 笑脸：😀\n- 跑步：🏃‍\n\n## 十、提示信息\n- 使用方法: \n    -  \\> \\[!类型\\] 标题 开头\n    -  \\> 正文\n\n> [!note] Note\n> 用于强调即使用户在快速浏览时也应考虑的重点信息。\n\n\n> [!Tip] Tip\n> 用于帮助用户更成功的可选信息。\n\n\n> [!Check] Check\n> xxxxxxxx\n\n\n> [!warning] Warning\n> 由于存在潜在风险，需要用户立即关注的关键内容。\n\n\n> [!Danger] Danger\n> 一个行为的潜在负面后果。# 数学公式\n\n\n## 视频\n\n### 自定义视频\n\n<h3>单视频</h3>\n<video width=\"640\" height=\"360\" controls>\n    <source src=\"http://vjs.zencdn.net/v/oceans.mp4\" type=\"video/mp4\">\n    您的浏览器不支持 HTML5 视频标签。\n</video>\n\n<h3>视频尺寸</h3>\n<video width=\"800\" controls>\n    <source src=\"http://vjs.zencdn.net/v/oceans.mp4\" type=\"video/mp4\">\n    您的浏览器不支持 HTML5 视频标签。\n</video>\n\n<h3>视频加封面</h3>\n<video width=\"640\" height=\"360\" controls poster=\"https://bu.dusays.com/2024/09/17/66e9704b2b809.png\">\n    <source src=\"http://vjs.zencdn.net/v/oceans.mp4\" type=\"video/mp4\">\n    您的浏览器不支持 HTML5 视频标签。\n</video>\n\n<h3>视频加封面加尺寸</h3>\n<video width=\"100%\" controls poster=\"https://bu.dusays.com/2024/09/17/66e9704b2b809.png\">\n    <source src=\"http://vjs.zencdn.net/v/oceans.mp4\" type=\"video/mp4\">\n    您的浏览器不支持 HTML5 视频标签。\n</video>\n\n\n### 哔哩哔哩视频\n\n<h3>默认宽度</h3>\n<iframe src=\"//player.bilibili.com/player.html?isOutside=true&aid=113651931481594&bvid=BV1yaB7YbEy6&cid=27343916591&p=1\" scrolling=\"no\" border=\"0\" frameborder=\"no\" framespacing=\"0\" allowfullscreen></iframe>\n\n<h3>自定义尺寸</h3>\n<iframe src=\"//player.bilibili.com/player.html?isOutside=true&aid=113651931481594&bvid=BV1yaB7YbEy6&cid=27343916591&p=1\" scrolling=\"no\" border=\"0\" frameborder=\"no\" framespacing=\"0\" allowfullscreen style=\"width:100%;height:500px\"></iframe>\n\n\n## 其他\n\n### 折叠\n<details>\n<summary>点击展开</summary>\n\n这里是折叠内容。\n\n</details>\n\n\n### 分割线\n___\n\n***\n\n---\n\n\n### 解析 HTML 标签\n<div style=\"color: red; font-size:30px\">ThriveX 现代化博客管理系统</div>','',13,0,1744980393520),(14,'记得点个免费的 Star 支持一下作者','','记得点个免费的 Star 支持一下作者','',0,0,1775230885372);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_cate`
--

DROP TABLE IF EXISTS `article_cate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_cate` (
  `id` int NOT NULL AUTO_INCREMENT,
  `article_id` int NOT NULL COMMENT '文章ID',
  `cate_id` int NOT NULL COMMENT '分类ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_cate_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1513 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章和分类的中间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_cate`
--

LOCK TABLES `article_cate` WRITE;
/*!40000 ALTER TABLE `article_cate` DISABLE KEYS */;
INSERT INTO `article_cate` VALUES (1440,1,1),(1507,14,1),(1510,5,1),(1512,2,1);
/*!40000 ALTER TABLE `article_cate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_config`
--

DROP TABLE IF EXISTS `article_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` tinyint DEFAULT '1' COMMENT '文章状态',
  `password` varchar(100) DEFAULT '' COMMENT '是否文章加密',
  `is_encrypt` tinyint DEFAULT '0' COMMENT '是否加密',
  `is_draft` tinyint DEFAULT '0' COMMENT '是否为草稿',
  `is_del` tinyint DEFAULT '0' COMMENT '是否删除',
  `article_id` int NOT NULL COMMENT '对应的文章id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_config_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_config`
--

LOCK TABLES `article_config` WRITE;
/*!40000 ALTER TABLE `article_config` DISABLE KEYS */;
INSERT INTO `article_config` VALUES (1,1,'',0,0,0,1),(26,3,'',0,0,1,14),(29,1,'',0,0,0,5),(31,1,'',0,0,0,2);
/*!40000 ALTER TABLE `article_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `id` int NOT NULL AUTO_INCREMENT,
  `article_id` int NOT NULL COMMENT '文章 ID',
  `tag_id` int NOT NULL COMMENT '标签 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_tag_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag`
--

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
INSERT INTO `article_tag` VALUES (1,1,3),(22,14,3),(23,14,97),(26,5,3),(28,2,3);
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assistant`
--

DROP TABLE IF EXISTS `assistant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assistant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '助手名称',
  `key` varchar(500) NOT NULL COMMENT '密钥',
  `url` varchar(500) NOT NULL COMMENT 'API 地址',
  `model` varchar(255) NOT NULL COMMENT '模型',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否被启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `assistant_pk` (`name`),
  UNIQUE KEY `assistant_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='助手管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assistant`
--

LOCK TABLES `assistant` WRITE;
/*!40000 ALTER TABLE `assistant` DISABLE KEYS */;
INSERT INTO `assistant` VALUES (2,'测试助手','xxxxxxxxxxxxxxxxxx','https://api.deepseek.com','deepseek-chat',1);
/*!40000 ALTER TABLE `assistant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cate`
--

DROP TABLE IF EXISTS `cate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cate` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '分类名称',
  `url` varchar(255) DEFAULT '/' COMMENT '分类链接',
  `mark` varchar(100) NOT NULL COMMENT '分类标识',
  `level` int DEFAULT NULL COMMENT '分类级别',
  `type` varchar(10) DEFAULT 'cate' COMMENT '导航还是分类',
  `is_hide` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否隐藏',
  `order` int NOT NULL DEFAULT '0' COMMENT '分类顺序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE,
  UNIQUE KEY `cate_pk` (`mark`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cate`
--

LOCK TABLES `cate` WRITE;
/*!40000 ALTER TABLE `cate` DISABLE KEYS */;
INSERT INTO `cate` VALUES (1,'💎 默认分类','/','kfbj',0,'cate',0,1),(68,'⛳️ 足迹','/footprint','zj',83,'page',0,1),(69,'👋 关于我','/my','my',83,'page',0,8),(70,'😇 朋友圈','/friend','pyq',83,'page',0,4),(71,'💌 留言墙','/wall/all','wall',83,'page',0,5),(72,'🔥 GitHub','https://github.com/LiuYuYang01/ThriveX-Blog','github',83,'page',0,10),(73,'📊 统计','/data','data',83,'page',0,11),(74,'🏕️ 闪念','/record','record',83,'page',0,2),(77,'🔭 我的设备','/equipment','wdsb',83,'page',0,7),(78,'🏷️ 标签墙','/tags','bqy',83,'page',0,6),(79,'💪 我的履历','/resume','wdll',83,'page',0,9),(81,'🐟 鱼塘','/fishpond','yt',83,'page',0,3),(83,'🧩 探索','/','ts',0,'page',0,2);
/*!40000 ALTER TABLE `cate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '评论者名称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '评论者头像',
  `content` text NOT NULL COMMENT '评论内容',
  `email` varchar(100) DEFAULT NULL COMMENT '评论者邮箱',
  `url` varchar(500) DEFAULT NULL COMMENT '评论者网站',
  `article_id` int NOT NULL COMMENT '所属文章ID',
  `comment_id` int DEFAULT '0' COMMENT '二级评论',
  `status` int DEFAULT '0' COMMENT '是否审核',
  `create_time` varchar(255) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=518 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (515,'ThriveX','https://q1.qlogo.cn/g?b=qq&nk=3311118881&s=640','记得点个star','3311118881@qq.com','https://liuyuyang.net',2,0,1,'1744980488518'),(517,'神秘人','https://q1.qlogo.cn/g?b=qq&nk=3311118881&s=640','Hello','3311118881@qq.com','',1,0,0,'1778672355584');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `env_config`
--

DROP TABLE IF EXISTS `env_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `env_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `value` json NOT NULL COMMENT '配置项',
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '配置备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `env_config_pk_2` (`id`),
  UNIQUE KEY `env_config_pk` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `env_config`
--

LOCK TABLES `env_config` WRITE;
/*!40000 ALTER TABLE `env_config` DISABLE KEYS */;
INSERT INTO `env_config` VALUES (1,'baidu_statis','{\"site_id\": 17256142, \"access_token\": \"\"}','B 百度统计：在控制端首页显示网站数据'),(2,'email','{\"host\": \"smtp.qq.com\", \"port\": 465, \"password\": \"\", \"username\": \"xxx@qq.com\"}','邮件发送配置'),(3,'gaode_map','{\"key_code\": \"\", \"security_code\": \"\"}','高德地图配置'),(4,'gaode_coordinate','{\"key\": \"\"}','高德地图坐标配置'),(5,'qiniu_storage','{\"domain\": \"\", \"zlevel\": 1, \"root_dir\": \"static\", \"end_point\": \"\", \"access_key\": \"\", \"secret_key\": \"\", \"bucket_name\": \"\"}','七牛云存储'),(6,'baidu_statis_key','{\"key\": \"\"}','A 百度统计：在前端获取该配置来激活统计功能'),(7,'hcaptcha','{\"key\": \"\"}','人机验证配置'),(8,'is_system_init','{\"value\": false}','系统是否初始化');
/*!40000 ALTER TABLE `env_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `footprint`
--

DROP TABLE IF EXISTS `footprint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `footprint` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` varchar(1500) DEFAULT NULL COMMENT '内容',
  `address` varchar(255) NOT NULL COMMENT '地址',
  `position` varchar(255) NOT NULL COMMENT '坐标纬度',
  `images` json DEFAULT NULL COMMENT '图片',
  `create_time` bigint NOT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `footprint_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `footprint`
--

LOCK TABLES `footprint` WRITE;
/*!40000 ALTER TABLE `footprint` DISABLE KEYS */;
INSERT INTO `footprint` VALUES (33,'东方明珠','','上海市浦东新区世纪大道','121.499718,31.239703','[]',1599667200000);
/*!40000 ALTER TABLE `footprint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `link`
--

DROP TABLE IF EXISTS `link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `link` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '网站标题',
  `description` varchar(255) NOT NULL COMMENT '网站描述',
  `email` varchar(100) DEFAULT NULL COMMENT '网站邮箱',
  `image` varchar(255) NOT NULL COMMENT '网站logo',
  `url` varchar(500) DEFAULT NULL COMMENT '网站链接',
  `rss` varchar(500) DEFAULT NULL,
  `order` int NOT NULL DEFAULT '0' COMMENT '友联顺序',
  `type_id` int NOT NULL COMMENT '网站所绑定的类型',
  `status` int NOT NULL DEFAULT '0' COMMENT '是否审核',
  `create_time` bigint NOT NULL COMMENT '网站创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `link`
--

LOCK TABLES `link` WRITE;
/*!40000 ALTER TABLE `link` DISABLE KEYS */;
INSERT INTO `link` VALUES (50,'宇阳','ThriveX 博客管理系统作者','liuyuyang1024@yeah.net','https://q1.qlogo.cn/g?b=qq&nk=3311118881&s=640','https://liuyuyang.net/','https://liuyuyang.net/api/rss',1,3,1,1723533206613),(53,'心之所向','旅行让我爱上摄影，摄影让我爱上旅行','3311118881@qq.com','https://q1.qlogo.cn/g?b=qq&nk=3311118881&s=640','https://frame.liuyuyang.net/','',5,3,1,1723533206613),(55,'平凡的人','关于我的个人介绍','3311118881@qq.com','https://q1.qlogo.cn/g?b=qq&nk=3311118881&s=640','https://my.liuyuyang.net/','',4,3,1,1781577262957),(56,'ThriveX 官网','现代化博客管理系统','3311118881@qq.com','https://bu.dusays.com/2024/11/17/6739adf188f64.png','https://thrivex.liuyuyang.net',NULL,2,3,1,1781577319966),(57,'ThriveX 官方文档','年轻、开源、免费、好用、高颜值','3311118881@qq.com','https://bu.dusays.com/2024/11/17/6739adf188f64.png','https://docs.liuyuyang.net',NULL,3,3,1,1781577395234);
/*!40000 ALTER TABLE `link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `link_type`
--

DROP TABLE IF EXISTS `link_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `link_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '类型名称',
  `is_admin` int NOT NULL DEFAULT '0' COMMENT '用户是否可选择',
  `order` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='网站类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `link_type`
--

LOCK TABLES `link_type` WRITE;
/*!40000 ALTER TABLE `link_type` DISABLE KEYS */;
INSERT INTO `link_type` VALUES (1,'生活类',0,4),(2,'技术类',0,5),(3,'全站置顶',1,1),(4,'推荐',1,2),(5,'大佬',1,3),(6,'聚合类',0,6);
/*!40000 ALTER TABLE `link_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oss`
--

DROP TABLE IF EXISTS `oss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oss` (
  `id` int NOT NULL AUTO_INCREMENT,
  `platform` varchar(255) NOT NULL COMMENT '平台',
  `access_key` varchar(100) DEFAULT NULL COMMENT 'key',
  `secret_key` varchar(255) DEFAULT '/' COMMENT '密钥',
  `end_point` varchar(100) DEFAULT NULL COMMENT 'endPoint',
  `bucket_name` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `base_path` varchar(255) DEFAULT NULL,
  `is_enable` int DEFAULT NULL COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='oss配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oss`
--

LOCK TABLES `oss` WRITE;
/*!40000 ALTER TABLE `oss` DISABLE KEYS */;
INSERT INTO `oss` VALUES (1,'local',NULL,'','/path/to/your/static/dir/',NULL,'localhost:9003/static/','upload/',1),(7,'tencent','','','ap-nanjing','your-bucket-appid','your-bucket-appid.cos.ap-nanjing.myqcloud.com/','upload/',NULL);
/*!40000 ALTER TABLE `oss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_config`
--

DROP TABLE IF EXISTS `page_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `page_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '配置项名称',
  `value` json NOT NULL COMMENT '配置项值',
  `notes` varchar(255) DEFAULT NULL COMMENT '配置备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `page_config_pk_2` (`id`),
  UNIQUE KEY `page_config_pk_3` (`id`),
  UNIQUE KEY `page_config_pk` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `page_config`
--

LOCK TABLES `page_config` WRITE;
/*!40000 ALTER TABLE `page_config` DISABLE KEYS */;
INSERT INTO `page_config` VALUES (1,'my','{\"goals\": [{\"value\": \"1、提高前端技术栈深度\", \"status\": 1}, {\"value\": \"2、提高后端技术栈深度\", \"status\": 1}, {\"value\": \"3、ThriveX 博客管理系统 ⭐️ 破千\", \"status\": 1}, {\"value\": \"4、ThriveX 最低要求保持周更\", \"status\": 1}], \"project\": [{\"name\": \"ThriveX\", \"front\": {\"url\": \"https://github.com/LiuYuYang01/ThriveX-Blog\", \"name\": \"前端：\", \"technology\": \"NextJS、TypeScript、Zustand、TailwindCSS、Scss、Echarts\"}, \"images\": [\"https://bu.dusays.com/2024/09/17/66e9704b2b809.png\", \"https://bu.dusays.com/2024/09/17/66e97036dddcb.png\", \"https://bu.dusays.com/2024/09/17/66e97035726ae.png\", \"https://bu.dusays.com/2024/09/17/66e97031cd456.png\"], \"backend\": {\"url\": \"https://github.com/LiuYuYang01/ThriveX-Server\", \"name\": \"后端：\", \"technology\": \"Spring Boot、Mybatis Plus、MySQL、Redis、Qiniu、Socket.io、Swagger\"}, \"control\": {\"url\": \"https://github.com/LiuYuYang01/ThriveX-Admin\", \"name\": \"控制端：\", \"technology\": \"React、Antd、TypeScript、Zustand、TailwindCSS、Echarts\"}, \"description\": \"🎉 ThriveX 相比旧版 Thrive 的核心改变是从 Vue 全面迁移到了 React 技术栈并采用了 Nextjs 服务端渲染技术进行全方面重构，对SEO方面有了显著的提高。并且还新增了很多额外的功能...\"}, {\"name\": \"Thrive\", \"front\": {\"url\": \"https://github.com/LiuYuYang01/Thrive_Blog\", \"technology\": \"前端：Vue3、TypeScript、Pinia、Element-plus、Scss、Echarts 、highlight.js\"}, \"images\": [\"https://bu.dusays.com/2024/09/17/66e96cb4e8417.png\", \"https://bu.dusays.com/2024/09/17/66e96ca366600.png\", \"https://bu.dusays.com/2024/09/17/66e96ca781d49.png\", \"https://bu.dusays.com/2024/09/17/66e96c9e76c81.png\"], \"backend\": {\"url\": \"https://github.com/LiuYuYang01/Thrive_Server\", \"name\": \"后端：\", \"technology\": \"Python、Flask、SQLAlchemy、MySQL、Flask-JWT、Socket.io、Swagger\"}, \"control\": {\"url\": \"https://github.com/LiuYuYang01/Thrive_Admin\", \"name\": \"控制端：\", \"technology\": \"Vue3、TypeScript、Pinia、Element-plus、Scss\"}, \"description\": \"🎉 Thrive 是一个简而不简单的现代化博客管理系统，专注于分享技术文章和知识，为技术爱好者和从业者提供一个分享、交流和学习的平台。用户可以在平台上发表自己的技术文章，或浏览其他用户分享的文章，并与他们进行讨论和互动。\"}, {\"name\": \"云上校园\", \"front\": {\"url\": \"\", \"name\": \"前端：\", \"technology\": \"微信小程序、Vant、Echarts、Autojs\"}, \"images\": [\"https://bu.dusays.com/2024/09/18/66ea606eb5aa1.png\", \"https://bu.dusays.com/2024/09/18/66ea605d89df7.png\", \"https://bu.dusays.com/2024/09/18/66ea605ca9f0d.jpg\"], \"backend\": {\"url\": \"\", \"name\": \"后端：\", \"technology\": \"Nodejs、Eggjs、Socket.io、MySQL\"}, \"control\": {\"url\": \"\", \"name\": \"控制端：\", \"technology\": \"Vue2、Element UI、vue-element-admin\"}, \"description\": \"🎉 云上校园是一个现代化大学生社交平台，该项目的立意是为了打造一个完整的校园生态圈，使校园触手可及!\"}], \"hometown\": [113.625351, 34.746303], \"info_one\": {\"name\": \"Liu YuYang\", \"notes\": \"不是修改这个哦，这段代码暂时先保留，勿删！！！\", \"avatar\": \"https://q.qlogo.cn/g?b=qq&nk=3311118881&s=640\", \"profession\": \"一名Web全栈开发工程师\", \"introduction\": \"我从小就对计算机技术有着无穷的兴趣，因此我的梦想是做一名技术顶尖的 架构师。所以我一直在朝着这个方向去努力、去坚持 直到梦想成真！\"}, \"info_two\": {\"author\": \"宇阳\", \"know_me\": \"https://liuyuyang.net/article/2227\", \"left_tags\": [\"🤖️ 数码科技爱好者\", \"🔍 分享与热心帮助\", \"💻 全栈开发工程师\"], \"avatar_url\": \"https://q.qlogo.cn/g?b=qq&nk=3311118881&s=640\", \"right_tags\": [\"源于热爱而发电 ✨\", \"开源项目作者 🥳\", \"热爱漫无边际 🎉\"]}, \"character\": [{\"color\": \"#4298b4\", \"text1\": \"内向\", \"text2\": \"外向\", \"value\": 54, \"content\": \"内向型的人往往更喜欢较少但深入和有意义的社交互动，通常更喜欢安静的环境。\"}, {\"color\": \"#e4ae3a\", \"text1\": \"现实\", \"text2\": \"有远见\", \"value\": 41, \"content\": \"有远见型的人非常富有想象力、思想开放并充满好奇心。他们重视原创性，专注于隐含的意义和遥远的可能性。\"}, {\"color\": \"#33a474\", \"text1\": \"感受\", \"text2\": \"理性分析\", \"value\": 41, \"content\": \"感受型的人重视情感表达和敏感性。他们非常重视同理心、社会和谐及合作。\"}, {\"color\": \"#88619a\", \"text1\": \"展望\", \"text2\": \"评判\", \"value\": 79, \"content\": \"评判型的人果断、周到，很有条理。他们重视清晰度、可预测性和封闭性，更喜欢结构和计划，而不是自发性。\"}, {\"color\": \"#f25e62\", \"text1\": \"起伏不定\", \"text2\": \"坚决\", \"value\": 78, \"content\": \"起伏不定型的人自我意识强，对压力敏感。他们在情绪上有一种紧迫感，往往以成功为导向，追求完美，渴望进步。\"}], \"work_open\": false, \"info_style\": \"info_one\", \"technology_stack\": [\"scss\", \"css\", \"html\", \"tailwindcss\", \"axios\", \"fetch\", \"vue\", \"vuex\", \"redux\", \"zustand\", \"element-plus\", \"typescript\", \"javascript\", \"antdesign\", \"motion\", \"pinia\", \"framer-motion\", \"echarts\", \"java\", \"spring\", \"springboot\", \"mybatis\", \"mybatis-plus\", \"redis\", \"rabbitmq\", \"mysql\", \"mongodb\", \"react\", \"nextjs\", \"nestjs\", \"webpack\", \"vite\", \"nodedotjs\", \"nextdotjs\", \"prisma\", \"koa\", \"express\", \"python\", \"flask\", \"nginx\", \"vercel\", \"docker\", \"git\", \"github\", \"visualstudiocode\", \"intellijidea\", \"datagrip\", \"apifox\", \"postman\", \"trae\", \"cursor\", \"webstorm\", \"navicat\", \"hbuilder\", \"hbuilderx\", \"googledrive\", \"apple\", \"windows\", \"linux\", \"pycharm\", \"wechat\"]}','关于我'),(2,'resume','{\"links\": {\"blog\": \"https://liuyuyang.net/\", \"csdn\": \"https://thrive.blog.csdn.net\", \"github\": \"https://github.com/LiuYuYang01\"}, \"skills\": [\"熟练 HTML5、CSS3、Flex、Scss、TailwindCSS 能够精准还原 UI 设计师的产品原型图，实现产品级的复现\", \"熟练 TypeScript、JavaScript、jQuery、面向对象、闭包、原型链、WebApi、原生DOM\", \"熟练 Vue2 / 3 相关生态：Axios、Vuex、Pinia\", \"熟练 React18 相关生态：NextJS、Redux、Zustand、Ahooks、Motion\", \"熟练 Uniapp 完成多端适配 以及 原生微信小程序开发\", \"熟练 Echarts 数据可视化开发 且 能够根据业务需求进行自定义扩展\", \"熟练 Ant Design、NextUI、Element UI、Vant、Naive UI、Bootstrap 等多种组件库的使用\", \"熟悉 NodeJS 相关生态：Express、NextJS、NestJS、Prisma\", \"熟悉 Python Flask 及 SQLAlchemy 对象映射（ORM）框架\", \"熟悉 Electron 跨平台桌面应用开发框架\", \"了解 Remix 全栈开发框架\", \"了解 WebPack、Vite 等打包构建工具\", \"熟悉 Spring Boot、Spring MVC、Mybatis Plus 等主流框架\", \"熟练 MySQL 关系型数据库，具备手写 SQL 及复杂查询的能力\", \"了解 Spring Cloud 微服务框架及常用的组件 Nacos、OpenFeign、Gateway\", \"熟悉 Linux 常用命令以及 Nginx 基本配置\", \"熟悉 Docker 基本命令、容器编排、镜像构建，并熟练使用可视化工具如：宝塔、1Panel 等，并有项目部署上线的经验\"], \"projects\": [{\"name\": \"ThriveX CMS 建站管理系统\", \"role\": \"全栈开发（NextJS + Spring Boot）\", \"links\": {\"api\": \"https://api.liuyuyang.net/doc.html\", \"docs\": \"https://docs.liuyuyang.net/\", \"preview\": \"https://liuyuyang.net/\", \"website\": \"https://thrivex.liuyuyang.net/\"}, \"period\": \"2023.03-至今\", \"techStack\": {\"backend\": \"Spring Boot、Mybatis Plus、MySQL、Qiniu、Swagger、Python、Flask、SQLalchemy\", \"frontend\": \"React、NextJS、TypeScript、Zustand、TailwindCSS、Scss、Next UI、Antd UI、Echarts、React Hook Form、Ahooks、百度统计API、高德地图API、Vue3、Pinia、Element UI\", \"deployment\": \"采用 Docker Compose 一键部署、采用 Nginx 实现反向代理、SSL 证书等\"}, \"highlights\": [\"【AI】采用科大讯飞AI大模型实现文章续写、优化、总结、智能问答\", \"【权限】RBAC 权限管理，动态路由、按钮权限、多用户登录\", \"【地图】采用 高德地图实现旅游足迹可视化等功能\", \"【安全】限制异地登录 及 以及多设备登录同一账号\", \"【交互】采用 TailwindCSS 实现白天、昼夜主题切换效果\", \"【适配】采用 TailwindCSS 媒体查询实现手机、平板、电脑三端适配\", \"【可视化】采用 百度统计API + Echarts 实现数据可视化\", \"【SEO】采用 NextJS 服务端渲染技术进行 SEO 优化，在 Lighthouse 中评分 100%\", \"【文件系统】同时兼容阿里云、腾讯云、华为云、七牛云、百度云等对象存储\", \"【一键部署】采用 Docker Compose 脚本实现前端、控制端、后端、数据库一键部署\", \"【自动化部署】利用 Vercel 实现高效的持续集成与自动化部署\"], \"description\": [\"ThriveX 是一个年轻、高颜值、全开源、永不收费的现代化 CMS 管理系统，项目组成是前端、控制端、后端，采用前后端分离开发式，是一个 NextJS + Spring Boot 的产物。\"], \"achievements\": [\"一个人完成产品、UI、前端、控制端、后端、数据库、测试 以及 项目部署上线\", \"项目代码全开源，截止目前在 GitHub 已经有 1867 条 Commit 记录，收获 700+ Star，156+ Fork\", \"目前已有多数用户在使用该系统，粉丝群已超千名用户\"], \"repositories\": [{\"url\": \"https://github.com/LiuYuYang01/ThriveX-Blog\", \"name\": \"前端\"}, {\"url\": \"https://github.com/LiuYuYang01/ThriveX-Admin\", \"name\": \"控制端\"}, {\"url\": \"https://github.com/LiuYuYang01/ThriveX-Server\", \"name\": \"后端\"}]}, {\"name\": \"云上校园\", \"role\": \"全栈开发（小程序 + Eggjs）\", \"period\": \"2022.12-2023.05\", \"techStack\": \"微信小程序、Vant、Vue2、Element UI、Echarts、Nodejs（Eggjs）、MySQL、Socket.io\", \"challenges\": [], \"highlights\": [\"一、通过 Echarts 搭配百度统计API实现数据可视化\", \"二、微信登录、登录状态检测\", \"三、实时通讯聊天室：支持私聊、群聊、表情、图片聊天记录存储\"], \"description\": [\"云上校园是一个专注提升大学校园生活质量与便利性的平台，打造一个集信息传播、学习辅助、就业赚取、维权举报、科技体验于一体的综合服务平台。\"], \"achievements\": \"上海市第十五届计算机应用能力大赛三等奖\", \"repositories\": []}], \"education\": {\"major\": \"软件工程\", \"degree\": \"本科\", \"period\": \"2021-2026\", \"school\": \"上海开放大学\", \"achievements\": [\"上海市计算机应用能力大赛三等奖\", \"上海开放大学创新项目奖\", \"二等奖学金\", \"计算机软件著作权\"]}, \"advantages\": [\"上海市第十五届计算机应用能力大赛 三等奖、创新项目奖\", \"GitHub 开源项目作者（ThriveX CMS 建站系统）Star 900+\", \"ThriveX CMS 建站系统 计算机软件著作权（申请中）\", \"具备项目从 0 到 1 部署上线的经验：https://liuyuyang.net/\", \"利用业余时间持续输出技术文章，目前在 CSDN 累计拥有 1700+ 粉丝\", \"熟练 Spring Boot 以及 Express、Flask 等多种后端开发语言 对前后端接口联调过程中的问题能够进行清晰定位\", \"能够独当一面，从 0 到 1 构建前端项目 且 具备完整项目设计、研发、部署、全链路问题排查能力\"], \"personalInfo\": {\"age\": \"22岁\", \"name\": \"刘宇阳\", \"title\": \"前端开发工程师\", \"avatar\": \"https://q1.qlogo.cn/g?b=qq&nk=3311118881&s=640\", \"contact\": {\"email\": \"liuyuyang1024@yeah.net\", \"phone\": \"1778811xxxx\", \"github\": \"https://github.com/LiuYuYang01\"}, \"location\": \"郑州\"}, \"workExperience\": [{\"period\": \"2024.07-至今\", \"company\": \"宁波 XXXX 数字科技有限公司\", \"position\": \"前端开发工程师\", \"responsibilities\": [\"完成日常项目的开发与迭代，高质量完成项目交付与上线\"]}, {\"period\": \"2024.05-2024.06\", \"company\": \"成都 XX 科技有限公司\", \"position\": \"前端开发工程师\", \"responsibilities\": [\"负责公司内部 Todo 系统开发与改进，采用 Electron 桌面软件开发框架\"]}]}','我的简历'),(3,'equipment','{\"list\": [{\"items\": [{\"name\": \"MacBook Air M4\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/MacBook%20Air%20M4.jpg\", \"price\": 8757, \"description\": \"发布当天就买了，目前在为我创造价值\"}, {\"name\": \"iPhone 16 Pro Max\", \"color\": \"#F6F6F8\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/iPhone16ProMax.jpg\", \"price\": 8299, \"description\": \"第一次用苹果，感觉除了流畅些，其他的都不如安卓 [:狗头]\"}, {\"name\": \"Xiaomi 7s Pro\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/Xiaomi7sPro.png\", \"price\": 2300, \"description\": \"用来学习是个不错的选择\"}, {\"name\": \"Redmi A27U 4K 显示器\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/Redmi%20A27U%204K%20%E6%98%BE%E7%A4%BA%E5%99%A8.png\", \"price\": 1399, \"description\": \"4K 显示屏性价比天花板\"}, {\"name\": \"Magic Keyboard\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/Magic%20Keyboard.jpg\", \"price\": 599, \"description\": \"习惯了用触控板，自然少不了这个\"}, {\"name\": \"洛斐小顺青春版\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E6%B4%9B%E6%96%90%E5%B0%8F%E9%A1%BA%E9%9D%92%E6%98%A5%E7%89%88.jpeg\", \"price\": 399, \"description\": \"目前我的主力键盘\"}, {\"name\": \"Keychron K3 Max\", \"color\": \"#E9E9E9\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/Keychron%20K3%20Max.jpeg\", \"price\": 379, \"description\": \"比小顺手感略差些\"}, {\"name\": \"ROG月刃无线AimPoint36k\", \"color\": \"#E9E9E9\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/ROGAimPoint36k.png\", \"price\": 319, \"description\": \"颜值、手感和续航都很 Nice，可惜没有无极滚轮功能\"}], \"category\": \"正在使用的装备\", \"description\": \"这些是我的核心生产力设备\"}, {\"items\": [{\"name\": \"MacBook Air M2\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/MacBook%20Air%20M2.png\", \"price\": 10017, \"description\": \"第四台电脑， 用了半年以 6k 价位卖给了同学， 几乎每个月亏损 1k 🥺\"}, {\"name\": \"华硕灵耀 14\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E5%8D%8E%E7%A1%95%E7%81%B5%E8%80%80%2014.png\", \"price\": 6596, \"description\": \"我的第三台电脑，我比较看重这台电脑的屏幕显示效果。可不知道为什么电脑各方面配置还不错，但经常死机，不夸张的说一天能死机最起码 5 次，只能强制重启。最终用了半年多以 4k 出掉了 😤\"}, {\"name\": \"联想拯救者 R900k\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E8%81%94%E6%83%B3%E6%8B%AF%E6%95%91%E8%80%85%20R900k.jpg\", \"price\": 9298, \"description\": \"我的第二台电脑，买来之后几乎没打过游戏，每天在教室搬来搬去 用了一年左右干脆 5k 卖掉了\"}, {\"name\": \"联想小新 14\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E8%81%94%E6%83%B3%E5%B0%8F%E6%96%B0%2014.jpg\", \"price\": 4279, \"description\": \"我的第一台电脑，用了 2 年以 2700 卖掉，还挺保值的 😌\"}, {\"name\": \"小米 14 Pro\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E5%B0%8F%E7%B1%B3%2014%20Pro.jpg\", \"price\": 4999, \"description\": \"我的第三款小米手机\"}, {\"name\": \"小米 13\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E5%B0%8F%E7%B1%B3%2013.jpg\", \"price\": 3920, \"description\": \"我的第二款小米手机\"}, {\"name\": \"小米 10 至尊纪念版\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E5%B0%8F%E7%B1%B3%2010%20%E8%87%B3%E5%B0%8A%E7%BA%AA%E5%BF%B5%E7%89%88.webp\", \"price\": 5599, \"description\": \"我的第一款小米手机，用了 2 年以 1500 元卖掉了\"}, {\"name\": \"Vivo X23\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/Vivo%20X23.jpg\", \"price\": 2100, \"description\": \"我的第一款安卓手机\"}, {\"name\": \"iPhone SE\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/iPhone%20SE.jpg\", \"price\": 0, \"description\": \"第一次用苹果 🤩\"}, {\"name\": \"HUAWEI FreeBuds SE 2\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/HUAWEI%20FreeBuds%20SE%202.png\", \"price\": 175, \"description\": \"还可以，只可惜不支持降噪和多设备连接\"}, {\"name\": \"漫步者 LOLLI3 ANC\", \"color\": \"#829887\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E6%BC%AB%E6%AD%A5%E8%80%85%20LOLLI3%20ANC.jpg\", \"price\": 469, \"description\": \"最终还是丢了 😭，以后不会再买这么贵的耳机了\"}, {\"name\": \"小度降噪耳机 Pro\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E5%B0%8F%E5%BA%A6%E9%99%8D%E5%99%AA%E8%80%B3%E6%9C%BA%20Pro.webp\", \"price\": 438, \"description\": \"丢了 😭\"}, {\"name\": \"漫步者 LolliPods plus\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E6%BC%AB%E6%AD%A5%E8%80%85%20LolliPods%20plus.jpg\", \"price\": 279, \"description\": \"丢了 😭\"}, {\"name\": \"腹灵 MK800\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E8%85%B9%E7%81%B5%20MK800.webp\", \"price\": 449, \"description\": \"做工和外观都不错，只是按压偏重适合打游戏，最终以 200 元卖给了同学\"}, {\"name\": \"RK987\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/RK987.webp\", \"price\": 273, \"description\": \"键盘进水导致部分按键失灵，但不影响打游戏，后来以 20 元送给了同学\"}, {\"name\": \"联想拯救者 M600\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E8%81%94%E6%83%B3%E6%8B%AF%E6%95%91%E8%80%85%20M600.jpg\", \"price\": 205, \"description\": \"手感还不错，只是过保就坏😠，修的话也不划算就扔掉了\"}, {\"name\": \"联想拯救者无线鼠标 M7\", \"image\": \"https://res.liuyuyang.net/thrive/equipment/%E8%81%94%E6%83%B3%E6%8B%AF%E6%95%91%E8%80%85%E6%97%A0%E7%BA%BF%E9%BC%A0%E6%A0%87%20M7.jpg\", \"price\": 199, \"description\": \"外观和手感都不错，尤其是滚轮支持无极滚动\"}], \"category\": \"以往的设备\", \"description\": \"这些设备已经退役，但它们曾经陪伴我度过了一段美好的时光\"}]}','我的设备');
/*!40000 ALTER TABLE `page_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `record`
--

DROP TABLE IF EXISTS `record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL COMMENT '内容',
  `images` json DEFAULT NULL COMMENT '图片',
  `create_time` bigint NOT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `record_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `record`
--

LOCK TABLES `record` WRITE;
/*!40000 ALTER TABLE `record` DISABLE KEYS */;
INSERT INTO `record` VALUES (1,'Hello World','[]',1736513670072);
/*!40000 ALTER TABLE `record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `swiper`
--

DROP TABLE IF EXISTS `swiper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `swiper` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `url` varchar(500) DEFAULT NULL,
  `order` int NOT NULL DEFAULT '0' COMMENT '排序，越小越靠前',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `swiper`
--

LOCK TABLES `swiper` WRITE;
/*!40000 ALTER TABLE `swiper` DISABLE KEYS */;
INSERT INTO `swiper` VALUES (1,'ThriveX 4.0 还是那个起点，还是那份炽热','','https://bu.dusays.com/2026/06/16/6a30b75a0dad5.jpg','https://github.com/LiuYuYang01/ThriveX-Admin',2);
/*!40000 ALTER TABLE `swiper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (3,'测试标签');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `name` varchar(50) NOT NULL COMMENT '用户名称',
  `email` varchar(100) DEFAULT NULL COMMENT '用户邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `info` varchar(255) DEFAULT NULL COMMENT '用户介绍',
  `create_time` varchar(255) NOT NULL COMMENT '用户创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_pk` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','e10adc3949ba59abbe56e057f20f883e','宇阳','3311118881@qq.com','https://bu.dusays.com/2024/11/17/6739adf188f64.png','ThriveX 博客管理系统作者','1723533206613');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_token`
--

DROP TABLE IF EXISTS `user_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_token` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL COMMENT '用户 ID',
  `token` text NOT NULL COMMENT '用户token',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_token_pk_2` (`id`),
  UNIQUE KEY `user_token_pk_3` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户 token';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_token`
--

LOCK TABLES `user_token` WRITE;
/*!40000 ALTER TABLE `user_token` DISABLE KEYS */;
INSERT INTO `user_token` VALUES (156,1,'eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3ODE4MzU0MzV9.JI0a-IkXj_1AFxTtUV_4fJxjO_eXrR-0FNRLB5L1oG4');
/*!40000 ALTER TABLE `user_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wall`
--

DROP TABLE IF EXISTS `wall`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wall` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT '神秘人' COMMENT '留言人名称',
  `content` varchar(255) NOT NULL COMMENT '留言内容',
  `color` varchar(100) DEFAULT '#ffe3944d' COMMENT '留言墙颜色',
  `email` varchar(100) DEFAULT NULL COMMENT '留言者邮箱',
  `cate_id` int NOT NULL,
  `status` int DEFAULT '0' COMMENT '是否审核',
  `is_choice` int NOT NULL DEFAULT '0' COMMENT '是否为精选留言',
  `create_time` bigint NOT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `wall_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='留言墙';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wall`
--

LOCK TABLES `wall` WRITE;
/*!40000 ALTER TABLE `wall` DISABLE KEYS */;
INSERT INTO `wall` VALUES (104,'ThriveX','Hello','#fcafa24d','3311118881@qq.com',6,1,0,1729231268305),(108,'ThriveX','这是你的第一条留言','#fcafa24d','3311118881@qq.com',6,0,0,1729231268305);
/*!40000 ALTER TABLE `wall` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wall_cate`
--

DROP TABLE IF EXISTS `wall_cate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wall_cate` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `mark` varchar(100) NOT NULL COMMENT '分类标识',
  `order` int NOT NULL COMMENT '分类顺序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `wall_cate_pk_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='留言分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wall_cate`
--

LOCK TABLES `wall_cate` WRITE;
/*!40000 ALTER TABLE `wall_cate` DISABLE KEYS */;
INSERT INTO `wall_cate` VALUES (1,'全部','all',1),(2,'想对我说的话','info',2),(3,'对我的建议','suggest',3),(6,'其他','other',6),(7,'精选','choice',0);
/*!40000 ALTER TABLE `wall_cate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_config`
--

DROP TABLE IF EXISTS `web_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `web_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '配置项名称',
  `value` json NOT NULL COMMENT '配置项值',
  `notes` varchar(255) DEFAULT NULL COMMENT '配置备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `web_page1_pk_2` (`id`),
  UNIQUE KEY `web_page1_pk_3` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='网站配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_config`
--

LOCK TABLES `web_config` WRITE;
/*!40000 ALTER TABLE `web_config` DISABLE KEYS */;
INSERT INTO `web_config` VALUES (1,'web','{\"icp\": \"豫ICP备2020031040号-1\", \"url\": \"https://liuyuyang.net/\", \"title\": \"ThriveX\", \"footer\": \"真诚邀请大家成为 ThriveX 博客管理系统的贡献者，一起参与项目开发，构建一个强大的博客管理系统！\", \"favicon\": \"https://bu.dusays.com/2024/05/03/663481106dcfd.png\", \"keyword\": \"宇阳,刘宇阳,Thrive,前端,Python,Java,Thrive,ThriveX,ThriveX现代化博客管理系统\", \"subhead\": \"现代化博客管理系统\", \"create_time\": 1547568000000, \"description\": \"也许会是最好用的博客管理系统\"}','网站配置'),(2,'theme','{\"covers\": [\"https://bu.dusays.com/2023/11/10/654e2da1d80f8.jpg\", \"https://bu.dusays.com/2023/11/10/654e2d719d31c.jpg\", \"https://bu.dusays.com/2023/11/10/654e2cf92cd45.jpg\", \"https://bu.dusays.com/2023/11/10/654e2cf6055b0.jpg\", \"https://bu.dusays.com/2023/11/10/654e2db0889fe.jpg\", \"https://bu.dusays.com/2023/11/10/654e2d50015a9.jpg\", \"https://bu.dusays.com/2023/11/05/65473848ed863.jpg\", \"https://bu.dusays.com/2023/11/10/654e2c870e280.jpg\", \"https://bu.dusays.com/2023/11/10/654e2c717eb73.jpg\", \"https://bu.dusays.com/2023/11/10/654e2c5d75d5b.jpg\", \"https://bu.dusays.com/2023/11/10/654e2da27801e.jpg\", \"https://bu.dusays.com/2023/11/10/654e2d2a67517.jpg\", \"https://bu.dusays.com/2023/11/10/654e2cf47f17a.jpg\", \"https://bu.dusays.com/2023/11/05/65473848ed863.jpg\"], \"social\": [{\"url\": \"https://github.com/LiuYuYang01\", \"name\": \"GitHub\"}, {\"url\": \"https://gitee.com/liu_yu_yang666\", \"name\": \"Gitee\"}, {\"url\": \"https://juejin.cn/user/3083456627092078/posts\", \"name\": \"Juejin\"}, {\"url\": \"https://blog.csdn.net/haodian666?type=blog\", \"name\": \"CSDN\"}, {\"url\": \"http://wpa.qq.com/msgrd?v=3&uin=3311118881&site=qq&menu=yes\", \"name\": \"QQ\"}], \"dark_logo\": \"https://bu.dusays.com/2024/05/03/663481106dcfd.png\", \"light_logo\": \"https://bu.dusays.com/2024/05/03/663481106e2a4.png\", \"record_info\": \"🎯 梦想做一名技术顶尖的架构师，奈何学历太低！\", \"record_name\": \"👋 Liu 宇阳\", \"swiper_text\": [\"System.out.print(\\\"欢迎使用 ThriveX 博客管理系统！\\\");\", \"print(\\\"这是一个 Nextjs + Spring Boot 的产物\\\")\"], \"reco_article\": [1, 2], \"swiper_image\": \"https://bu.dusays.com/2024/04/24/6628990012b51.jpg\", \"right_sidebar\": [\"author\", \"hotArticle\", \"newComments\", \"randomArticle\", \"runTime\", \"study\"], \"is_article_layout\": \"classics\"}','主题配置'),(3,'other','{}','其他配置'),(5,'file','{\"upload_compress_mode\": \"auto\"}','文件上传压缩策略');
/*!40000 ALTER TABLE `web_config` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-20 17:21:16
