package com.wcw.backend.Controller;

import com.wcw.backend.Common.Result;
import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/trend")
    public Result<?> getTrendStatistics(
            @ModelAttribute QueryDTO queryDTO,
            @RequestParam(defaultValue = "daily") String granularity) {

        if (queryDTO.getStartTime().isAfter(queryDTO.getEndTime())) {
            return Result.fail("开始日期不能晚于结束日期");
        }

        if (!"daily".equals(granularity) && !"monthly".equals(granularity)) {
            return Result.fail("granularity 必须是 'daily' 或 'monthly'");
        }

        try {
            Map<String, Object> data = statisticsService.getTrendStatistics(queryDTO, granularity);
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail("统计失败：" + e.getMessage());
        }
    }
}