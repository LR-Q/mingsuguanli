# 悦鑫乐怡民宿管理系统

## 项目简介

悦鑫乐怡民宿管理系统是一个现代化的民宿管理解决方案，采用前后端分离架构，提供完整的民宿运营管理功能。

## 技术栈

### 后端 (yxly-backend)
- **框架**: Spring Boot 2.7.x
- **安全**: Spring Security + JWT
- **数据访问**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **文档**: Swagger 3.x (SpringDoc)
- **构建工具**: Maven 3.8+
- **JDK版本**: OpenJDK 11+

### 前端 (yxly-frontend)
- **框架**: Vue 3.x (Composition API)
- **构建工具**: Vite 4.x
- **UI组件库**: Element Plus
- **路由管理**: Vue Router 4.x
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **CSS预处理器**: Sass/SCSS

## 功能模块

### 核心功能
- 🏠 **房间管理** - 房间信息管理、房型配置、房间状态管理
- 📋 **订单管理** - 预订管理、入住退房、订单状态跟踪
- 👥 **客户管理** - 客户信息管理、客户关系维护
- 💰 **财务管理** - 收支记录、财务统计、报表分析
- 💬 **评论管理** - 客户评价管理、回复功能
- ⚙️ **系统管理** - 用户权限、系统配置、操作日志

### 系统特性
- 🔐 **安全认证** - JWT无状态认证、角色权限控制
- 📱 **响应式设计** - 支持PC、平板、手机多端访问
- 🎨 **现代化UI** - 基于Element Plus的美观界面
- 🚀 **高性能** - 前后端分离、接口缓存优化
- 📊 **数据统计** - 实时统计分析、可视化图表
- 🔍 **操作日志** - 完整的操作审计追踪

## 项目结构

```
悦鑫乐怡民宿管理系统/
├── yxly-backend/          # 后端项目
│   ├── src/main/java/com/yxly/
│   │   ├── config/        # 配置类
│   │   ├── controller/    # 控制器层
│   │   ├── service/       # 业务服务层
│   │   ├── mapper/        # 数据访问层
│   │   ├── entity/        # 实体类
│   │   ├── dto/           # 数据传输对象
│   │   ├── vo/            # 视图对象
│   │   ├── enums/         # 枚举类
│   │   ├── exception/     # 异常处理
│   │   ├── security/      # 安全相关
│   │   ├── utils/         # 工具类
│   │   └── common/        # 公共类
│   └── src/main/resources/
│       ├── application.yml    # 主配置文件
│       ├── application-dev.yml   # 开发环境配置
│       ├── application-prod.yml  # 生产环境配置
│       └── mapper/        # MyBatis映射文件
├── yxly-frontend/         # 前端项目
│   ├── src/
│   │   ├── api/           # API接口定义
│   │   ├── assets/        # 静态资源
│   │   ├── components/    # 公共组件
│   │   ├── layouts/       # 布局组件
│   │   ├── pages/         # 页面组件
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # 状态管理
│   │   ├── utils/         # 工具函数
│   │   ├── App.vue        # 根组件
│   │   └── main.js        # 入口文件
│   ├── package.json       # 项目依赖
│   └── vite.config.js     # Vite配置
└── README.md              # 项目说明
```

## 快速开始

### 环境要求
- Node.js 16+
- Java 11+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 后端启动

1. **克隆项目**
```bash
git clone <repository-url>
cd yxly-backend
```

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE yxly_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置文件**
```yaml
# src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yxly_dev
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

4. **启动应用**
```bash
mvn spring-boot:run
```

访问地址：
- 应用: http://localhost:8080/api
- 接口文档: http://localhost:8080/api/swagger-ui.html
- 数据库监控: http://localhost:8080/api/druid/

### 前端启动

1. **进入前端目录**
```bash
cd yxly-frontend
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

访问地址: http://localhost:3000

## 开发规范

### 代码规范
- **后端**: 遵循阿里巴巴Java开发规范
- **前端**: 使用ESLint + Prettier进行代码格式化
- **提交**: 使用约定式提交规范

### 分支管理
- `main`: 主分支，用于生产环境
- `develop`: 开发分支，用于集成测试
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复分支

### 设计原则
项目严格遵循以下设计原则：
- **KISS** (Keep It Simple, Stupid) - 简单至上
- **YAGNI** (You Aren't Gonna Need It) - 精益求精
- **SOLID** - 面向对象设计原则
- **DRY** (Don't Repeat Yourself) - 杜绝重复

## 部署说明

### Docker部署
```bash
# 构建后端镜像
cd yxly-backend
docker build -t yxly-backend .

# 构建前端镜像
cd yxly-frontend
docker build -t yxly-frontend .

# 使用Docker Compose启动
docker-compose up -d
```

### 传统部署
1. **后端**: 打包为JAR文件，使用systemd管理
2. **前端**: 构建静态文件，部署到Nginx
3. **数据库**: MySQL主从配置
4. **缓存**: Redis集群配置

## API文档

系统提供完整的RESTful API，支持：
- 用户认证与授权
- 房间管理CRUD操作
- 订单全生命周期管理
- 客户信息管理
- 财务数据统计
- 系统日志查询

详细API文档请访问：http://localhost:8080/api/swagger-ui.html

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目负责人: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [项目仓库地址]

## 更新日志

### v1.0.0 (2024-01-01)
- 🎉 项目初始化
- ✨ 完成基础架构搭建
- 🔧 配置开发环境
- 📝 创建项目文档