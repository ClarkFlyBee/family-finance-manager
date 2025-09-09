package com.wcw.backend.DTO;

import com.wcw.backend.Service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TrendStatDTO {
    private String date;            // 格式：”2025-08-01“ 或 ”2025-08“
    private BigDecimal income;      // 收入总额
    private BigDecimal expense;     // 支出总额
}
