@echo off
chcp 65001 >nul
echo ===========================================
echo 悦鑫乐怡民宿管理系统 - 热加载开发环境
echo ===========================================

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java未安装，请先安装Java 11+
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven未安装，请先安装Maven 3.8+
    pause
    exit /b 1
)

REM 检查Node.js环境
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js未安装，请先安装Node.js 16+
    pause
    exit /b 1
)

echo.
echo 🔧 配置开发环境...

REM 设置后端热加载环境变量
set SPRING_DEVTOOLS_RESTART_ENABLED=true
set SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true

echo.
echo 🚀 启动后端服务 (支持热加载)...
cd yxly-backend

REM 清理编译
echo 📦 清理并编译后端项目...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo ❌ 后端编译失败
    pause
    exit /b 1
)

echo ✅ 后端编译成功
echo 🌐 后端服务启动中... (端口: 8080)
echo 🔄 热加载已启用 - Java文件修改后自动重启
echo 📚 接口文档: http://localhost:8080/api/swagger-ui.html
echo 📊 数据库监控: http://localhost:8080/api/druid/
echo.

REM 使用spring-boot:run启动，支持热加载
start "后端服务-热加载" cmd /k "echo 🔄 后端热加载模式启动中... && mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true'"

REM 等待后端启动
echo ⏳ 等待后端服务启动...
timeout /t 15 /nobreak >nul

echo.
echo 🚀 启动前端服务 (支持热加载)...
cd ..\yxly-frontend

REM 检查是否已安装依赖
if not exist "node_modules" (
    echo 📦 安装前端依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo ❌ 前端依赖安装失败
        pause
        exit /b 1
    )
)

echo 🌐 前端服务启动中... (端口: 3000)
echo 🔄 热模块替换(HMR)已启用 - 文件修改后自动刷新
echo 🏠 前端访问地址: http://localhost:3000
echo.

REM 启动前端开发服务器，自动启用HMR
start "前端服务-热加载" cmd /k "echo 🔄 前端热加载模式启动中... && npm run dev"

REM 等待前端启动
timeout /t 5 /nobreak >nul

echo.
echo ===========================================
echo 🎉 热加载开发环境启动完成！
echo.
echo 📍 访问地址:
echo   🏠 前端应用: http://localhost:3000
echo   🔧 后端API: http://localhost:8080/api
echo   📚 接口文档: http://localhost:8080/api/swagger-ui.html
echo   📊 数据监控: http://localhost:8080/api/druid/
echo.
echo 🔄 热加载功能:
echo   ✅ 后端: Java文件修改后自动重启 (DevTools)
echo   ✅ 前端: Vue文件修改后自动刷新 (HMR)
echo   ✅ 样式: CSS/SCSS文件修改后实时更新
echo   ✅ 配置: 配置文件修改后自动重载
echo.
echo 💡 开发提示:
echo   - 修改Java文件后，后端会自动重启 (约3-5秒)
echo   - 修改Vue文件后，前端会立即热更新
echo   - 修改配置文件后，对应服务会自动重载
echo   - 浏览器会自动打开并连接到开发服务器
echo.
echo 📝 两个服务窗口已打开，可查看实时日志
echo ===========================================
pause
