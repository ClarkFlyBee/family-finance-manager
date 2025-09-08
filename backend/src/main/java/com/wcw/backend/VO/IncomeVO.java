package com.wcw.backend.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class IncomeVO {
    private Long id;
    private String incNo;
    private String ownerType;
    private Long ownerId;
    private String ownerName;
    private String categoryName;
    private BigDecimal amount;
    private LocalDateTime incTime;
    private String remark;
    private LocalDateTime createdAt;
}