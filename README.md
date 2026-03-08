<p align="center">
    <a href="https://liuyuyang.net" target="_blank">
        <img width="120" src="https://bu.dusays.com/2024/11/17/6739adf188f64.png" alt="ThriveX logo" />
    </a>
</p>

<h1 align="center" style="margin: 20px 0; font-weight: 700; padding-bottom:10px;">ThriveX Server</h1>


<p align="center">
    <a href="https://github.com/LiuYuYang01/ThriveX-Server/blob/main/LICENSE" target="_blank">
        <img alt="License: AGPL-3.0" src="https://img.shields.io/badge/License-AGPL--3.0-blue.svg?style=flat-square&logo=gnu" />
    </a>
    <a href="https://github.com/LiuYuYang01/ThriveX-Server/stargazers" target="_blank">
        <img alt="Stars" src="https://img.shields.io/github/stars/LiuYuYang01/ThriveX-Server?style=flat-square&logo=github&color=gold" />
    </a>
    <a href="https://github.com/LiuYuYang01/ThriveX-Server/network" target="_blank">
        <img alt="Forks" src="https://img.shields.io/github/forks/LiuYuYang01/ThriveX-Server?style=flat-square&logo=github" />
    </a>
</p>

## 📖 项目简介

**ThriveX Server** 是 ThriveX 博客系统的后端服务端，采用 Spring Boot 构建，提供 RESTful API 接口服务。

作为 ThriveX 全栈解决方案的核心部分，Server 后端与前端展示端（[ThriveX-Blog](https://github.com/LiuYuYang01/ThriveX-Blog)）和控制端（[ThriveX-Admin](https://github.com/LiuYuYang01/ThriveX-Admin)）共同构成了一个完整的开源博客生态系统。



## ✨ 核心特性

- 🚀 **高性能架构**：基于 Spring Boot 框架，提供稳定可靠的 API 服务
- 🗄️ **数据持久化**：集成 MyBatis-Plus ORM，高效操作 MySQL 数据库
- ⚡ **缓存加速**：Redis 缓存机制，大幅提升系统响应速度
- 🔐 **安全认证**：完善的权限认证机制，保障系统安全
- 📦 **文件管理**：集成 X File Storage，支持多种存储策略
- 📊 **数据统计**：支持百度统计和高德地图数据集成
- 🐳 **容器化部署**：支持 Docker 容器化部署，简化运维流程
- 📖 **API 文档**：集成 Swagger，提供完善的 API 文档说明



## 📸 项目预览

<div align="center">
    <img src="https://bu.dusays.com/2026/03/08/69ad27c95c51f.jpg" alt="ThriveX Architecture" style="border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1)" />
</div>



## 🚀 快速开始

https://docs.liuyuyang.net/docs/项目部署/1Panel.html



## 📂 项目结构

```
ThriveX-Server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── net/
│   │   │       └── liuyuyang/
│   │   │           └── thrivex/
│   │   │               ├── ThriveXApplication.java
│   │   │               ├── config/          # 配置类
│   │   │               ├── controller/      # 控制器层
│   │   │               ├── service/         # 服务层
│   │   │               ├── mapper/          # 数据访问层
│   │   │               ├── entity/          # 实体类
│   │   │               ├── dto/             # 数据传输对象
│   │   │               ├── vo/              # 视图对象
│   │   │               ├── util/            # 工具类
│   │   │               └── aspect/          # 切面编程
│   │   └── resources/
│   │       ├── mapper/          # MyBatis Mapper XML
│   │       ├── application.yml  # 应用配置
│   │       └── application-dev.yml # 开发环境配置
│   └── test/
│       └── java/                # 单元测试
├── docker/                      # Docker 相关配置
├── docs/                        # 文档
├── sql/                         # 数据库脚本
├── pom.xml                      # Maven 配置
└── README.md                    # 项目说明
```



## 🌐 项目链接

| 名称        | 链接                                                         | 说明         |
| ----------- | ------------------------------------------------------------ | ------------ |
| 博客预览    | [https://liuyuyang.net](https://liuyuyang.net)                | 前端博客展示 |
| 官网地址    | [https://thrivex.liuyuyang.net](https://thrivex.liuyuyang.net)  | 项目官网     |
| 文档中心    | [https://docs.liuyuyang.net](https://docs.liuyuyang.net)      | 使用文档     |
| GitHub 主页 | [https://github.com/LiuYuYang01/ThriveX-Server](https://github.com/LiuYuYang01/ThriveX-Server)  | 源码仓库     |



## 🔗 相关仓库

| 名称        | 链接                                                         | 说明         |
| ----------- | ------------------------------------------------------------ | ------------ |
| 前端展示端  | [https://github.com/LiuYuYang01/ThriveX-Blog](https://github.com/LiuYuYang01/ThriveX-Blog)  | 博客前端     |
| 控制端      | [https://github.com/LiuYuYang01/ThriveX-Admin](https://github.com/LiuYuYang01/ThriveX-Admin)  | 管理后台     |



## 📝 开源协议

本项目采用 **AGPL-3.0** 许可证。

**使用须知**：

- ✅ 允许商业使用、修改、分发
- ✅ 必须保留原始版权说明
- ✅ 修改后的版本必须开源
- ❌ 禁止任何闭源商业行为

在项目 Star 突破 2K 后，您可以自由选择保留或删除版权信息。



## ⭐ Star History

[![Star History Chart](https://api.star-history.com/svg?repos=LiuYuYang01/ThriveX-Server&type=Date)](https://star-history.com/#LiuYuYang01/ThriveX-Server&Date)



## 👨‍💻 作者信息

**刘宇阳**

- GitHub: [@LiuYuYang01](https://github.com/LiuYuYang01)
- 我的博客: [https://liuyuyang.net](https://liuyuyang.net)
- 关于我： [https://my.liuyuyang.net](https://my.liuyuyang.net)
- 邮箱: [liuyuyang1024@yeah.net](mailto:liuyuyang1024@yeah.net)



## 💬 交流群

欢迎加入 ThriveX 官方交流群，与开发者和其他用户交流：

<div align="center">
    <img src="https://bu.dusays.com/2025/06/03/683e96eb43ad8.jpg" alt="WeChat Group" style="width: 300px; border-radius: 8px;" />
</div>

**加群方式**：添加微信 `liuyuyang2023`，备注 "ThriveX"



## 🙏 鸣谢

感谢所有为 ThriveX 项目做出贡献的开发者和用户！

特别感谢以下项目提供的灵感与技术支持：

- [https://blog.zwying.com/](https://blog.zwying.com/)
- [https://www.blatr.cn/](https://www.blatr.cn/)
- [https://poetize.cn/](



## 🔒 免责声明

本项目仅供学习交流使用，不提供任何技术咨询或技术支持服务。使用者在使用本项目时应遵守当地法律法规，不得用于任何违法活动。