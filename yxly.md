1. 系统架构
系统采用前后端分离的架构，前端使用Vue框架，后端使用SpringBoot框架，通过RESTful API进行数据交互。数据库使用MySQL，同时可以考虑使用Redis进行缓存。项目部署时，前端使用Nginx作为静态服务器，后端使用Tomcat容器。

2. 功能模块
根据民宿管理系统的需求，我们设计以下功能模块：

2.1 用户管理模块
用户注册、登录（支持手机号/邮箱登录）

用户信息管理（修改个人信息、头像上传）

用户权限管理（区分普通用户、管理员）

2.2 民宿房间管理模块
房间信息管理（增删改查）

房间类型管理（如单人间、双人间等）

房间图片上传与展示

房间状态管理（如可预订、已预订、维修中）

2.3 订单管理模块
订单创建（用户下单）

订单查询（用户和管理员都可以查询）

订单状态管理（待支付、已支付、已入住、已完成、已取消等）

订单取消处理

2.4 评论管理模块
用户对已入住的订单进行评论

评论列表展示

评论回复（管理员可以回复用户的评论）

2.5 系统管理模块
数据统计（如订单统计、房间入住率等）

日志管理

系统设置（如民宿信息、联系方式等）

## 3. 完整数据库设计

### 3.1 数据库概述
- **数据库类型**: MySQL 8.0+
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **存储引擎**: InnoDB
- **事务支持**: 完全支持ACID特性

### 3.2 核心数据表设计

#### 3.2.1 用户表 (sys_user)
```sql
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `email_verified` TINYINT DEFAULT 0 COMMENT '邮箱验证状态',
  `phone_verified` TINYINT DEFAULT 0 COMMENT '手机验证状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 3.2.2 角色表 (sys_role)
```sql
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(20) NOT NULL COMMENT '角色编码',
  `description` TEXT COMMENT '角色描述',
  `permissions` JSON COMMENT '权限列表',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

#### 3.2.3 房间类型表 (room_type)
```sql
CREATE TABLE `room_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '房型ID',
  `type_name` VARCHAR(50) NOT NULL COMMENT '房型名称',
  `type_code` VARCHAR(20) NOT NULL COMMENT '房型编码',
  `description` TEXT COMMENT '房型描述',
  `base_price` DECIMAL(10,2) NOT NULL COMMENT '基础价格',
  `max_guests` INT DEFAULT 2 COMMENT '最大入住人数',
  `bed_type` VARCHAR(50) DEFAULT NULL COMMENT '床型',
  `area` DECIMAL(5,2) DEFAULT NULL COMMENT '面积(平方米)',
  `facilities` JSON COMMENT '标准设施',
  `images` JSON COMMENT '房型图片',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间类型表';
```

#### 3.2.4 房间表 (room_info)
```sql
CREATE TABLE `room_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
  `room_type_id` BIGINT NOT NULL COMMENT '房型ID',
  `floor_number` INT DEFAULT NULL COMMENT '楼层',
  `area` DECIMAL(5,2) DEFAULT NULL COMMENT '面积(平方米)',
  `bed_type` VARCHAR(50) DEFAULT NULL COMMENT '床型',
  `max_guests` INT DEFAULT 2 COMMENT '最大入住人数',
  `base_price` DECIMAL(10,2) NOT NULL COMMENT '基础价格',
  `current_price` DECIMAL(10,2) NOT NULL COMMENT '当前价格',
  `status` TINYINT DEFAULT 1 COMMENT '房间状态(1:可用 2:占用 3:维修 4:清洁 5:停用)',
  `facilities` JSON COMMENT '房间设施',
  `description` TEXT COMMENT '房间描述',
  `images` JSON COMMENT '房间图片',
  `wifi_password` VARCHAR(50) DEFAULT NULL COMMENT 'WiFi密码',
  `last_clean_time` DATETIME DEFAULT NULL COMMENT '最后清洁时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_number` (`room_number`),
  KEY `idx_room_type_id` (`room_type_id`),
  KEY `idx_status` (`status`),
  KEY `idx_floor_number` (`floor_number`),
  CONSTRAINT `fk_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间信息表';
```

#### 3.2.5 客户表 (customer_info)
```sql
CREATE TABLE `customer_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `customer_no` VARCHAR(32) DEFAULT NULL COMMENT '客户编号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` TINYINT DEFAULT NULL COMMENT '性别(1:男 2:女)',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `total_consumption` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费',
  `visit_count` INT DEFAULT 0 COMMENT '入住次数',
  `last_visit_time` DATETIME DEFAULT NULL COMMENT '最后入住时间',
  `preferences` JSON COMMENT '客户偏好',
  `remarks` TEXT COMMENT '备注',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:黑名单 1:正常)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_no` (`customer_no`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';
```

#### 3.2.6 预订订单表 (booking_order)
```sql
CREATE TABLE `booking_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `room_id` BIGINT NOT NULL COMMENT '房间ID',
  `check_in_date` DATE NOT NULL COMMENT '入住日期',
  `check_out_date` DATE NOT NULL COMMENT '退房日期',
  `nights` INT NOT NULL COMMENT '入住天数',
  `guests_count` INT DEFAULT 1 COMMENT '入住人数',
  `room_price` DECIMAL(10,2) NOT NULL COMMENT '房间单价',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `paid_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '已付金额',
  `booking_status` TINYINT DEFAULT 1 COMMENT '预订状态(1:待确认 2:已确认 3:已入住 4:已退房 5:已取消)',
  `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态(0:未支付 1:已支付 2:部分支付 3:已退款)',
  `booking_source` VARCHAR(20) DEFAULT 'DIRECT' COMMENT '预订来源(DIRECT:直订 OTA:第三方)',
  `special_requests` TEXT COMMENT '特殊要求',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `check_in_time` DATETIME DEFAULT NULL COMMENT '实际入住时间',
  `check_out_time` DATETIME DEFAULT NULL COMMENT '实际退房时间',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_booking_status` (`booking_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_check_in_date` (`check_in_date`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer_info` (`id`),
  CONSTRAINT `fk_room` FOREIGN KEY (`room_id`) REFERENCES `room_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订订单表';
```

