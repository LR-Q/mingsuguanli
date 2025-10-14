package com.yxly.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "总页数")
    private Long pages;
    
    @Schema(description = "当前页")
    private Long current;
    
    @Schema(description = "每页大小")
    private Long size;
    
    @Schema(description = "数据列表")
    private List<T> records;
    
    public PageResult() {}
    
    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
    
    public PageResult(Long total, Long pages, Long current, Long size, List<T> records) {
        this.total = total;
        this.pages = pages;
        this.current = current;
        this.size = size;
        this.records = records;
    }
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(total, records);
    }
    
    public static <T> PageResult<T> of(Long total, Long pages, Long current, Long size, List<T> records) {
        return new PageResult<>(total, pages, current, size, records);
    }
}
