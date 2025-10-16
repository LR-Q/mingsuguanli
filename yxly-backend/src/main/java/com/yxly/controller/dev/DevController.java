package com.yxly.controller.dev;

import com.yxly.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 开发工具控制器
 * 仅用于开发环境，生产环境应禁用
 */
@Slf4j
@RestController
@RequestMapping("/v1/dev")
@RequiredArgsConstructor
@Tag(name = "开发工具", description = "开发环境工具接口")
public class DevController {

    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "初始化房型数据", description = "执行SQL脚本初始化房型数据")
    @PostMapping("/init-room-types")
    public Result<String> initRoomTypes() {
        try {
            log.info("开始初始化房型数据...");
            
            // 读取SQL文件
            ClassPathResource resource = new ClassPathResource("sql/insert-room-types.sql");
            String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            
            // 分割SQL语句并执行
            String[] sqlStatements = sql.split(";");
            int executedCount = 0;
            
            for (String statement : sqlStatements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmedStatement);
                        executedCount++;
                        log.info("执行SQL: {}", trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "...");
                    } catch (Exception e) {
                        log.warn("执行SQL失败: {}, 错误: {}", trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())), e.getMessage());
                    }
                }
            }
            
            // 查询插入的数据数量
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM room_type WHERE deleted = 0", Integer.class);
            
            log.info("房型数据初始化完成，执行了{}条SQL语句，当前房型数量: {}", executedCount, count);
            return Result.success("房型数据初始化成功，当前房型数量: " + count);
            
        } catch (Exception e) {
            log.error("初始化房型数据失败", e);
            return Result.error("初始化房型数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "查询房型数据", description = "查询当前数据库中的房型数据")
    @PostMapping("/check-room-types")
    public Result<String> checkRoomTypes() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM room_type WHERE deleted = 0", Integer.class);
            log.info("当前房型数量: {}", count);
            return Result.success("当前房型数量: " + count);
        } catch (Exception e) {
            log.error("查询房型数据失败", e);
            return Result.error("查询房型数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "初始化充值表", description = "创建充值相关数据库表")
    @PostMapping("/init-recharge-tables")
    public Result<String> initRechargeTables() {
        try {
            log.info("开始初始化充值相关数据库表...");
            
            // 读取SQL文件
            ClassPathResource resource = new ClassPathResource("sql/create-recharge-table.sql");
            String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            
            // 分割SQL语句并执行
            String[] sqlStatements = sql.split(";");
            int executedCount = 0;
            
            for (String statement : sqlStatements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmedStatement);
                        executedCount++;
                        log.info("执行SQL: {}", trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "...");
                    } catch (Exception e) {
                        log.warn("执行SQL失败: {}, 错误: {}", trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())), e.getMessage());
                    }
                }
            }
            
            log.info("充值表初始化完成，执行了{}条SQL语句", executedCount);
            return Result.success("充值表初始化成功，执行了 " + executedCount + " 条SQL语句");
            
        } catch (Exception e) {
            log.error("初始化充值表失败", e);
            return Result.error("初始化充值表失败: " + e.getMessage());
        }
    }
}