#### 3.2.7 支付记录表 (payment_record)
```sql
CREATE TABLE `payment_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `payment_no` VARCHAR(32) NOT NULL COMMENT '支付流水号',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式(ALIPAY:支付宝 WECHAT:微信 CASH:现金 CARD:刷卡)',
  `payment_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态(0:待支付 1:支付成功 2:支付失败 3:已退款)',
  `third_party_no` VARCHAR(64) DEFAULT NULL COMMENT '第三方支付流水号',
  `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `refund_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额',
  `refund_time` DATETIME DEFAULT NULL COMMENT '退款时间',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `remarks` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_payment_time` (`payment_time`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';
```

#### 3.2.8 评论表 (customer_review)
```sql
CREATE TABLE `customer_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `room_id` BIGINT NOT NULL COMMENT '房间ID',
  `rating` TINYINT NOT NULL COMMENT '评分(1-5分)',
  `content` TEXT COMMENT '评论内容',
  `images` JSON COMMENT '评论图片',
  `reply_content` TEXT COMMENT '管理员回复',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `reply_user_id` BIGINT DEFAULT NULL COMMENT '回复人ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:隐藏 1:显示)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`),
  CONSTRAINT `fk_review_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer_info` (`id`),
  CONSTRAINT `fk_review_room` FOREIGN KEY (`room_id`) REFERENCES `room_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户评论表';
```

#### 3.2.9 财务记录表 (financial_record)
```sql
CREATE TABLE `financial_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '财务记录ID',
  `record_no` VARCHAR(32) NOT NULL COMMENT '记录编号',
  `type` TINYINT NOT NULL COMMENT '类型(1:收入 2:支出)',
  `category` VARCHAR(50) NOT NULL COMMENT '分类(ROOM:房费 DEPOSIT:押金 REFUND:退款 EXPENSE:支出)',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `related_order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `description` TEXT COMMENT '描述',
  `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `voucher_url` VARCHAR(255) DEFAULT NULL COMMENT '凭证图片URL',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:无效 1:有效)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_type` (`type`),
  KEY `idx_category` (`category`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务记录表';
```

#### 3.2.10 系统配置表 (system_config)
```sql
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `config_type` VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型(STRING:字符串 NUMBER:数字 BOOLEAN:布尔 JSON:JSON)',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
  `group_name` VARCHAR(50) DEFAULT 'DEFAULT' COMMENT '配置分组',
  `is_system` TINYINT DEFAULT 0 COMMENT '是否系统配置(0:否 1:是)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';
```

