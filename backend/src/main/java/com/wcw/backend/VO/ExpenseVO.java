package com.wcw.backend.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseVO {
    private Long id;
    private String ExpNo;
    private String ownerType;
    private Long ownerId;
    private String ownerName;
    private String categoryName;
    private BigDecimal amount;
    private LocalDateTime expTime;
    private String remark;
    private LocalDateTime createdAt;
}
