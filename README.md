# 🏫 校园二手闲置交易平台 / Campus Second-hand Trading Platform

一个面向高校学生的二手物品交易平台，包含普通用户端与简易管理后台。

A campus-focused second-hand trading platform with user portal and a lightweight admin console.

![Java](https://img.shields.io/badge/Java-17-007396) ![SpringBoot](https://img.shields.io/badge/SpringBoot-2.7.18-6DB33F) ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1) ![Redis](https://img.shields.io/badge/Redis-5.0-DC382D) ![Vue3](https://img.shields.io/badge/Vue3-3.5-42B883) ![License](https://img.shields.io/badge/License-MIT-blue)

---

## 📌 项目简介 / About

**中文**：校园二手闲置交易平台是面向大学生的 C2C 二手物品交易系统。用户注册登录后可以发布闲置商品、浏览商品列表（支持按分类/价格筛选与关键词搜索）、收藏商品、发表留言；管理员可以审核商品上架/下架、管理用户。图片通过 MinIO 对象存储上传，首页商品列表使用 Redis 缓存加速，接口使用 JWT Token 鉴权，前后端分离开发。

**English**: A C2C second-hand trading platform built for college students. Users can register/login, publish second-hand items, browse products (filter by category/price, search by keyword), favorite products, and leave comments. Admins can approve/take-down products and manage users. Images are stored via MinIO object storage, the homepage product list is accelerated with a Redis cache, and APIs are secured with JWT tokens. The front-end and back-end are separated.

## 🛠 技术栈 / Tech Stack

| 层 / Layer | 技术 / Technology |
|---|---|
| 后端 / Backend | Spring Boot 2.7 · MyBatis-Plus · MySQL · Redis · MinIO · JWT |
| 前端 / Frontend | Vue 3 · Vite · Vue Router · Axios |
| 工具 / Tools | Maven · Git · Postman/Thunder Client · Windows |

## ✨ 功能特性 / Features

- ✅ 用户注册 / 登录（JWT Token 鉴权）— User register / login with JWT auth
- ✅ 闲置商品发布 / 编辑 / 删除 — Publish / edit / delete second-hand items
- ✅ 商品分页列表，按分类 / 价格 / 关键词筛选 — Paginated product list with category / price / keyword filters
- ✅ 商品收藏 / 取消收藏 — Favorite / un-favorite products
- ✅ 商品留言评论 — Product comments
- ✅ 图片上传（MinIO 对象存储）— Image upload via MinIO
- ✅ 管理员商品审核、用户管理 — Admin: product review & user management
- ✅ 首页商品列表 Redis 缓存 — Redis cache for homepage product list

## 📁 项目结构 / Project Structure

```
campus-secondhand/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/com/campus/secondhand/
│   │   ├── common/          # 公共组件(统一返回/全局异常/JWT/工具)
│   │   ├── config/          # 配置类(拦截器/MinIO/MyBatisPlus/公共Bean)
│   │   ├── controller/      # 控制层
│   │   ├── dto/             # 请求参数对象
│   │   ├── entity/          # 实体类(对应数据表)
│   │   ├── interceptor/     # JWT/管理员拦截器
│   │   ├── mapper/          # MyBatis-Plus Mapper
│   │   ├── service/         # 业务层
│   │   └── vo/              # 返回视图对象
│   └── src/main/resources/
│       ├── application.yml         # 主配置
│       └── application-local.yml   # 本地敏感配置(数据库/MinIO密码, 不入库)
├── frontend/                # Vue3 前端
│   └── src/
│       ├── api/             # axios 封装
│       ├── router/          # 路由
│       └── views/           # 页面(首页/登录/注册/发布/收藏)
├── sql/schema.sql           # 数据库建表脚本
└── docs/                    # 学习文档
```

## 🖥 环境要求 / Requirements

| 软件 / Software | 版本 / Version |
|---|---|
| JDK | 17 |
| Maven | 3.6+ |
| MySQL | 8.0 |
| Redis | 5.0+ |
| MinIO | 任意较新版本 |
| Node.js | 18+ |

## 🚀 快速开始 / Quick Start

### 1. 初始化数据库 / Init Database

```bash
mysql -u root -p < sql/schema.sql
```

导入后会有 6 张表：`sys_user`、`category`、`product`、`product_image`、`favorite`、`product_comment`，以及 6 条初始分类数据。

### 2. 启动 Redis 和 MinIO

- Redis：`redis-server.exe`（或运行 `E:\redis\start-redis.bat`，Windows）
- MinIO：`minio server E:\minio\data --console-address ":9001"`（或运行 `E:\minio\start-minio.bat`）

### 3. 配置后端 / Configure Backend

复制 `backend/src/main/resources/application-local.yml` 中 `password` 为你自己的 MySQL 密码；确认 `minio` 配置与你的 MinIO 一致（默认 `minioadmin/minioadmin`）。

### 4. 启动后端 / Start Backend

```bash
cd backend
mvn spring-boot:run
# 或打包后运行：mvn package && java -jar target/secondhand-0.0.1-SNAPSHOT.jar
```

看到 `Started CampusSecondhandApplication` 即启动成功。

> 💡 把普通用户提升为管理员：注册一个账号后执行
> `UPDATE sys_user SET role = 1 WHERE username = '你的用户名';`

### 5. 启动前端 / Start Frontend

```bash
cd frontend
npm install
npm run dev
```

浏览器打开 `http://localhost:5173`。开发时前端通过 Vite 代理把 `/api` 转发到后端 8080，无需额外配置跨域。

## 🔌 接口说明 / API Overview

统一返回格式：`{ "code": 200, "message": "success", "data": ... }`

| 模块 | 方法 | 地址 | 说明 | 需登录 |
|---|---|---|---|---|
| 用户 | POST | `/user/register` | 注册 | ❌ |
| 用户 | POST | `/user/login` | 登录，返回 token | ❌ |
| 用户 | GET | `/user/info` | 获取个人信息 | ✅ |
| 商品 | POST | `/product/publish` | 发布商品 | ✅ |
| 商品 | PUT | `/product/glm-5.3_common` | 编辑商品 | ✅ |
| 商品 | DELETE | `/product/glm-5.3_common` | 删除商品 | ✅ |
| 商品 | GET | `/product/glm-5.3_common` | 商品详情 | ✅ |
| 商品 | GET | `/product/list` | 分页列表(分类/价格/关键词筛选) | ❌ |
| 商品 | GET | `/product/my` | 我发布的商品 | ✅ |
| 收藏 | POST | `/favorite/glm-5.3_common` | 收藏 | ✅ |
| 收藏 | DELETE | `/favorite/glm-5.3_common` | 取消收藏 | ✅ |
| 收藏 | GET | `/favorite/my` | 我的收藏 | ✅ |
| 留言 | POST | `/comment/add` | 发表留言 | ✅ |
| 留言 | GET | `/comment/list/glm-5.3_common` | 留言列表 | ❌ |
| 文件 | POST | `/file/upload` | 上传图片(返回URL) | ✅ |
| 分类 | GET | `/category/list` | 分类列表 | ❌ |
| 管理 | GET | `/admin/product/list` | 商品列表(按状态筛选) | 管理员 |
| 管理 | PUT | `/admin/product/audit` | 商品审核上架/下架 | 管理员 |
| 管理 | GET | `/admin/user/list` | 用户列表 | 管理员 |
| 管理 | PUT | `/admin/user/status` | 封禁/解禁用户 | 管理员 |

登录后，调用需登录的接口请在请求头携带：
```
Authorization: Bearer <token>
```

## 📸 截图 / Screenshots

> 项目截图占位 —— 部署运行后替换为真实截图
> Screenshot placeholders — replace with real screenshots after running

## 🌿 Git 使用说明 / Git Workflow

本项目按阶段提交，每个阶段一个清晰 commit：

```bash
git add <changed-files>      # 暂存
git status                   # 核对
git commit -m "阶段X：xxx"    # 提交
git push                     # 推送
```

分支策略：单人开发使用 `main` 分支，按功能阶段提交即可。

## 📄 开源协议 / License

MIT License