#### 3.2.11 操作日志表 (operation_log)
```sql
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
  `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
  `params` TEXT COMMENT '请求参数',
  `result` TEXT COMMENT '返回结果',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '操作地点',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `execute_time` BIGINT DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` TINYINT DEFAULT 1 COMMENT '操作状态(0:失败 1:成功)',
  `error_message` TEXT COMMENT '错误信息',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

#### 3.2.12 登录日志表 (login_log)
```sql
CREATE TABLE `login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '登录日志ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `login_type` TINYINT DEFAULT 1 COMMENT '登录类型(1:账号密码 2:短信验证码 3:邮箱验证)',
  `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
  `login_location` VARCHAR(100) DEFAULT NULL COMMENT '登录地点',
  `browser` VARCHAR(100) DEFAULT NULL COMMENT '浏览器',
  `os` VARCHAR(100) DEFAULT NULL COMMENT '操作系统',
  `device` VARCHAR(100) DEFAULT NULL COMMENT '设备类型',
  `status` TINYINT DEFAULT 1 COMMENT '登录状态(0:失败 1:成功)',
  `message` VARCHAR(200) DEFAULT NULL COMMENT '提示消息',
  `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_login_ip` (`login_ip`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';
```

### 3.3 数据字典

#### 3.3.1 用户状态枚举
| 值 | 说明 |
|---|---|
| 0 | 禁用 |
| 1 | 启用 |

#### 3.3.2 房间状态枚举
| 值 | 说明 |
|---|---|
| 1 | 可用 |
| 2 | 占用 |
| 3 | 维修 |
| 4 | 清洁 |
| 5 | 停用 |

#### 3.3.3 订单状态枚举
| 值 | 说明 |
|---|---|
| 1 | 待确认 |
| 2 | 已确认 |
| 3 | 已入住 |
| 4 | 已退房 |
| 5 | 已取消 |

#### 3.3.4 支付状态枚举
| 值 | 说明 |
|---|---|
| 0 | 未支付 |
| 1 | 已支付 |
| 2 | 部分支付 |
| 3 | 已退款 |


### 3.4 索引设计说明

#### 3.4.1 主键索引
- 所有表都使用自增BIGINT作为主键
- 保证数据唯一性和查询性能

#### 3.4.2 唯一索引
- 用户名、邮箱、手机号等唯一性字段
- 订单号、支付流水号等业务唯一标识

#### 3.4.3 普通索引
- 外键字段建立索引提高关联查询性能
- 状态字段建立索引支持状态筛选
- 时间字段建立索引支持时间范围查询

#### 3.4.4 复合索引
- (customer_id, booking_status) - 客户订单状态查询
- (room_id, check_in_date, check_out_date) - 房间可用性查询
- (record_date, type, category) - 财务报表查询

4. 技术选型
## 5. 前端开发规范

### 5.1 技术栈选型
- **框架**: Vue 3.x (Composition API)
- **构建工具**: Vite 4.x
- **UI组件库**: Element Plus
- **路由管理**: Vue Router 4.x
- **状态管理**: Pinia (替代Vuex)
- **HTTP客户端**: Axios
- **CSS预处理器**: Sass/SCSS
- **代码规范**: ESLint + Prettier
- **类型检查**: TypeScript (可选)

### 5.2 项目结构规范

```
src/
├── api/                    # API接口定义
│   ├── modules/           # 按模块分类的接口
│   │   ├── auth.js       # 认证相关接口
│   │   ├── room.js       # 房间管理接口
│   │   ├── booking.js    # 订单管理接口
│   │   └── customer.js   # 客户管理接口
│   ├── request.js        # Axios配置和拦截器
│   └── index.js          # 接口统一导出
├── assets/                # 静态资源
│   ├── images/           # 图片资源
│   ├── icons/            # 图标资源
│   └── styles/           # 全局样式
│       ├── variables.scss # SCSS变量
│       ├── mixins.scss   # SCSS混入
│       └── global.scss   # 全局样式
├── components/            # 公共组件
│   ├── common/           # 通用组件
│   │   ├── AppHeader.vue
│   │   ├── AppSidebar.vue
│   │   └── AppFooter.vue
│   ├── business/         # 业务组件
│   │   ├── RoomCard.vue
│   │   ├── BookingForm.vue
│   │   └── CustomerInfo.vue
│   └── ui/               # UI组件
│       ├── Loading.vue
│       ├── Empty.vue
│       └── Pagination.vue
├── composables/           # 组合式函数
│   ├── useAuth.js        # 认证相关
│   ├── useTable.js       # 表格相关
│   └── useForm.js        # 表单相关
├── directives/            # 自定义指令
│   ├── permission.js     # 权限指令
│   └── loading.js        # 加载指令
├── layouts/               # 布局组件
│   ├── DefaultLayout.vue # 默认布局
│   ├── AuthLayout.vue    # 认证布局
│   └── EmptyLayout.vue   # 空白布局
├── pages/                 # 页面组件
│   ├── auth/             # 认证相关页面
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   └── ForgotPassword.vue
│   ├── dashboard/        # 仪表盘
│   │   └── Index.vue
│   ├── rooms/            # 房间管理
│   │   ├── List.vue
│   │   ├── Detail.vue
│   │   └── Form.vue
│   ├── bookings/         # 订单管理
│   │   ├── List.vue
│   │   ├── Detail.vue
│   │   └── CheckIn.vue
│   └── customers/        # 客户管理
│       ├── List.vue
│       └── Detail.vue
├── plugins/               # 插件配置
│   ├── element-plus.js   # Element Plus配置
│   └── dayjs.js          # 日期库配置
├── router/                # 路由配置
│   ├── modules/          # 路由模块
│   │   ├── auth.js
│   │   ├── dashboard.js
│   │   └── business.js
│   ├── guards.js         # 路由守卫
│   └── index.js          # 路由主文件
├── stores/                # 状态管理
│   ├── modules/          # 状态模块
│   │   ├── auth.js       # 用户认证状态
│   │   ├── app.js        # 应用全局状态
│   │   └── business.js   # 业务状态
│   └── index.js          # Store主文件
├── utils/                 # 工具函数
│   ├── auth.js           # 认证工具
│   ├── request.js        # 请求工具
│   ├── storage.js        # 存储工具
│   ├── validate.js       # 验证工具
│   ├── format.js         # 格式化工具
│   └── constants.js      # 常量定义
├── App.vue               # 根组件
└── main.js               # 入口文件
```

### 5.3 组件设计规范

#### 5.3.1 组件命名规范
```javascript
// 组件文件命名：PascalCase
UserProfile.vue
RoomCard.vue
BookingForm.vue

// 组件注册名：PascalCase
app.component('UserProfile', UserProfile)

// 模板中使用：kebab-case
<user-profile />
<room-card />
<booking-form />
```

#### 5.3.2 组件结构规范
```vue
<template>
  <!-- 模板内容 -->
  <div class="component-name">
    <!-- 内容 -->
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue'

export default defineComponent({
  name: 'ComponentName',
  
  props: {
    // Props定义
    title: {
      type: String,
      required: true,
      default: ''
    }
  },
  
  emits: ['update', 'change'],
  
  setup(props, { emit }) {
    // 响应式数据
    const loading = ref(false)
    
    // 计算属性
    const displayTitle = computed(() => {
      return props.title.toUpperCase()
    })
    
    // 方法
    const handleClick = () => {
      emit('update', 'value')
    }
    
    // 生命周期
    onMounted(() => {
      // 初始化逻辑
    })
    
    return {
      loading,
      displayTitle,
      handleClick
    }
  }
})
</script>

<style lang="scss" scoped>
.component-name {
  // 样式定义
}
</style>
```

#### 5.3.3 Props定义规范
```javascript
// 完整的Props定义
props: {
  // 基础类型
  title: String,
  count: Number,
  isVisible: Boolean,
  
  // 复杂类型定义
  user: {
    type: Object,
    required: true,
    default: () => ({})
  },
  
  // 数组类型
  items: {
    type: Array,
    default: () => []
  },
  
  // 多类型
  value: {
    type: [String, Number],
    default: ''
  },
  
  // 自定义验证
  status: {
    type: String,
    validator: (value) => {
      return ['pending', 'success', 'error'].includes(value)
    }
  }
}
```

### 5.4 路由设计规范

#### 5.4.1 路由结构
```javascript
// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { setupRouterGuards } from './guards'

// 静态路由
const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/auth/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  }
]

