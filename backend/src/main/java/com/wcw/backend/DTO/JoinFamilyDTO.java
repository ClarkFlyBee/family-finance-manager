package com.wcw.backend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinFamilyDTO {
    @NotNull(message = "家庭 ID 不能为空")
    private Long familyId;
}
