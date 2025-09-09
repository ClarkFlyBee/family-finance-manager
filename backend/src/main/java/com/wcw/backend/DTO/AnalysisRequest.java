package com.wcw.backend.DTO;

import lombok.Data;

@Data
public class AnalysisRequest {
    private QueryDTO query;
    private String question;
}
