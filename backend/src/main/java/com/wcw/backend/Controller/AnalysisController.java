package com.wcw.backend.Controller;

import com.wcw.backend.Common.Result;
import com.wcw.backend.DTO.AnalysisRequest;
import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Service.FinanceAnalysisService;
import com.wcw.backend.Util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final FinanceAnalysisService financeAnalysisService;

    /**
     * 智能财务分析对话接口
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody @Valid AnalysisRequest request,
                               HttpServletRequest httpServletRequest) {
        Long userId = JwtUtil.getUserId(httpServletRequest);

        QueryDTO query = request.getQuery();
        query.setOwnerId(userId);
        query.setOwnerType("M"); // 假设个人用户

        return financeAnalysisService.analyze(query, request.getQuestion());
    }

    /**
     * 一键生成智能分析（预设问题）
     */
    @GetMapping("/suggest")
    public Result<String> suggest(HttpServletRequest httpServletRequest,
                                  @RequestParam LocalDate startTime,
                                  @RequestParam LocalDate endTime) {
        Long userId = JwtUtil.getUserId(httpServletRequest);

        QueryDTO query = new QueryDTO();
        query.setOwnerId(userId);
        query.setOwnerType("M");
        query.setStartTime(startTime);
        query.setEndTime(endTime);

        return financeAnalysisService.analyze(query, "请全面分析我在此期间的财务状况，包括收支趋势和优化建议。");
    }
}
