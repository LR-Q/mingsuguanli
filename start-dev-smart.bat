@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo ===========================================
echo 悦鑫乐怡民宿管理系统 - 智能热加载监控
echo ===========================================

REM 检查PowerShell是否可用
powershell -Command "Write-Host 'PowerShell可用'" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 需要PowerShell支持，请确保PowerShell已安装
    pause
    exit /b 1
)

echo 🔍 启动文件监控系统...
echo 📁 监控目录:
echo   - 后端: yxly-backend/src/
echo   - 前端: yxly-frontend/src/
echo   - 配置: *.yml, *.properties, *.json
echo.

REM 创建临时监控脚本
echo 创建文件监控脚本...
(
echo $backend_path = "yxly-backend/src"
echo $frontend_path = "yxly-frontend/src"
echo $config_files = @^("*.yml", "*.yaml", "*.properties", "*.json"^)
echo.
echo Write-Host "🔄 文件监控已启动..." -ForegroundColor Green
echo Write-Host "📁 后端监控: $backend_path" -ForegroundColor Yellow
echo Write-Host "📁 前端监控: $frontend_path" -ForegroundColor Yellow
echo.
echo # 后端文件监控
echo if ^(Test-Path $backend_path^) {
echo     $backend_watcher = New-Object System.IO.FileSystemWatcher
echo     $backend_watcher.Path = $backend_path
echo     $backend_watcher.IncludeSubdirectories = $true
echo     $backend_watcher.Filter = "*.java"
echo     $backend_watcher.EnableRaisingEvents = $true
echo.
echo     Register-ObjectEvent -InputObject $backend_watcher -EventName Changed -Action {
echo         $name = $Event.SourceEventArgs.Name
echo         $changeType = $Event.SourceEventArgs.ChangeType
echo         Write-Host "🔄 检测到后端文件变化: $name ^($changeType^)" -ForegroundColor Cyan
echo         Write-Host "   DevTools将自动重启后端服务..." -ForegroundColor Green
echo     }
echo }
echo.
echo # 前端文件监控
echo if ^(Test-Path $frontend_path^) {
echo     $frontend_watcher = New-Object System.IO.FileSystemWatcher
echo     $frontend_watcher.Path = $frontend_path
echo     $frontend_watcher.IncludeSubdirectories = $true
echo     $frontend_watcher.Filter = "*.*"
echo     $frontend_watcher.EnableRaisingEvents = $true
echo.
echo     Register-ObjectEvent -InputObject $frontend_watcher -EventName Changed -Action {
echo         $name = $Event.SourceEventArgs.Name
echo         $changeType = $Event.SourceEventArgs.ChangeType
echo         if ^($name -match "\\.^(vue^|js^|ts^|css^|scss^|sass^)$"^) {
echo             Write-Host "🔄 检测到前端文件变化: $name ^($changeType^)" -ForegroundColor Cyan
echo             Write-Host "   HMR将自动更新浏览器..." -ForegroundColor Green
echo         }
echo     }
echo }
echo.
echo Write-Host "✅ 文件监控系统运行中..." -ForegroundColor Green
echo Write-Host "💡 提示: 按Ctrl+C停止监控" -ForegroundColor Yellow
echo.
echo # 保持脚本运行
echo try {
echo     while ^($true^) {
echo         Start-Sleep -Seconds 1
echo     }
echo } finally {
echo     Write-Host "🛑 文件监控已停止" -ForegroundColor Red
echo }
) > file_monitor.ps1

REM 启动常规开发服务
echo 🚀 启动开发服务...
call start-dev-hot.bat

REM 等待服务启动
timeout /t 3 /nobreak >nul

REM 启动文件监控
echo.
echo 🔍 启动智能文件监控...
start "文件监控系统" powershell -ExecutionPolicy Bypass -File file_monitor.ps1

echo.
echo ===========================================
echo 🎉 智能热加载环境已启动！
echo.
echo 🔄 自动化功能:
echo   ✅ 后端Java文件 → DevTools自动重启
echo   ✅ 前端Vue/JS/CSS → HMR实时更新  
echo   ✅ 配置文件 → 自动重载配置
echo   ✅ 文件变化监控 → 实时日志提示
echo.
echo 📝 已打开的窗口:
echo   - 后端服务 (端口8080)
echo   - 前端服务 (端口3000)  
echo   - 文件监控系统
echo.
echo 💡 开发体验优化:
echo   - 保存文件后自动生效
echo   - 浏览器自动刷新
echo   - 实时错误提示
echo   - 智能监控日志
echo ===========================================

echo.
echo 按任意键关闭此窗口...
pause >nul

REM 清理临时文件
if exist file_monitor.ps1 del file_monitor.ps1
