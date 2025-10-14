@echo off
chcp 65001 >nul
echo ===========================================
echo 悦鑫乐怡民宿管理系统 - 开发环境启动脚本
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
echo 🚀 开始启动后端服务 (支持热加载)...
cd yxly-backend
echo 📦 编译后端项目...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo ❌ 后端编译失败
    pause
    exit /b 1
)

echo ✅ 后端编译成功
echo 🌐 后端服务启动中... (端口: 8080)
echo 🔄 DevTools热加载已启用 - Java文件修改后自动重启
echo 📚 接口文档: http://localhost:8080/api/swagger-ui.html
echo 📊 数据库监控: http://localhost:8080/api/druid/
start "后端服务(热加载)" cmd /k "mvn spring-boot:run"

REM 等待后端启动
timeout /t 10 /nobreak >nul

echo.
echo 🚀 开始启动前端服务 (支持热加载)...
cd ..\yxly-frontend

REM 检查是否已安装依赖
if not exist "node_modules" (
    echo 📦 安装前端依赖...
    call npm install
)

echo 🌐 前端服务启动中... (端口: 3000)
echo 🔄 HMR热模块替换已启用 - 文件修改后自动刷新
echo 🏠 前端访问地址: http://localhost:3000
start "前端服务(热加载)" cmd /k "npm run dev"

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
echo   ✅ 后端: Java文件修改 → 自动重启 (3-5秒)
echo   ✅ 前端: Vue/JS/CSS文件修改 → 实时更新
echo   ✅ 配置: 配置文件修改 → 自动重载
echo.
echo 💡 高级功能:
echo   - 运行 start-dev-hot.bat 获得增强热加载
echo   - 运行 start-dev-smart.bat 获得智能文件监控
echo.
echo 📝 两个服务窗口已打开，请查看各自的启动状态
echo ===========================================
pause