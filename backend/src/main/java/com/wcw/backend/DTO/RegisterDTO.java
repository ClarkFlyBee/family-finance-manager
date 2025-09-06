package com.wcw.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank private String name;
    @NotBlank private String phone;
    @NotBlank private String password;
}
