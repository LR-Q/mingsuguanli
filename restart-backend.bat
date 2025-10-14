@echo off
chcp 65001 >nul
echo ===========================================
echo 重启后端服务以确保配置生效
echo ===========================================

echo 🔄 停止现有服务...
taskkill /f /im java.exe 2>nul

echo.
echo ⏳ 等待端口释放...
timeout /t 3 /nobreak >nul

echo.
echo 🚀 启动后端服务...
cd yxly-backend
start "后端服务" mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo.
echo ✅ 后端服务启动中...
echo 请等待约30秒后测试注册功能
echo ===========================================
pause