// 动态路由
const asyncRoutes = [
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: {
      title: '仪表盘',
      requiresAuth: true,
      roles: ['admin', 'manager']
    },
    children: [
      {
        path: '',
        name: 'DashboardIndex',
        component: () => import('@/pages/dashboard/Index.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes]
})

// 设置路由守卫
setupRouterGuards(router)

export default router
```

#### 5.4.2 路由守卫
```javascript
// router/guards.js
import { useAuthStore } from '@/stores/modules/auth'

export function setupRouterGuards(router) {
  // 全局前置守卫
  router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore()
    
    // 设置页面标题
    document.title = to.meta.title || '悦鑫乐怡民宿管理系统'
    
    // 检查认证状态
    if (to.meta.requiresAuth) {
      if (!authStore.isAuthenticated) {
        next('/login')
        return
      }
      
      // 检查权限
      if (to.meta.roles && !authStore.hasRole(to.meta.roles)) {
        next('/403')
        return
      }
    }
    
    next()
  })
  
  // 全局后置守卫
  router.afterEach(() => {
    // 隐藏加载状态
  })
}
```

### 5.5 状态管理规范

#### 5.5.1 Pinia Store结构
```javascript
// stores/modules/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getUserInfo } from '@/api/modules/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const hasRole = computed(() => (roles) => {
    if (!userInfo.value) return false
    return roles.some(role => userInfo.value.roles.includes(role))
  })
  
  // Actions
  const loginAction = async (credentials) => {
    try {
      const response = await login(credentials)
      token.value = response.data.token
      userInfo.value = response.data.userInfo
      localStorage.setItem('token', token.value)
      return response
    } catch (error) {
      throw error
    }
  }
  
  const logoutAction = async () => {
    try {
      await logout()
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }
  
  const getUserInfoAction = async () => {
    try {
      const response = await getUserInfo()
      userInfo.value = response.data
      return response
    } catch (error) {
      throw error
    }
  }
  
  return {
    // State
    token,
    userInfo,
    // Getters
    isAuthenticated,
    hasRole,
    // Actions
    loginAction,
    logoutAction,
    getUserInfoAction
  }
})
```

### 5.6 API接口规范

#### 5.6.1 Axios配置
```javascript
// api/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    
    // 添加认证token
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    
    // 添加请求ID
    config.headers['X-Request-ID'] = generateRequestId()
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    
    if (code === 200) {
      return { data, message }
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    const { response } = error
    
    if (response) {
      switch (response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          useAuthStore().logoutAction()
          router.push('/login')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接异常')
    }
    
    return Promise.reject(error)
  }
)

export default request
```

#### 5.6.2 API模块定义
```javascript
// api/modules/room.js
import request from '../request'

// 获取房间列表
export const getRoomList = (params) => {
  return request({
    url: '/rooms',
    method: 'GET',
    params
  })
}

// 获取房间详情
export const getRoomDetail = (id) => {
  return request({
    url: `/rooms/${id}`,
    method: 'GET'
  })
}

// 创建房间
export const createRoom = (data) => {
  return request({
    url: '/rooms',
    method: 'POST',
    data
  })
}

// 更新房间
export const updateRoom = (id, data) => {
  return request({
    url: `/rooms/${id}`,
    method: 'PUT',
    data
  })
}

// 删除房间
export const deleteRoom = (id) => {
  return request({
    url: `/rooms/${id}`,
    method: 'DELETE'
  })
}
```

### 5.7 UI/UX设计规范

#### 5.7.1 设计系统
```scss
// assets/styles/variables.scss

// 主色调
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;

// 中性色
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #C0C4CC;

// 边框色
$border-base: #DCDFE6;
$border-light: #E4E7ED;
$border-lighter: #EBEEF5;
$border-extra-light: #F2F6FC;

// 背景色
$bg-color: #FFFFFF;
$bg-color-page: #F2F3F5;
$bg-color-light: #FCFCFC;

// 间距
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
$spacing-xl: 32px;

// 圆角
$border-radius-sm: 2px;
$border-radius-base: 4px;
$border-radius-lg: 6px;
$border-radius-round: 20px;

// 阴影
$box-shadow-base: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
$box-shadow-light: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
```

#### 5.7.2 响应式设计
```scss
// assets/styles/mixins.scss

// 断点定义
$breakpoints: (
  xs: 480px,
  sm: 768px,
  md: 992px,
  lg: 1200px,
  xl: 1920px
);

// 响应式混入
@mixin respond-to($breakpoint) {
  @if map-has-key($breakpoints, $breakpoint) {
    @media (min-width: map-get($breakpoints, $breakpoint)) {
      @content;
    }
  }
}

