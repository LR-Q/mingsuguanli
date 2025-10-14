@echo off
echo 启动悦鑫乐怡民宿管理系统 - 热加载模式
echo =====================================

echo 清理旧进程...
taskkill /F /IM java.exe 2>nul
taskkill /F /IM node.exe 2>nul

echo 等待进程清理完成...
timeout /t 3 /nobreak >nul

echo 启动后端服务 (Spring Boot DevTools)...
cd yxly-backend
start "后端服务" cmd /k "mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo 等待后端启动...
timeout /t 10 /nobreak >nul

echo 启动前端服务 (Vite HMR)...
cd ../yxly-frontend
start "前端服务" cmd /k "npm run dev"

echo 服务启动完成！
echo 前端地址: http://localhost:3000
echo 后端地址: http://localhost:8080
echo Swagger文档: http://localhost:8080/swagger-ui.html
echo.
echo 热加载功能已启用：
echo - 后端: 修改Java文件会自动重启
echo - 前端: 修改Vue文件会自动刷新
echo.
pause
