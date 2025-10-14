@echo off
chcp 65001 >nul
echo ===========================================
echo 测试用户注册修复
echo ===========================================

echo 🧪 测试注册功能...

echo.
echo 1. 测试简单注册（符合密码规则）:
curl -X POST "http://localhost:8080/v1/auth/register" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser123\",\"password\":\"test123\",\"confirmPassword\":\"test123\",\"email\":\"test@example.com\",\"phone\":\"13800138000\",\"realName\":\"测试用户\"}"

echo.
echo.
echo 2. 检查用户名是否存在:
curl -X GET "http://localhost:8080/v1/auth/check-username?username=testuser123"

echo.
echo.
echo 3. 检查邮箱是否存在:
curl -X GET "http://localhost:8080/v1/auth/check-email?email=test@example.com"

echo.
echo ===========================================
pause