// 使用示例
.container {
  padding: $spacing-md;
  
  @include respond-to(md) {
    padding: $spacing-lg;
  }
  
  @include respond-to(lg) {
    padding: $spacing-xl;
  }
}
```

### 5.8 代码规范

#### 5.8.1 ESLint配置
```javascript
// .eslintrc.js
module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    '@vue/eslint-config-prettier'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  rules: {
    // Vue相关规则
    'vue/multi-word-component-names': 'off',
    'vue/no-unused-vars': 'error',
    'vue/no-unused-components': 'warn',
    
    // JavaScript规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-unused-vars': 'warn',
    'prefer-const': 'error'
  }
}
```

#### 5.8.2 Prettier配置
```javascript
// .prettierrc.js
module.exports = {
  semi: false,
  singleQuote: true,
  tabWidth: 2,
  trailingComma: 'none',
  printWidth: 100,
  bracketSpacing: true,
  arrowParens: 'avoid'
}
```

## 6. 后端开发规范

### 6.1 技术栈选型
- **框架**: Spring Boot 2.7.x
- **安全**: Spring Security + JWT
- **数据访问**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **连接池**: Druid
- **文档**: Swagger 3.x (SpringDoc)
- **构建工具**: Maven 3.8+
- **JDK版本**: OpenJDK 11+

### 6.2 项目结构规范

```
src/main/java/com/yxly/
├── YxlyApplication.java           # 启动类
├── config/                        # 配置类
│   ├── SecurityConfig.java       # 安全配置
│   ├── SwaggerConfig.java        # 接口文档配置
│   ├── RedisConfig.java          # Redis配置
│   ├── DruidConfig.java          # 数据源配置
│   ├── WebMvcConfig.java         # Web配置
│   └── AsyncConfig.java          # 异步配置
├── controller/                    # 控制器层
│   ├── auth/                     # 认证相关控制器
│   │   ├── AuthController.java
│   │   └── UserController.java
│   ├── business/                 # 业务控制器
│   │   ├── RoomController.java
│   │   ├── BookingController.java
│   │   ├── CustomerController.java
│   │   └── FinancialController.java
│   └── system/                   # 系统管理控制器
│       ├── SystemController.java
│       └── LogController.java
├── service/                       # 业务服务层
│   ├── auth/                     # 认证服务
│   │   ├── AuthService.java
│   │   └── UserService.java
│   ├── business/                 # 业务服务
│   │   ├── RoomService.java
│   │   ├── BookingService.java
│   │   ├── CustomerService.java
│   │   └── FinancialService.java
│   └── system/                   # 系统服务
│       ├── SystemConfigService.java
│       └── LogService.java
├── service/impl/                  # 服务实现类
│   ├── auth/
│   ├── business/
│   └── system/
├── mapper/                        # 数据访问层
│   ├── SysUserMapper.java
│   ├── RoomInfoMapper.java
│   ├── BookingOrderMapper.java
│   ├── CustomerInfoMapper.java
│   └── FinancialRecordMapper.java
├── entity/                        # 实体类
│   ├── SysUser.java
│   ├── RoomInfo.java
│   ├── BookingOrder.java
│   ├── CustomerInfo.java
│   └── FinancialRecord.java
├── dto/                           # 数据传输对象
│   ├── request/                  # 请求DTO
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── RoomCreateRequest.java
│   │   └── BookingCreateRequest.java
│   └── response/                 # 响应DTO
│       ├── LoginResponse.java
│       ├── UserInfoResponse.java
│       ├── RoomDetailResponse.java
│       └── BookingDetailResponse.java
├── vo/                            # 视图对象
│   ├── RoomVO.java
│   ├── BookingVO.java
│   ├── CustomerVO.java
│   └── StatisticsVO.java
├── enums/                         # 枚举类
│   ├── UserStatus.java
│   ├── RoomStatus.java
│   ├── BookingStatus.java
│   └── PaymentStatus.java
├── exception/                     # 异常处理
│   ├── GlobalExceptionHandler.java
│   ├── BusinessException.java
│   ├── AuthenticationException.java
│   └── ValidationException.java
├── security/                      # 安全相关
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── UserDetailsServiceImpl.java
│   └── SecurityUtils.java
├── utils/                         # 工具类
│   ├── ResponseUtils.java        # 响应工具
│   ├── ValidationUtils.java      # 验证工具
│   ├── DateUtils.java           # 日期工具
│   ├── StringUtils.java         # 字符串工具
│   ├── EncryptUtils.java        # 加密工具
│   └── FileUtils.java           # 文件工具
├── common/                        # 公共类
│   ├── BaseEntity.java          # 基础实体
│   ├── BaseController.java      # 基础控制器
│   ├── BaseService.java         # 基础服务
│   ├── Result.java              # 统一响应结果
│   ├── PageResult.java          # 分页结果
│   └── Constants.java           # 常量定义
├── aspect/                        # 切面
│   ├── LogAspect.java           # 日志切面
│   ├── ValidationAspect.java    # 验证切面
│   └── PermissionAspect.java    # 权限切面
└── task/                          # 定时任务
    ├── DataCleanTask.java       # 数据清理任务
    └── StatisticsTask.java      # 统计任务

