# IDC Finance Backend

一个面向 IDC / 云服务商的销售、计费、资源与运营管理后端。提供管理端、会员端、代理端、开放 API，以及云服务器、独立服务器、虚拟主机、域名、DNS、订单、支付、工单、营销、权限等模块。

> 本仓库为开源版本，已移除授权校验，克隆后可直接构建运行。生产使用请自行完成安全加固、配置第三方支付/短信/实名服务，并遵守当地法律法规。

## 技术栈

- Java 17
- Spring Boot 3.2.1
- MyBatis-Plus 3.5.5
- Sa-Token（管理端 / 会员端双会话）
- Flyway（数据库迁移）
- MySQL 8.0
- Redis
- Druid
- Knife4j / OpenAPI
- Maven 多模块

## 模块

| 模块 | 说明 |
|---|---|
| `finance-web-app` | 启动模块、全局配置、过滤器、打包入口 |
| `finance-common` | 公共实体、工具、异常、缓存、基础能力 |
| `finance-admin-module` | 管理端：认证、系统、会员、订单、业务、生产、财务、营销、工单、开发工具 |
| `finance-member-module` | 会员端：认证、订单、云服务器、虚拟主机、独立服务器、DNS、域名、支付、工单、工具 |
| `finance-agent-module` | 代理端：代理账号、会员绑定、分润 |
| `finance-plugin-api` | 各模块对外的 API/插件接口 |

## 环境要求

- JDK 17
- Maven 3.9+
- MySQL 8.0
- Redis 6+

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE runxinyun DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

Flyway 会在首次启动时自动创建表并写入基础数据。

### 2. 配置环境变量

```bash
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_NAME=runxinyun
export DB_USERNAME=runxinyun
export DB_PASSWORD=your-db-password

export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_PASSWORD=

export SERVER_PORT=82
```

也可以复制 `finance-web-app/src/main/resources/application-prod.yml`，通过外部配置启动：

```bash
java -Dspring.config.location=/path/to/application-prod.yml \
     -jar finance-web-app/target/finance-web-app-1.0.0.jar
```

### 3. 构建

```bash
mvn clean package -DskipTests
```

### 4. 启动

```bash
java -jar finance-web-app/target/finance-web-app-1.0.0.jar
```

- 首页：`http://127.0.0.1:82/`
- 接口文档：`http://127.0.0.1:82/doc.html`

### 5. 初始管理员

首次启动会写入两个管理员账号：

| 账号 | 初始密码 | 角色 |
|---|---|---|
| `superAdmin` | `admin123` | 超级管理员 |
| `bizAdmin` | `admin123` | 业务管理员 |

> **首次登录后请立即修改密码。** 如果用于公网，请在初始化前修改 `V1_0__init_tables.sql` 中的密码哈希，或启动后通过管理端重置。

## 配置说明

主要配置位于 `finance-web-app/src/main/resources/application.yml`，敏感项全部使用环境变量：

| 环境变量 | 说明 |
|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | MySQL 地址、端口、库名 |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL 账号密码 |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | Redis |
| `SERVER_PORT` | 服务端口，默认 82 |
| `DRUID_LOGIN_PASSWORD` | Druid 监控页密码 |
| `KNIFE4J_PASSWORD` | Knife4j 文档页密码 |
| `UPDATE_SERVER_URL` | 可选，在线更新服务地址 |

> 仓库不包含任何生产数据库、Redis、支付、短信、对象存储、实名认证的密钥。请在部署时通过环境变量或管理端配置。

## 第三方服务

以下能力需要接入你自己的服务商，代码中保留了接口和默认实现：

- 支付宝 / 微信支付 / 易支付
- 阿里云 / 腾讯云短信、邮件、对象存储
- 实名认证（支付宝人脸、MCloud、Reckonkvm 等实现可按需替换）
- DNS Agent 安装脚本与节点
- 在线更新服务

第三方回调地址、密钥、模板 ID 请通过管理端“系统配置”或环境变量配置，不要提交到仓库。

## 目录结构

```text
backend/
├── finance-web-app/          # 启动模块
├── finance-common/           # 公共模块
├── finance-admin-module/     # 管理端
├── finance-member-module/    # 会员端
├── finance-agent-module/     # 代理端
├── finance-plugin-api/       # 插件/API 接口
├── pom.xml
├── LICENSE
└── README.md
```

## 开发

```bash
# 只编译
mvn -DskipTests compile

# 运行某个模块的测试
mvn -pl finance-common test

# 打包
mvn -DskipTests clean package
```

数据库迁移文件位于：

```text
finance-web-app/src/main/resources/db/migration
```

新增表结构请新增 `Vx_y__xxx.sql`，不要修改已发布的历史迁移。

## 安全建议

1. 修改默认管理员密码
2. 不要公网暴露 Druid、Knife4j、管理端
3. 使用 HTTPS 和反向代理
4. 数据库、Redis、支付、短信密钥全部走环境变量/密钥管理
5. 定期备份数据库
6. 根据业务需要配置 IP 白名单、WAF、限流
7. 上线前做一次依赖漏洞扫描和权限回归

## 许可证

本项目使用 [Apache License 2.0](LICENSE)。

## 贡献

欢迎提交 Issue 和 Pull Request。提交前请确保：

- 不包含任何真实密钥、客户数据、生产地址
- 新增代码有必要的注释
- 数据库变更使用新的 Flyway 迁移文件
- 不引入与项目许可证不兼容的依赖
