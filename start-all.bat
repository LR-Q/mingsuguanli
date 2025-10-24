@echo off
chcp 65001 >nul
echo ====================================
echo 同时启动用户端和管理后台
echo ====================================
echo.
echo 用户端: http://localhost:3000
echo 管理后台: http://localhost:3001
echo.
echo 提示: 关闭此窗口将停止所有服务
echo ====================================
echo.

:: 启动用户端 (新窗口)
start "用户端 - 端口3000" cmd /k "cd yxly-frontend && npm run dev"

:: 等待2秒
timeout /t 2 /nobreak >nul

:: 启动管理后台 (新窗口)
start "管理后台 - 端口3001" cmd /k "cd yxly-admin-frontend && npm run dev"

echo.
echo 已启动两个服务窗口
echo 请在各自窗口中查看运行状态
echo.
pause
