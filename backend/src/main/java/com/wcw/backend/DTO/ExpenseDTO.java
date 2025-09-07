package com.wcw.backend.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseDTO {
    @NotBlank(message = "归属类型不能为空")
    private String ownerType;   // F/M

    @NotNull(message = "归属ID不能为空")
    private Long ownerId;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value="0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "支出时间不能为空")
    private LocalDateTime expTime;

    private String remark;
    private Integer isDraft = 0;
}