src/main/resources/
├── application.yml                # 主配置文件
├── application-dev.yml           # 开发环境配置
├── application-prod.yml          # 生产环境配置
├── mapper/                       # MyBatis映射文件
│   ├── SysUserMapper.xml
│   ├── RoomInfoMapper.xml
│   ├── BookingOrderMapper.xml
│   └── CustomerInfoMapper.xml
├── static/                       # 静态资源
└── templates/                    # 模板文件
```

### 6.3 代码结构规范

#### 6.3.1 控制器规范
```java
@RestController
@RequestMapping("/api/v1/rooms")
@Api(tags = "房间管理")
@Slf4j
@Validated
public class RoomController extends BaseController {
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping
    @ApiOperation("获取房间列表")
    @PreAuthorize("hasAuthority('room:list')")
    public Result<PageResult<RoomVO>> getRoomList(
            @Valid RoomQueryRequest request) {
        
        log.info("获取房间列表，参数：{}", request);
        
        try {
            PageResult<RoomVO> result = roomService.getRoomList(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取房间列表失败", e);
            return Result.error("获取房间列表失败");
        }
    }
    
    @PostMapping
    @ApiOperation("创建房间")
    @PreAuthorize("hasAuthority('room:create')")
    @LogOperation("创建房间")
    public Result<Long> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        
        log.info("创建房间，参数：{}", request);
        
        Long roomId = roomService.createRoom(request);
        return Result.success(roomId, "房间创建成功");
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新房间")
    @PreAuthorize("hasAuthority('room:update')")
    @LogOperation("更新房间")
    public Result<Void> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateRequest request) {
        
        log.info("更新房间，ID：{}，参数：{}", id, request);
        
        roomService.updateRoom(id, request);
        return Result.success("房间更新成功");
    }
}
```

#### 6.3.2 服务层规范
```java
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RoomServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo> 
        implements RoomService {
    
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Override
    @Transactional(readOnly = true)
    public PageResult<RoomVO> getRoomList(RoomQueryRequest request) {
        
        // 参数验证
        ValidationUtils.validatePageParams(request.getPage(), request.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<RoomInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(request.getRoomNumber()), 
                RoomInfo::getRoomNumber, request.getRoomNumber())
                .eq(Objects.nonNull(request.getRoomTypeId()), 
                RoomInfo::getRoomTypeId, request.getRoomTypeId())
                .eq(Objects.nonNull(request.getStatus()), 
                RoomInfo::getStatus, request.getStatus())
                .eq(RoomInfo::getDeleted, 0)
                .orderByDesc(RoomInfo::getCreateTime);
        
        // 分页查询
        Page<RoomInfo> page = new Page<>(request.getPage(), request.getSize());
        Page<RoomInfo> roomPage = this.page(page, queryWrapper);
        
        // 转换VO
        List<RoomVO> roomVOList = roomPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageResult.of(roomPage.getTotal(), roomVOList);
    }
    
    @Override
    public Long createRoom(RoomCreateRequest request) {
        
        // 业务验证
        validateRoomNumber(request.getRoomNumber());
        validateRoomType(request.getRoomTypeId());
        
        // 构建实体
        RoomInfo roomInfo = new RoomInfo();
        BeanUtils.copyProperties(request, roomInfo);
        roomInfo.setStatus(RoomStatus.AVAILABLE.getCode());
        roomInfo.setCreateTime(LocalDateTime.now());
        
        // 保存数据
        this.save(roomInfo);
        
        log.info("房间创建成功，ID：{}", roomInfo.getId());
        return roomInfo.getId();
    }
    
    private void validateRoomNumber(String roomNumber) {
        LambdaQueryWrapper<RoomInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomInfo::getRoomNumber, roomNumber)
                .eq(RoomInfo::getDeleted, 0);
        
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("房间号已存在");
        }
    }
    
    private RoomVO convertToVO(RoomInfo roomInfo) {
        RoomVO roomVO = new RoomVO();
        BeanUtils.copyProperties(roomInfo, roomVO);
        
        // 设置房型信息
        RoomType roomType = roomTypeService.getById(roomInfo.getRoomTypeId());
        if (roomType != null) {
            roomVO.setRoomTypeName(roomType.getTypeName());
        }
        
        // 设置状态文本
        roomVO.setStatusText(RoomStatus.getByCode(roomInfo.getStatus()).getDesc());
        
        return roomVO;
    }
}
```

#### 6.3.3 实体类规范
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("room_info")
@ApiModel("房间信息")
public class RoomInfo extends BaseEntity {
    
    @ApiModelProperty("房间ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @ApiModelProperty("房间号")
    @TableField("room_number")
    @NotBlank(message = "房间号不能为空")
    @Length(max = 20, message = "房间号长度不能超过20个字符")
    private String roomNumber;
    
    @ApiModelProperty("房型ID")
    @TableField("room_type_id")
    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;
    
    @ApiModelProperty("楼层")
    @TableField("floor_number")
    @Min(value = 1, message = "楼层必须大于0")
    private Integer floorNumber;
    
    @ApiModelProperty("面积")
    @TableField("area")
    @DecimalMin(value = "0.01", message = "面积必须大于0")
    private BigDecimal area;
    
    @ApiModelProperty("床型")
    @TableField("bed_type")
    @Length(max = 50, message = "床型长度不能超过50个字符")
    private String bedType;
    
    @ApiModelProperty("最大入住人数")
    @TableField("max_guests")
    @Min(value = 1, message = "最大入住人数必须大于0")
    private Integer maxGuests;
    
    @ApiModelProperty("基础价格")
    @TableField("base_price")
    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.01", message = "基础价格必须大于0")
    private BigDecimal basePrice;
    
    @ApiModelProperty("当前价格")
    @TableField("current_price")
    @NotNull(message = "当前价格不能为空")
    @DecimalMin(value = "0.01", message = "当前价格必须大于0")
    private BigDecimal currentPrice;
    
    @ApiModelProperty("房间状态")
    @TableField("status")
    @NotNull(message = "房间状态不能为空")
    private Integer status;
    
    @ApiModelProperty("房间设施")
    @TableField("facilities")
    private String facilities;
    
    @ApiModelProperty("房间描述")
    @TableField("description")
    @Length(max = 500, message = "房间描述长度不能超过500个字符")
    private String description;
    
    @ApiModelProperty("房间图片")
    @TableField("images")
    private String images;
    
    @ApiModelProperty("WiFi密码")
    @TableField("wifi_password")
    @Length(max = 50, message = "WiFi密码长度不能超过50个字符")
    private String wifiPassword;
    
    @ApiModelProperty("最后清洁时间")
    @TableField("last_clean_time")
    private LocalDateTime lastCleanTime;
}
```

### 6.4 异常处理机制

