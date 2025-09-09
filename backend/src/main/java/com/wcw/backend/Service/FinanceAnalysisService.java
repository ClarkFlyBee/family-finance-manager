package com.wcw.backend.Service;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Mapper.ExpenseMapper;
import com.wcw.backend.Mapper.IncomeMapper;
import com.wcw.backend.Common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FinanceAnalysisService {

    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;
    private final ZhipuAiClient zhipuAiClient;

    // 支持的时间粒度格式
    private static final String DAILY_FORMAT = "%Y-%m-%d";
    private static final String MONTHLY_FORMAT = "%Y-%m";

    public Result<String> analyze(QueryDTO query, String question) {
        if (query.getOwnerId() == null || query.getOwnerType() == null ||
                query.getStartTime() == null || query.getEndTime() == null) {
            return Result.fail("查询条件不完整：请提供 ownerId、ownerType、startTime、endTime");
        }

        try {
            // 1. 查询收入 & 支出汇总数据
            Map<String, Object> financeData = collectFinancialData(query);

            // 2. 构造 AI 提示词（Prompt）
            String prompt = buildPrompt(financeData, query, question);

            // 3. 调用 GLM 模型
            ChatCompletionResponse response = callZhipuAi(prompt);

            if (response.isSuccess()) {
                ChatMessage chatMessage = (ChatMessage) response.getData().getChoices().get(0).getMessage();
                String content = chatMessage.getContent().toString();
                return Result.ok(content.trim());
            } else {
                return Result.fail("AI 分析失败: " + response.getMsg());
            }

        } catch (Exception e) {
            return Result.fail("分析过程中发生错误: " + e.getMessage());
        }
    }

    private Map<String, Object> collectFinancialData(QueryDTO query) {
        Map<String, Object> data = new HashMap<>();

        // 格式化日期范围
        LocalDate start = query.getStartTime();
        LocalDate end = query.getEndTime();

        // 判断是按日还是按月聚合（简单判断跨度）
        boolean isMonthly = end.toEpochDay() - start.toEpochDay() > 90; // 超过 90 天按月
        String format = isMonthly ? MONTHLY_FORMAT : DAILY_FORMAT;

        // 查询总收入
        List<Map<String, Object>> incomeList = incomeMapper.sumIncomeByPeriod(query, format);
        double totalIncome = incomeList.stream()
                .mapToDouble(m -> ((Number) m.get("amount")).doubleValue())
                .sum();

        // 查询总支出
        List<Map<String, Object>> expenseList = expenseMapper.sumExpenseByPeriod(query, format);
        double totalExpense = expenseList.stream()
                .mapToDouble(m -> ((Number) m.get("amount")).doubleValue())
                .sum();

        // 支出分类统计（可扩展：从 category 表关联）
        // 这里简化：只查大类（需前端传 categoryId 或自行设计分类表）
        // TODO: 后续可增强分类统计

        // 最大单笔支出
        List<Map<String, Object>> allExpenses = expenseMapper.sumExpenseByPeriod(
                query, DAILY_FORMAT); // 按天查所有记录
        Optional<Map<String, Object>> maxExpenseOpt = allExpenses.stream()
                .max(Comparator.comparing(m -> ((Number) m.get("amount")).doubleValue()));

        String maxRemark = "无";
        double maxAmount = 0.0;
        if (maxExpenseOpt.isPresent()) {
            // 这里无法直接拿到 remark，需扩展 mapper 查询完整 expense 记录
            // 当前先占位
            maxAmount = ((Number) maxExpenseOpt.get().get("amount")).doubleValue();
            maxRemark = "某笔支出"; // 可后续优化
        }

        data.put("totalIncome", round(totalIncome));
        data.put("totalExpense", round(totalExpense));
        data.put("surplus", round(totalIncome - totalExpense));
        data.put("maxAmount", round(maxAmount));
        data.put("maxRemark", maxRemark);
        data.put("isMonthly", isMonthly);

        return data;
    }

    private String buildPrompt(Map<String, Object> data, QueryDTO query, String userQuestion) {
        String startTime = query.getStartTime().toString();
        String endTime = query.getEndTime().toString();

        String context = String.format(
                "你是专业的财务分析师。请基于以下用户财务数据进行分析，并用中文、简洁清晰地回答问题。\n\n" +
                        "【分析时间范围】%s 至 %s\n" +
                        "【总收入】¥%.2f\n" +
                        "【总支出】¥%.2f\n" +
                        "【结余】¥%.2f\n" +
                        "【最大单笔支出】¥%.2f（%s）\n\n" +
                        "请结合以上数据，回答用户的问题：\n\"%s\"\n\n" +
                        "要求：\n" +
                        "- 回答专业但口语化，避免术语堆砌\n" +
                        "- 如果用户未提问具体问题，给出整体趋势和建议\n" +
                        "- 不要编造未提供的数据（如分类占比）\n" +
                        "- 控制在 150 字以内",
                startTime, endTime,
                data.get("totalIncome"),
                data.get("totalExpense"),
                data.get("surplus"),
                data.get("maxAmount"),
                data.get("maxRemark"),
                userQuestion.isEmpty() ? "请全面分析我的财务状况" : userQuestion
        );

        return context;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ChatCompletionResponse callZhipuAi(String prompt) {
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4") // 可改为 glm-4-flash 更快
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(prompt)
                                .build()
                ))
                .build();

        return zhipuAiClient.chat().createChatCompletion(request);
    }
}