package com.wcw.backend.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryDTO {
    private String ownerType;
    private Long ownerId;
    private Long categoryId;
    private String keyword;     // 模糊搜索 remark
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
