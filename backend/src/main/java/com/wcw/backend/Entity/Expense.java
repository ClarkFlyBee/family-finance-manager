package com.wcw.backend.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Expense {
    private Long id;
    private String expNo;
    private Long ownerId;       // 家庭 ID 或 用户 ID
    private String ownerType;   // F: 家庭，M: 个人
    private Long categoryId;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expTime;
    private String remark;
    private Integer isDraft;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
