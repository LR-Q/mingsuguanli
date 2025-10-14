package com.yxly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 悦鑫乐怡民宿管理系统启动类
 * 
 * @author yxly
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@MapperScan("com.yxly.mapper")
public class YxlyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(YxlyApplication.class, args);
        System.out.println("==========================================");
        System.out.println("悦鑫乐怡民宿管理系统启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("数据库监控: http://localhost:8080/api/druid/");
        System.out.println("==========================================");
    }
}