#### 6.4.1 全局异常处理器
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数验证失败：{}", message);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 认证异常处理
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常：{}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), "认证失败");
    }
    
    /**
     * 权限异常处理
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限异常：{}", e.getMessage());
        return Result.error(ResultCode.FORBIDDEN.getCode(), "权限不足");
    }
    
    /**
     * 系统异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.INTERNAL_ERROR.getCode(), "系统内部错误");
    }
}
```

#### 6.4.2 自定义业务异常
```java
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    
    private Integer code;
    private String message;
    
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}
```

### 6.5 日志记录规范

#### 6.5.1 日志配置
```yaml
# application.yml
logging:
  level:
    root: INFO
    com.yxly: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{50}] - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%logger{50}] - %msg%n"
  file:
    name: logs/yxly.log
    max-size: 100MB
    max-history: 30
```

#### 6.5.2 日志切面
```java
@Aspect
@Component
@Slf4j
public class LogAspect {
    
    @Pointcut("@annotation(com.yxly.annotation.LogOperation)")
    public void logPointcut() {}
    
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        
        long startTime = System.currentTimeMillis();
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        
        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();
        String ip = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // 获取用户信息
        String username = SecurityUtils.getCurrentUsername();
        
        // 执行方法
        Object result;
        String errorMessage = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            
            // 记录操作日志
            OperationLog operationLog = new OperationLog();
            operationLog.setUsername(username);
            operationLog.setOperation(logOperation.value());
            operationLog.setMethod(method.getName());
            operationLog.setParams(JSON.toJSONString(joinPoint.getArgs()));
            operationLog.setIp(ip);
            operationLog.setUserAgent(userAgent);
            operationLog.setExecuteTime(executeTime);
            operationLog.setStatus(errorMessage == null ? 1 : 0);
            operationLog.setErrorMessage(errorMessage);
            operationLog.setCreateTime(LocalDateTime.now());
            
            // 异步保存日志
            logService.saveOperationLog(operationLog);
        }
        
        return result;
    }
}
```

### 6.6 配置文件详细说明

#### 6.6.1 主配置文件
```yaml
# application.yml
server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
  tomcat:
    max-threads: 200
    min-spare-threads: 10

spring:
  profiles:
    active: dev
  
  # 数据源配置
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:yxly_db}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
    
    # Druid配置
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      
      # 监控配置
      stat-view-servlet:
        enabled: true
        url-pattern: /druid/*
        login-username: admin
        login-password: admin123
      
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
  
  # Redis配置
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 10000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB
  
  # Jackson配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    default-property-inclusion: non_null

# MyBatis-Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
    banner: false
  mapper-locations: classpath*:/mapper/**/*.xml

# JWT配置
jwt:
  secret: ${JWT_SECRET:yxly-jwt-secret-key-2024}
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天

# 文件存储配置
file:
  upload-path: ${FILE_UPLOAD_PATH:/data/uploads/}
  access-url: ${FILE_ACCESS_URL:http://localhost:8080/api/files/}

# 业务配置
business:
  # 订单配置
  order:
    payment-timeout: 1800  # 支付超时时间（秒）
    auto-cancel-timeout: 3600  # 自动取消超时时间（秒）
  
  # 房间配置
  room:
    default-check-in-time: "14:00"
    default-check-out-time: "12:00"
```

#### 6.6.2 开发环境配置
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yxly_dev?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  
  redis:
    host: localhost
    port: 6379
    password: 

logging:
  level:
    com.yxly: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# Swagger配置
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

#### 6.6.3 生产环境配置
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    password: ${REDIS_PASSWORD}

logging:
  level:
    root: WARN
    com.yxly: INFO
  file:
    name: /var/log/yxly/application.log

# Swagger配置（生产环境关闭）
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```
## 4. 详细API文档设计

### 4.1 API设计规范

#### 4.1.1 基础规范
- **基础路径**: `/api/v1`
- **请求方式**: RESTful风格
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT Bearer Token

#### 4.1.2 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2024-01-01T12:00:00Z",
  "requestId": "uuid"
}
```

#### 4.1.3 HTTP状态码规范
| 状态码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 请求成功处理 |
| 201 | 创建成功 | 资源创建成功 |
| 400 | 请求错误 | 参数错误、业务逻辑错误 |
| 401 | 未认证 | 未登录或Token过期 |
| 403 | 无权限 | 权限不足 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 系统内部错误 |

#### 4.1.4 业务错误码定义
```json
{
  "USER_NOT_FOUND": 10001,
  "USER_ALREADY_EXISTS": 10002,
  "PASSWORD_ERROR": 10003,
  "TOKEN_EXPIRED": 10004,
  "ROOM_NOT_AVAILABLE": 20001,
  "ROOM_ALREADY_BOOKED": 20002,
  "ORDER_NOT_FOUND": 30001,
  "ORDER_STATUS_ERROR": 30002,
  "PAYMENT_FAILED": 40001,
  "INSUFFICIENT_BALANCE": 40002
}
```

### 4.2 认证相关接口

#### 4.2.1 用户注册
```
POST /api/v1/auth/register
```

**请求参数:**
```json
{
  "username": "string",      // 用户名，必填，3-20字符
  "password": "string",      // 密码，必填，6-20字符
  "email": "string",         // 邮箱，可选
  "phone": "string",         // 手机号，可选
  "realName": "string",      // 真实姓名，可选
  "verificationCode": "string" // 验证码，必填
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 12345,
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### 4.2.2 用户登录
```
POST /api/v1/auth/login
```

**请求参数:**
```json
{
  "username": "string",      // 用户名/邮箱/手机号
  "password": "string",      // 密码
  "loginType": "PASSWORD",   // 登录类型：PASSWORD/SMS/EMAIL
  "verificationCode": "string", // 验证码（短信/邮箱登录时必填）
  "rememberMe": false        // 是否记住登录
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_string",
    "expiresIn": 86400,
    "userInfo": {
      "id": 12345,
      "username": "testuser",
      "realName": "张三",
      "email": "test@example.com",
      "phone": "13800138000",
      "avatar": "http://example.com/avatar.jpg",
      "role": "USER"
    }
  }
}
```

### 4.3 房间管理接口

#### 4.3.1 获取房间列表
```
GET /api/v1/rooms
```

**请求参数:**
```
?page=1&size=10&roomType=1&status=1&checkInDate=2024-01-01&checkOutDate=2024-01-02
```

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "pages": 5,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "roomNumber": "101",
        "roomType": {
          "id": 1,
          "typeName": "标准单人间",
          "basePrice": 168.00
        },
        "currentPrice": 168.00,
        "status": 1,
        "statusText": "可用",
        "facilities": ["WiFi", "空调", "电视"],
        "images": ["http://example.com/room1.jpg"],
        "description": "舒适的标准单人间"
      }
    ]
  }
}
```

#### 4.3.2 创建房间
```
POST /api/v1/rooms
Authorization: Bearer {token}
```

**请求参数:**
```json
{
  "roomNumber": "201",
  "roomTypeId": 1,
  "floorNumber": 2,
  "area": 25.5,
  "bedType": "单人床",
  "maxGuests": 1,
  "basePrice": 168.00,
  "facilities": ["WiFi", "空调", "电视", "独立卫浴"],
  "description": "舒适的标准单人间，配备现代化设施",
  "images": ["http://example.com/room201_1.jpg", "http://example.com/room201_2.jpg"]
}
```

### 4.4 订单管理接口

#### 4.4.1 创建订单
```
POST /api/v1/bookings
Authorization: Bearer {token}
```

**请求参数:**
```json
{
  "customerId": 123,
  "roomId": 1,
  "checkInDate": "2024-01-15",
  "checkOutDate": "2024-01-17",
  "guestsCount": 2,
  "specialRequests": "需要加床",
  "contactName": "张三",
  "contactPhone": "13800138000"
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "orderId": 12345,
    "orderNo": "YX202401150001",
    "totalAmount": 336.00,
    "bookingStatus": 1,
    "paymentStatus": 0,
    "paymentDeadline": "2024-01-15T18:00:00Z"
  }
}
```

#### 4.4.2 订单支付
```
POST /api/v1/bookings/{orderId}/payment
Authorization: Bearer {token}
```

**请求参数:**
```json
{
  "paymentMethod": "ALIPAY",    // ALIPAY/WECHAT/CASH/CARD
  "paymentAmount": 336.00,
  "returnUrl": "http://example.com/payment/return",
  "notifyUrl": "http://example.com/payment/notify"
}
```

### 4.5 客户管理接口

#### 4.5.1 获取客户列表
```
GET /api/v1/customers
Authorization: Bearer {token}
```

**请求参数:**
```
?page=1&size=10&keyword=张三&status=1
```

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 123,
        "customerNo": "C202401001",
        "name": "张三",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "totalConsumption": 1580.00,
        "visitCount": 5,
        "lastVisitTime": "2024-01-10T14:30:00Z",
        "status": 1
      }
    ]
  }
}
```

