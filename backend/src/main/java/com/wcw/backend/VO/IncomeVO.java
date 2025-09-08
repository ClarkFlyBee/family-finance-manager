package com.wcw.backend.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IncomeVO {
    private Long id;
    private String incNo;
    private String ownerType;
    private Long ownerId;
    private String ownerName;
    private String categoryName;
    private Long categoryId;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate incTime;
    private String remark;
    private LocalDateTime createdAt;
}