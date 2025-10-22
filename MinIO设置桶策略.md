# MinIO 桶策略设置指南

## 问题：上传文件403 Forbidden

### 原因
桶设置为"公共"只允许读取，不允许上传。需要设置完整的桶策略。

---

## 解决方案

### 方法一：MinIO控制台设置（推荐）

1. 访问 http://localhost:9001
2. 登录（admin / 12345678）
3. 点击左侧 **Buckets** → 点击 `txt` 桶
4. 找到 **Access Policy** 或 **Summary**
5. 点击 **Edit Policy** 或 **Access Rules**
6. 粘贴以下策略：

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {"AWS": ["*"]},
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": ["arn:aws:s3:::txt/*"]
    }
  ]
}
```

7. 保存

---

### 方法二：使用mc命令行

```bash
# 下载mc工具
# Windows: https://dl.min.io/client/mc/release/windows-amd64/mc.exe

# 配置别名
mc alias set myminio http://localhost:9000 admin 12345678

# 设置桶策略为完全公开
mc anonymous set public myminio/txt

# 验证策略
mc anonymous get myminio/txt
```

---

### 方法三：创建新桶（临时方案）

如果txt桶无法修改策略：

1. 在MinIO控制台创建新桶：`yxly-homestay`
2. 设置为 **public**（读写）
3. 修改 `application-dev.yml`：

```yaml
minio:
  bucketName: yxly-homestay
  urlPrefix: http://localhost:9000/yxly-homestay/
```

4. 重启后端服务

---

## 验证

上传成功后，应该能看到：

**后端日志**：
```
文件上传成功: idcards/20241021/abc123.jpg
```

**MinIO控制台**：
- txt桶中出现 idcards 文件夹
- 可以看到上传的图片文件

**访问URL**：
```
http://localhost:9000/txt/idcards/20241021/abc123.jpg
```

---

## 常见问题

### Q: 设置后还是403？
A: 
1. 检查MinIO服务是否重启
2. 清除浏览器缓存
3. 确认access key和secret key正确
4. 查看MinIO服务日志

### Q: 找不到Access Policy设置？
A: 
- 不同MinIO版本界面不同
- 尝试在 Summary / Access / Policies 标签中查找
- 使用mc命令行更可靠

### Q: mc命令执行失败？
A:
```bash
# 测试连接
mc admin info myminio

# 查看当前策略
mc anonymous list myminio/txt
```
