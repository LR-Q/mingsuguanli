@echo off
chcp 65001 >nul
echo ====================================
echo 启动用户端 (端口 3000)
echo ====================================
echo.

cd yxly-frontend

echo 检查依赖...
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
)

echo.
echo 启动开发服务器...
echo 用户端地址: http://localhost:3000
echo.

npm run dev
