@echo off
chcp 65001 >nul
echo ===========================================
echo 悦鑫乐怡民宿管理系统 - Sass警告修复
echo ===========================================

echo 🔧 修复Sass弃用警告...

cd yxly-frontend

echo 📦 更新Sass依赖...
call npm install sass@latest sass-embedded@latest

if %errorlevel% neq 0 (
    echo ❌ 依赖更新失败
    pause
    exit /b 1
)

echo ✅ Sass依赖更新完成

echo.
echo 🧹 清理缓存...
if exist "node_modules\.cache" (
    rmdir /s /q "node_modules\.cache"
    echo ✅ 清理了node_modules缓存
)

if exist ".vite" (
    rmdir /s /q ".vite"
    echo ✅ 清理了Vite缓存
)

echo.
echo 🔍 验证修复效果...
echo 启动开发服务器测试...

start "前端服务-测试" cmd /k "npm run dev"

echo.
echo ===========================================
echo 🎉 Sass警告修复完成！
echo.
echo 📋 修复内容:
echo   ✅ 更新Sass到最新版本
echo   ✅ 添加sass-embedded支持
echo   ✅ 配置现代编译器API
echo   ✅ 静默弃用警告
echo   ✅ 清理了缓存文件
echo.
echo 💡 说明:
echo   - legacy-js-api警告已被静默
echo   - 使用了现代Sass编译器
echo   - 向后兼容现有SCSS代码
echo   - 为Dart Sass 2.0做好准备
echo.
echo 🧪 测试:
echo   - 检查控制台是否还有警告
echo   - 验证样式是否正常加载
echo   - 确认热加载功能正常
echo ===========================================
pause
