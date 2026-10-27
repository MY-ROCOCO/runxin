# 前端构建产物

这里放的是当前运行环境的**前端构建产物**，不是前端源码。服务器上只有构建后的 `dist` / `.output`，没有前端工程源码。

| 文件 | 说明 |
|---|---|
| `admin-dist.zip` | 管理端，解压后是 `dist/` |
| `member-output.zip` | 用户端 Nuxt 产物，解压后是 `.output/` |

## 管理端

```bash
unzip admin-dist.zip -d /www/wwwroot/guanli.example.com
# 实际入口: /www/wwwroot/guanli.example.com/dist/index.html
```

Nginx 示例：

```nginx
server {
    listen 80;
    server_name guanli.example.com;

    root /www/wwwroot/guanli.example.com/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /admin/ {
        proxy_pass http://127.0.0.1:82;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /member/ {
        proxy_pass http://127.0.0.1:82;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

管理端构建里仍保留了原厂的社区/官网/版权链接（`www.reckonkvm.com`），以及“检查更新”等入口。要做完全独立的品牌，需要拿前端源码重新构建。

## 用户端

```bash
unzip member-output.zip -d /www/wwwroot/member.example.com
cd /www/wwwroot/member.example.com
PORT=3000 HOST=0.0.0.0 node .output/server/index.mjs
```

Nginx 反代：

```nginx
server {
    listen 80;
    server_name www.example.com;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /member/ {
        proxy_pass http://127.0.0.1:82;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 注意

- 构建产物是压缩/混淆后的代码，不建议直接改。
- 用户端 DNS 线路查询接口已从厂商地址改为同源 `/api/v1/geo/lines`，需要你自己实现或反代这个接口；否则会回退到默认线路列表。
- 管理端/用户端的 API 域名、支付回调、对象存储、实名认证等，需要在管理端配置或重新构建前端。
- 生产环境请修改默认管理员密码，不要直接使用仓库里的初始密码。
- 如果需要可维护的前端源码，请把前端工程单独放入仓库或提供源码地址，替换这里的构建包。
