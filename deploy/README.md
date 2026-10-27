# 部署说明

## 目录

```text
deploy/
├── application-prod.yml   # 脱敏后的生产配置示例
├── deploy.sh              # 后端启停脚本
└── README.md
```

## 1. 准备环境

- JDK 17
- MySQL 8.0
- Redis 6+
- Nginx（前端）

创建数据库：

```sql
CREATE DATABASE runxinyun DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

## 2. 后端配置

```bash
cp deploy/application-prod.yml /www/wwwroot/data/application-prod.yml
```

敏感项全部走环境变量，至少设置：

```bash
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_NAME=runxinyun
export DB_USERNAME=runxinyun
export DB_PASSWORD='your-password'
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_PASSWORD=''
export SERVER_PORT=82
```

Druid/Knife4j 密码：

```bash
export DRUID_LOGIN_PASSWORD='change-me'
export DRUID_PUBLIC_KEY=''
export KNIFE4J_PASSWORD='change-me'
```

## 3. 启动后端

```bash
cp deploy/deploy.sh /www/wwwroot/data/deploy.sh
chmod +x /www/wwwroot/data/deploy.sh

cd /www/wwwroot/data
APP_HOME=/www/wwwroot/data \
APP_PORT=82 \
CONFIG_FILE=/www/wwwroot/data/application-prod.yml \
JAVA_BIN=/path/to/java \
./deploy.sh start
```

常用命令：

```bash
./deploy.sh status
./deploy.sh restart
./deploy.sh stop
```

日志默认在 `$APP_HOME/app.log`，PID 在 `$APP_HOME/app.pid`。

## 4. 前端

见 [frontend/README.md](../frontend/README.md)。

## 5. 数据库迁移

- 全新数据库：Flyway 会自动执行 `finance-web-app/src/main/resources/db/migration`。
- 从旧库升级：开源版清理过历史迁移，可能校验和不一致。先备份，再按需设置：
  ```yaml
  spring:
    flyway:
      validate-on-migrate: false
  ```
  或直接使用全新数据库。
- 不要在已有生产库上直接关闭校验后长期运行；确认结构后再恢复校验。

## 6. 安全

- 修改默认管理员密码
- 不要公网暴露 Druid、Knife4j
- 使用 HTTPS、反向代理、IP 白名单
- 数据库/Redis/支付/短信密钥全部走环境变量
- 上线前做权限和支付回调回归
