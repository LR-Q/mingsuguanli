@echo off
chcp 65001 >nul
echo ====================================
echo 启动管理后台 (端口 3001)
echo ====================================
echo.

cd yxly-admin-frontend

echo 检查依赖...
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
)

echo.
echo 启动开发服务器...
echo 管理后台地址: http://localhost:3001
echo.

npm run dev
