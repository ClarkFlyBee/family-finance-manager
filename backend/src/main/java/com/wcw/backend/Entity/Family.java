package com.wcw.backend.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Family {
    private Long id;
    private String familyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
