@echo off
chcp 65001 >nul
echo ===========================================
echo 悦鑫乐怡民宿管理系统 - 热加载功能测试
echo ===========================================

echo 🧪 开始热加载功能测试...
echo.

REM 检查后端DevTools配置
echo 🔍 检查后端热加载配置...
if exist "yxly-backend\pom.xml" (
    findstr /C:"spring-boot-devtools" yxly-backend\pom.xml >nul
    if !errorlevel! equ 0 (
        echo ✅ 后端DevTools依赖已配置
    ) else (
        echo ❌ 后端DevTools依赖缺失
    )
) else (
    echo ❌ 后端项目不存在
)

REM 检查前端HMR配置
echo 🔍 检查前端热加载配置...
if exist "yxly-frontend\vite.config.js" (
    findstr /C:"hmr" yxly-frontend\vite.config.js >nul
    if !errorlevel! equ 0 (
        echo ✅ 前端HMR配置已启用
    ) else (
        echo ❌ 前端HMR配置缺失
    )
) else (
    echo ❌ 前端项目不存在
)

echo.
echo 📋 热加载配置检查完成！
echo.

REM 创建测试文件
echo 🧪 创建热加载测试文件...

REM 后端测试文件
if exist "yxly-backend\src\main\java\com\yxly" (
    echo 创建后端测试控制器...
    (
    echo package com.yxly.controller.test;
    echo.
    echo import org.springframework.web.bind.annotation.GetMapping;
    echo import org.springframework.web.bind.annotation.RequestMapping;
    echo import org.springframework.web.bind.annotation.RestController;
    echo import java.time.LocalDateTime;
    echo.
    echo /**
    echo  * 热加载测试控制器
    echo  */
    echo @RestController
    echo @RequestMapping^("/api/v1/test"^)
    echo public class HotReloadTestController {
    echo.
    echo     @GetMapping^("/time"^)
    echo     public String getCurrentTime^(^) {
    echo         return "当前时间: " + LocalDateTime.now^(^);
    echo     }
    echo.
    echo     @GetMapping^("/hello"^)
    echo     public String hello^(^) {
    echo         return "热加载测试成功！修改这里的文字来测试热加载功能。";
    echo     }
    echo }
    ) > "yxly-backend\src\main\java\com\yxly\controller\test\HotReloadTestController.java"
    
    mkdir "yxly-backend\src\main\java\com\yxly\controller\test" 2>nul
    echo ✅ 后端测试文件已创建
) else (
    echo ❌ 后端源码目录不存在
)

REM 前端测试文件
if exist "yxly-frontend\src\pages" (
    echo 创建前端测试页面...
    (
    echo ^<template^>
    echo   ^<div class="hot-reload-test"^>
    echo     ^<h2^>🔥 热加载功能测试^</h2^>
    echo     ^<div class="test-content"^>
    echo       ^<p^>当前时间: {{ currentTime }}^</p^>
    echo       ^<p^>修改这段文字来测试前端热加载功能^</p^>
    echo       ^<el-button type="primary" @click="updateTime"^>更新时间^</el-button^>
    echo       ^<el-button type="success" @click="testApi"^>测试后端API^</el-button^>
    echo     ^</div^>
    echo     ^<div class="api-result" v-if="apiResult"^>
    echo       ^<h3^>API响应:^</h3^>
    echo       ^<p^>{{ apiResult }}^</p^>
    echo     ^</div^>
    echo   ^</div^>
    echo ^</template^>
    echo.
    echo ^<script setup^>
    echo import { ref, onMounted } from 'vue'
    echo import axios from 'axios'
    echo.
    echo const currentTime = ref^(''^)
    echo const apiResult = ref^(''^)
    echo.
    echo const updateTime = ^(^) =^> {
    echo   currentTime.value = new Date^(^).toLocaleString^(^)
    echo }
    echo.
    echo const testApi = async ^(^) =^> {
    echo   try {
    echo     const response = await axios.get^('/api/v1/test/hello'^)
    echo     apiResult.value = response.data
    echo   } catch ^(error^) {
    echo     apiResult.value = '后端API未启动或配置错误'
    echo   }
    echo }
    echo.
    echo onMounted^(^() =^> {
    echo   updateTime^(^)
    echo }^)
    echo ^</script^>
    echo.
    echo ^<style lang="scss" scoped^>
    echo .hot-reload-test {
    echo   padding: 24px;
    echo   max-width: 800px;
    echo   margin: 0 auto;
    echo   
    echo   h2 {
    echo     color: #409eff;
    echo     margin-bottom: 24px;
    echo   }
    echo   
    echo   .test-content {
    echo     background: white;
    echo     padding: 24px;
    echo     border-radius: 8px;
    echo     box-shadow: 0 2px 12px rgba^(0, 0, 0, 0.1^);
    echo     margin-bottom: 16px;
    echo     
    echo     p {
    echo       margin-bottom: 16px;
    echo       font-size: 16px;
    echo     }
    echo     
    echo     .el-button + .el-button {
    echo       margin-left: 16px;
    echo     }
    echo   }
    echo   
    echo   .api-result {
    echo     background: #f0f9ff;
    echo     padding: 16px;
    echo     border-radius: 8px;
    echo     border-left: 4px solid #409eff;
    echo   }
    echo }
    echo ^</style^>
    ) > "yxly-frontend\src\pages\HotReloadTest.vue"
    echo ✅ 前端测试文件已创建
) else (
    echo ❌ 前端页面目录不存在
)

echo.
echo ===========================================
echo 🎉 热加载测试环境准备完成！
echo.
echo 📝 测试文件已创建:
echo   📁 后端: yxly-backend\src\main\java\com\yxly\controller\test\HotReloadTestController.java
echo   📁 前端: yxly-frontend\src\pages\HotReloadTest.vue
echo.
echo 🧪 测试步骤:
echo   1. 启动热加载开发环境 (start-dev.bat)
echo   2. 访问前端测试页面: http://localhost:3000/#/test
echo   3. 修改测试文件中的文字
echo   4. 观察浏览器是否自动更新
echo   5. 检查后端API是否自动重启
echo.
echo 💡 测试API端点:
echo   - GET /api/v1/test/hello  (测试文本)
echo   - GET /api/v1/test/time   (当前时间)
echo.
echo 🔄 热加载验证方法:
echo   ✅ 修改Java文件 → 控制台显示重启信息
echo   ✅ 修改Vue文件 → 浏览器立即更新
echo   ✅ 修改CSS样式 → 页面样式实时变化
echo ===========================================
pause
