package com.wcw.backend.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private Long parentId;
    private String name;
    private String type;        // I: 收入, E: 支出
    private Long createdBy;
    private String visibility;  // F: 家庭可见. P: 个人可见
    private Integer isSystem;   // 1: 系统内置, 0: 用户自定义
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
