# 📦 MinIO 对象存储部署使用说明

## 🎯 MinIO 简介

MinIO 是一个高性能的对象存储服务，兼容 Amazon S3 API，适合存储图片、视频等非结构化数据。

---

## 🚀 快速部署

### 方式一：Docker 部署（推荐）

#### 1. 安装 Docker
```bash
# Windows: 下载 Docker Desktop
# 访问: https://www.docker.com/products/docker-desktop

# Linux:
curl -fsSL https://get.docker.com | bash -s docker
```

#### 2. 启动 MinIO 容器
```bash
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v E:/minio/data:/data \
  minio/minio server /data --console-address ":9001"
```

#### 3. 访问 MinIO 控制台
- 控制台地址：http://localhost:9001
- 用户名：`minioadmin`
- 密码：`minioadmin`

---

### 方式二：Windows 本地部署

#### 1. 下载 MinIO
```bash
# 下载地址
https://dl.min.io/server/minio/release/windows-amd64/minio.exe
```

#### 2. 创建数据目录
```bash
mkdir E:\minio\data
```

#### 3. 启动 MinIO
```bash
# 在 minio.exe 所在目录执行
minio.exe server E:\minio\data --console-address ":9001"
```

#### 4. 设置环境变量（可选）
```bash
set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin
```

---

## 🔧 MinIO 配置

### 1. 登录控制台
访问 http://localhost:9001，使用默认账号登录

### 2. 创建存储桶（Bucket）
- 点击 "Buckets" → "Create Bucket"
- Bucket Name: `yxly-homestay`
- Access Policy: **Public**（重要：设置为公开访问）
- 点击 "Create Bucket"

### 3. 设置访问策略
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": ["*"]
      },
      "Action": ["s3:GetObject"],
      "Resource": ["arn:aws:s3:::yxly-homestay/*"]
    }
  ]
}
```

---

## ⚙️ 应用配置

### application-dev.yml 配置
```yaml
minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucketName: yxly-homestay
  urlPrefix: http://localhost:9000/yxly-homestay/
```

### 配置说明
| 参数 | 说明 | 示例值 |
|------|------|--------|
| endpoint | MinIO 服务地址 | http://localhost:9000 |
| accessKey | 访问密钥 | minioadmin |
| secretKey | 秘密密钥 | minioadmin |
| bucketName | 存储桶名称 | yxly-homestay |
| urlPrefix | 图片访问URL前缀 | http://localhost:9000/yxly-homestay/ |

---

## 📁 文件组织结构

### 身份证图片存储路径
```
yxly-homestay/
  └── idcards/
      └── 20241021/
          ├── abc123def456.jpg（身份证正面）
          └── def456ghi789.jpg（身份证反面）
```

### 房间图片存储路径
```
yxly-homestay/
  └── rooms/
      └── 20241021/
          ├── 1_uuid1.jpg（房间ID_1的图片）
          └── 2_uuid2.jpg（房间ID_2的图片）
```

---

## 🧪 测试上传

### 1. 使用 Postman/Apifox 测试
```
POST http://localhost:8080/api/v1/upload/image
Content-Type: multipart/form-data

form-data:
  file: [选择图片文件]
```

### 2. 预期响应
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:9000/yxly-homestay/idcards/20241021/abc123def456.jpg",
    "filename": "abc123def456.jpg",
    "originalFilename": "idcard.jpg",
    "objectName": "idcards/20241021/abc123def456.jpg"
  }
}
```

### 3. 访问图片
直接在浏览器访问返回的 `url` 即可查看图片

---

## 🔐 生产环境配置

### 1. 修改默认密码
```bash
# 设置环境变量
export MINIO_ROOT_USER=your_admin_user
export MINIO_ROOT_PASSWORD=your_strong_password

# 重启 MinIO
```

### 2. 使用HTTPS
```bash
# 生成SSL证书
openssl req -new -newkey rsa:2048 -days 365 -nodes -x509 \
  -keyout private.key -out public.crt

# 启动时指定证书
minio server /data \
  --certs-dir ~/.minio/certs \
  --console-address ":9001"
```

### 3. application-prod.yml 配置
```yaml
minio:
  endpoint: https://minio.yourdomain.com
  accessKey: ${MINIO_ACCESS_KEY}
  secretKey: ${MINIO_SECRET_KEY}
  bucketName: yxly-homestay-prod
  urlPrefix: https://cdn.yourdomain.com/yxly-homestay-prod/
```

---

## 📊 监控和管理

### 1. 查看存储使用情况
- 登录控制台
- 点击 "Monitoring" → "Metrics"

### 2. 查看上传的文件
- 点击 "Buckets" → "yxly-homestay"
- 浏览文件列表

### 3. 删除文件
- 选择文件
- 点击删除按钮

---

## 🛠️ 常见问题

### Q1: 上传成功但无法访问图片？
**A**: 检查存储桶访问策略是否设置为 Public

**解决方法**:
```bash
# 在MinIO控制台执行
mc anonymous set public myminio/yxly-homestay
```

### Q2: 连接MinIO失败？
**A**: 检查以下几点：
- MinIO 服务是否启动
- 端口 9000 和 9001 是否被占用
- 防火墙是否开放端口

### Q3: Docker容器启动失败？
**A**: 检查数据目录权限
```bash
# 给予数据目录权限
chmod -R 755 /path/to/minio/data
```

### Q4: 如何备份MinIO数据？
**A**: 直接备份数据目录
```bash
# 停止MinIO
docker stop minio

# 备份数据
cp -r E:/minio/data E:/minio/backup

# 启动MinIO
docker start minio
```

---

## 🚀 高级功能

### 1. 配置图片压缩
可以集成图片处理库（如 Thumbnailator）在上传时自动压缩

### 2. CDN加速
将 MinIO 配合 CDN 使用，提升访问速度
```yaml
minio:
  urlPrefix: https://cdn.yourdomain.com/yxly-homestay/
```

### 3. 多区域部署
MinIO 支持分布式部署，提高可用性

---

## 📝 API 接口说明

### 上传图片
```
POST /api/v1/upload/image
```

**请求参数**:
- file: MultipartFile（图片文件）

**响应**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "完整访问URL",
    "filename": "生成的文件名",
    "originalFilename": "原始文件名",
    "objectName": "MinIO对象名称"
  }
}
```

### 上传身份证
```
POST /api/v1/upload/idcard
```
与上传图片接口相同

---

## ✅ 部署检查清单

- [ ] MinIO 服务启动成功
- [ ] 访问 http://localhost:9001 正常
- [ ] 创建 `yxly-homestay` 存储桶
- [ ] 设置存储桶为公开访问
- [ ] 配置 application-dev.yml
- [ ] 重启后端服务
- [ ] 测试图片上传功能
- [ ] 测试图片访问URL

---

## 🎉 完成

现在你的民宿管理系统已经集成了 MinIO 对象存储服务：
- ✅ 高性能的图片存储
- ✅ 可扩展的分布式架构
- ✅ S3兼容API
- ✅ 简单易用的管理控制台

**祝您使用愉快！** 🚀
