# 百度地图API配置说明

## 获取百度地图AK密钥

### 步骤1：注册百度账号
访问 [百度地图开放平台](https://lbsyun.baidu.com/) 并登录

### 步骤2：创建应用
1. 登录后，点击右上角"控制台"
2. 进入控制台后，选择"应用管理" > "我的应用"
3. 点击"创建应用"按钮

### 步骤3：填写应用信息
- **应用名称**：填写你的应用名称，如"悦鑫乐怡民宿管理系统"
- **应用类型**：选择"浏览器端"
- **Referer白名单**：
  - 开发环境：`localhost`、`127.0.0.1`、`192.168.*`
  - 生产环境：填写你的域名，如 `yourdomain.com`
  - 可以先设置为 `*` (允许所有域名，不推荐生产环境)

### 步骤4：获取AK密钥
创建成功后，会生成一个AK密钥（访问应用AK），复制这个密钥

### 步骤5：配置到项目中
打开文件：`src/pages/admin/LocationManagement.vue`

找到第638行：
```javascript
script.src = 'https://api.map.baidu.com/api?v=1.0&type=webgl&ak=YOUR_BAIDU_MAP_AK'
```

将 `YOUR_BAIDU_MAP_AK` 替换为你的实际AK密钥：
```javascript
script.src = 'https://api.map.baidu.com/api?v=1.0&type=webgl&ak=你的AK密钥'
```

## 示例

```javascript
// 示例（这不是真实的AK，请替换为你自己的）
script.src = 'https://api.map.baidu.com/api?v=1.0&type=webgl&ak=abcdefghijklmnopqrstuvwxyz123456'
```

## 注意事项

1. **免费配额**：百度地图提供免费配额，日常使用足够
2. **安全性**：
   - 开发环境可以使用宽松的Referer配置
   - 生产环境务必配置准确的域名白名单
3. **服务启用**：确保在控制台中启用了以下服务：
   - 地图JavaScript API
   - 地点检索服务
   - 地理编码服务

## 替代方案

如果不想使用百度地图，也可以切换到高德地图：
1. 访问 [高德地图开放平台](https://lbs.amap.com/)
2. 创建应用获取Key
3. 修改代码使用高德地图API（需要调整代码）

## 测试配置

配置完成后，重启前端开发服务器：
```bash
npm run dev
```

然后访问位置管理页面，点击"添加位置"，如果地图正常显示则配置成功。

## 常见问题

### Q: 地图显示空白或报错？
A: 检查以下几点：
1. AK密钥是否正确配置
2. 浏览器控制台是否有错误信息
3. Referer白名单是否包含当前域名
4. 网络是否正常

### Q: 搜索功能不可用？
A: 确保在百度地图控制台中启用了"地点检索服务"

### Q: 无法获取详细地址？
A: 确保启用了"地理编码服务"

## 相关链接

- [百度地图开放平台](https://lbsyun.baidu.com/)
- [JavaScript API文档](https://lbsyun.baidu.com/index.php?title=jspopularGL)
- [AK申请教程](https://lbsyun.baidu.com/index.php?title=jspopularGL/guide/getkey)
