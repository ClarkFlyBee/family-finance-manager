package com.wcw.backend.Service;

import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.DTO.TrendStatDTO;
import com.wcw.backend.Mapper.ExpenseMapper;
import com.wcw.backend.Mapper.IncomeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;

    public Map<String, Object> getTrendStatistics(QueryDTO queryDTO, String granularity) {
        // 根据粒度确定时间格式化方式
        String dateFormat = "monthly".equals(granularity) ? "%Y-%m" : "%Y-%m-%d";

        // 查询收入数据
        List<Map<String, Object>> incomeData = incomeMapper.sumIncomeByPeriod(queryDTO, dateFormat);

        // 查询支出数据
        List<Map<String, Object>> expenseData = expenseMapper.sumExpenseByPeriod(queryDTO, dateFormat);

        // 4. 合并数据
        Map<String, TrendStatDTO> resultMap = new LinkedHashMap<>();

        // 处理收入
        for (Map<String, Object> row : incomeData) {
            String date = (String) row.get("day");
            BigDecimal amount = (BigDecimal) row.get("amount");
            resultMap.computeIfAbsent(date, k -> new TrendStatDTO(k, BigDecimal.ZERO, BigDecimal.ZERO))
                    .setIncome(amount);
        }

        // 处理支出
        for (Map<String, Object> row : expenseData) {
            String date = (String) row.get("day");
            BigDecimal amount = (BigDecimal) row.get("amount");
            resultMap.computeIfAbsent(date, k -> new TrendStatDTO(k, BigDecimal.ZERO, BigDecimal.ZERO))
                    .setExpense(amount);
        }

        // 5. 转为列表（保持时间顺序）
        List<TrendStatDTO> dataList = new ArrayList<>(resultMap.values());

        // 6. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("dateRange", queryDTO.getStartTime() + " ~ " + queryDTO.getEndTime());
        result.put("granularity", granularity);
        result.put("data", dataList);

        return result;
    }
}
