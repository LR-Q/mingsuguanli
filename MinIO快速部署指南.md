# 🚀 MinIO 快速部署指南（5分钟完成）

## 第一步：启动 MinIO 服务

### Windows系统（推荐使用Docker）

#### 方式A：使用Docker Desktop
```bash
# 1. 启动MinIO容器
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  minio/minio server /data --console-address ":9001"

# 2. 检查运行状态
docker ps | findstr minio
```

#### 方式B：直接运行exe文件
```bash
# 1. 下载MinIO (放到任意目录，如C:\minio)
https://dl.min.io/server/minio/release/windows-amd64/minio.exe

# 2. 创建数据目录
mkdir C:\minio\data

# 3. 启动MinIO
cd C:\minio
minio.exe server C:\minio\data --console-address ":9001"
```

---

## 第二步：配置 MinIO

### 1. 访问控制台
打开浏览器，访问：http://localhost:9001

### 2. 登录
- 用户名：`minioadmin`
- 密码：`minioadmin`

### 3. 创建存储桶
1. 点击左侧菜单 "**Buckets**"
2. 点击右上角 "**Create Bucket**"
3. Bucket Name: `yxly-homestay`
4. 点击 "**Create Bucket**"

### 4. 设置公开访问（重要）
1. 进入刚创建的 `yxly-homestay` 存储桶
2. 点击 "**Summary**" → "**Access Policy**"
3. 选择 "**Public**"
4. 点击 "**Set**"

---

## 第三步：验证配置

### 测试上传文件
```bash
# 使用Postman或curl测试
curl -X POST http://localhost:8080/api/v1/upload/image \
  -F "file=@C:\Users\YourName\Pictures\test.jpg"
```

### 预期响应
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:9000/yxly-homestay/idcards/20241021/abc123.jpg"
  }
}
```

### 访问图片
直接在浏览器打开返回的URL，应该能看到图片

---

## 常见问题解决

### Q: Docker启动失败？
```bash
# 检查Docker是否运行
docker --version

# 检查端口是否被占用
netstat -ano | findstr "9000"
netstat -ano | findstr "9001"

# 如果端口被占用，修改端口
docker run -d --name minio -p 9002:9000 -p 9003:9001 ...
```

### Q: 访问不了控制台？
```bash
# 检查MinIO是否运行
docker logs minio

# 检查防火墙设置
# Windows Defender防火墙 → 允许应用通过防火墙
```

### Q: 上传成功但访问图片显示403？
**原因**：存储桶未设置为公开访问

**解决**：
1. 登录MinIO控制台
2. 进入 `yxly-homestay` 存储桶
3. 设置 Access Policy 为 **Public**

---

## 完整配置检查清单

- [ ] MinIO服务启动成功（端口9000、9001）
- [ ] 可以访问 http://localhost:9001
- [ ] 创建了 `yxly-homestay` 存储桶
- [ ] 存储桶设置为公开访问（Public）
- [ ] application-dev.yml配置正确
- [ ] 重启了后端服务
- [ ] 测试上传成功
- [ ] 可以通过URL访问图片

---

## 下一步

完成以上步骤后，你的民宿管理系统已经可以：
- ✅ 上传身份证照片
- ✅ 注册民宿主管理员
- ✅ 通过MinIO存储和访问图片

**开始使用吧！** 🎉

详细配置请参考：**MinIO部署使用说明.md**