### 4.6 财务管理接口

#### 4.6.1 获取财务统计
```
GET /api/v1/financial/statistics
Authorization: Bearer {token}
```

**请求参数:**
```
?startDate=2024-01-01&endDate=2024-01-31&type=1
```

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalIncome": 158000.00,
    "totalExpense": 25000.00,
    "netProfit": 133000.00,
    "roomRevenue": 145000.00,
    "otherRevenue": 13000.00,
    "occupancyRate": 85.5,
    "averageRoomRate": 168.50,
    "dailyStatistics": [
      {
        "date": "2024-01-01",
        "income": 5200.00,
        "expense": 800.00,
        "occupiedRooms": 31,
        "totalRooms": 50
      }
    ]
  }
}
```

### 4.7 系统管理接口

#### 4.7.1 获取操作日志
```
GET /api/v1/system/logs
Authorization: Bearer {token}
```

**请求参数:**
```
?page=1&size=20&userId=123&operation=LOGIN&startTime=2024-01-01&endTime=2024-01-31
```

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 500,
    "records": [
      {
        "id": 1001,
        "userId": 123,
        "username": "admin",
        "operation": "用户登录",
        "method": "POST /api/v1/auth/login",
        "ip": "192.168.1.100",
        "location": "广东省梅州市",
        "userAgent": "Mozilla/5.0...",
        "executeTime": 150,
        "status": 1,
        "createTime": "2024-01-15T10:30:00Z"
      }
    ]
  }
}
```

### 4.8 文件上传接口

#### 4.8.1 图片上传
```
POST /api/v1/upload/image
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**请求参数:**
```
file: 图片文件 (支持jpg, png, gif, 最大5MB)
type: 上传类型 (avatar/room/review/voucher)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://example.com/uploads/2024/01/15/abc123.jpg",
    "filename": "room_image.jpg",
    "size": 1024000,
    "type": "image/jpeg"
  }
}
```

### 4.9 接口认证说明

#### 4.9.1 JWT Token格式
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

#### 4.9.2 Token刷新机制
- Access Token有效期：24小时
- Refresh Token有效期：7天
- 在Access Token过期前30分钟可使用Refresh Token获取新Token

#### 4.9.3 权限控制
- **ADMIN**: 系统管理员，拥有所有权限
- **MANAGER**: 民宿管理员，可管理房间、订单、客户
- **RECEPTIONIST**: 前台接待，可处理入住退房、查看订单
- **USER**: 普通用户，只能查看自己的订单信息

7. 安全设计
使用Spring Security进行权限控制，对API进行保护。

用户密码加密存储，使用BCrypt加密。

使用JWT进行无状态认证，避免使用Session。

对敏感操作（如支付）进行验证码或密码二次验证。



8.开发计划
搭建前后端项目框架

数据库设计并建表

后端基础架构搭建（Spring Security、JWT、MyBatis Plus等）

实现用户管理模块

实现房间管理模块

实现订单管理模块

实现评论管理模块

前端页面开发

前后端联调



以上是一个初步的设计方案，具体实现过程中可能根据实际需求进行调整。