package com.wcw.backend.Entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Income {
    private Long id;
    private String incNo;
    private Long ownerId;       // 家庭 ID 或 用户 ID
    private String ownerType;   // F: 家庭，M: 个人
    private Long categoryId;
    private BigDecimal amount;
    private LocalDateTime incTime;
    private String remark;
    private Integer isDraft;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
