# 悦鑫乐怡民宿管理系统 - 管理后台

## 项目说明

这是悦鑫乐怡民宿管理系统的**管理后台**，包含：
- **民宿管理员功能**：房间管理、订单管理、客户管理、提现申请等
- **超级管理员功能**：商户审核、充值管理、提现审批等

> **注意**：普通用户请访问用户端 (yxly-frontend)

## 技术栈

- Vue 3
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

管理后台将运行在：**http://localhost:3001**

### 3. 构建生产版本

```bash
npm run build
```

## 登录账号

### 超级管理员
- 账号：`superadmin@yxly.com`
- 密码：见项目文档

### 民宿管理员
- 需要先在用户端注册民宿主账号
- 等待超级管理员审核通过

## 项目结构

```
yxly-admin-frontend/
├── src/
│   ├── api/              # API 接口
│   ├── assets/           # 静态资源
│   ├── layouts/          # 布局组件
│   ├── pages/            # 页面组件
│   │   ├── admin/        # 民宿管理员页面
│   │   ├── super-admin/  # 超级管理员页面
│   │   ├── dashboard/    # 仪表盘
│   │   ├── bookings/     # 订单管理
│   │   ├── rooms/        # 房间管理
│   │   └── auth/         # 登录页面
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── utils/            # 工具函数
│   └── main.js           # 入口文件
├── package.json
└── vite.config.js
```

## 与用户端的关系

- **用户端 (yxly-frontend)**：端口 3000，面向普通用户
- **管理后台 (yxly-admin-frontend)**：端口 3001，面向管理员

两个项目可以**同时运行**，支持多账号同时登录。

## 注意事项

1. 管理后台仅限管理员访问，普通用户无法登录
2. 开发时需要同时启动后端服务 (默认端口 8080)
3. 路由权限由角色代码控制：
   - `SUPER_ADMIN`: 超级管理员
   - `HOMESTAY_ADMIN`: 民宿管理员
