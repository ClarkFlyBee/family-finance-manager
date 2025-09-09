package com.wcw.backend.Config;

import com.wcw.backend.DTO.RegisterDTO;
import com.wcw.backend.Entity.Category;
import com.wcw.backend.Entity.Expense;
import com.wcw.backend.Entity.Income;
import com.wcw.backend.Entity.User;
import com.wcw.backend.Mapper.CategoryMapper;
import com.wcw.backend.Mapper.ExpenseMapper;
import com.wcw.backend.Mapper.IncomeMapper;
import com.wcw.backend.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 初始化用户及过去三个月的收入/支出测试数据
 * 避免主 DataInitializer 过于臃肿
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class IncomeExpenseDataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;
    private final CategoryMapper categoryMapper;
    private final Random random = new Random();

    // 测试用户信息
    private static final String TEST_NAME = "root";
    private static final String TEST_PHONE = "13666432206";
    private static final String TEST_PASSWORD = "123456";

    // 分类 ID 模拟（可替换为真实存在的分类 ID）
    private static final Long[] INCOME_CATEGORIES = {1L, 2L, 3L, 4L}; // 如：工资、理财、奖金、其他
    private static final Long[] EXPENSE_CATEGORIES = {1L, 2L, 3L, 4L, 5L, 6L}; // 如：餐饮、交通、购物、娱乐、住房、其他

    // 金额范围（单位：元）
    private static final BigDecimal MIN_INCOME = new BigDecimal("1000.00");
    private static final BigDecimal MAX_INCOME = new BigDecimal("20000.00");
    private static final BigDecimal MIN_EXPENSE = new BigDecimal("10.00");
    private static final BigDecimal MAX_EXPENSE = new BigDecimal("500.00");

    // 备注模板
    private static final String[] INCOME_REMARKS = {"工资到账", "项目奖金", "理财收益", "兼职收入", "红包收入", "退款到账"};
    private static final String[] EXPENSE_REMARKS = {"日常开销", "外卖", "打车费", "超市购物", "聚餐", "电影票", "话费充值", "水电费", "网购"};


    @Override
    public void run(String... args) {
        log.info("开始初始化用户及收入/支出测试数据...");

        // 1. 注册测试用户（如果不存在）
        Long userId = registerTestUserIfNotExists();
        if (userId == null) {
            log.error("测试用户注册失败，终止初始化");
            return;
        }

        // 2. 获取用户家庭 ID
        Long familyId = userService.getAllUser().stream()
                .filter(u -> u.getPhone().equals(TEST_PHONE))
                .findFirst()
                .map(User::getFamilyId)
                .orElse(null);

        if (familyId == null) {
            log.error("无法获取用户 {} 的家庭 ID", TEST_PHONE);
            return;
        }

        // 4. ✅ 使用 selectByOwner 动态获取该用户可用的分类
        List<Category> incomeCategories = categoryMapper.selectByOwner(userId, "I"); // I 表示收入
        List<Category> expenseCategories = categoryMapper.selectByOwner(userId, "E"); // E 表示支出

        if (incomeCategories.isEmpty()) {
            log.warn("用户 {} 没有可用的收入分类，请检查 category 表数据", TEST_PHONE);
            return;
        }
        if (expenseCategories.isEmpty()) {
            log.warn("用户 {} 没有可用的支出分类，请检查 category 表数据", TEST_PHONE);
            return;
        }

        log.info("找到 {} 个收入分类和 {} 个支出分类，开始生成测试数据", incomeCategories.size(), expenseCategories.size());

        Random random = new Random();

        // 5. 生成 50 条收入数据
        for (int i = 0; i < 50; i++) {
            Income income = new Income();
            income.setIncNo("INC" + System.currentTimeMillis() + i);
            income.setOwnerType("M"); // 假设 1 是个人
            income.setOwnerId(userId);

            // ✅ 随机选择一个该用户拥有的收入分类
            Category randomIncomeCategory = incomeCategories.get(random.nextInt(incomeCategories.size()));
            income.setCategoryId(randomIncomeCategory.getId());

            // 金额：100 ~ 5000 随机
            income.setAmount(new BigDecimal("100.00").add(BigDecimal.valueOf(random.nextInt(4900))));

            // 时间：最近 90 天内随机
            income.setIncTime(LocalDate.now().minusDays(random.nextInt(90)));

            income.setRemark("测试收入记录 " + (i + 1));
            income.setIsDraft(0);

            // 插入数据库
            try {
                incomeMapper.insert(income);
            } catch (Exception e) {
                log.error("插入收入数据失败: {}", income, e);
            }
        }

        // 6. 生成 50 条支出数据
        for (int i = 0; i < 50; i++) {
            Expense expense = new Expense();
            expense.setExpNo("EXP" + System.currentTimeMillis() + i);
            expense.setOwnerType("M");
            expense.setOwnerId(userId);

            // ✅ 随机选择一个该用户拥有的支出分类
            Category randomExpenseCategory = expenseCategories.get(random.nextInt(expenseCategories.size()));
            expense.setCategoryId(randomExpenseCategory.getId());

            // 金额：50 ~ 2000 随机
            expense.setAmount(new BigDecimal("50.00").add(BigDecimal.valueOf(random.nextInt(1950))));

            // 时间：最近 90 天内随机
            expense.setExpTime(LocalDate.now().minusDays(random.nextInt(90)));

            expense.setRemark("测试支出记录 " + (i + 1));
            expense.setIsDraft(0);

            // 插入数据库
            try {
                expenseMapper.insert(expense);
            } catch (Exception e) {
                log.error("插入支出数据失败: {}", expense, e);
            }
        }

        log.info("✅ 测试数据初始化完成：50 条收入 + 50 条支出");
    }

    /**
     * 注册测试用户，若已存在则跳过
     */
    private Long registerTestUserIfNotExists() {
        var user = userService.getAllUser().stream()
                .filter(u -> TEST_PHONE.equals(u.getPhone()))
                .findFirst()
                .orElse(null);

        if (user != null) {
            log.info("用户 {} 已存在，跳过注册", TEST_PHONE);
            return user.getId();
        }

        RegisterDTO dto = new RegisterDTO();
        dto.setName(TEST_NAME);
        dto.setPhone(TEST_PHONE);
        dto.setPassword(TEST_PASSWORD);

        try {
            userService.register(dto);
            log.info("用户 {} 注册成功", TEST_NAME);
            return userService.getAllUser().stream()
                    .filter(u -> TEST_PHONE.equals(u.getPhone()))
                    .findFirst()
                    .map(com.wcw.backend.Entity.User::getId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成一笔收入记录
     */
    private Income generateIncome(Long ownerId, String ownerType, LocalDate date, Long familyId) {
        Income income = new Income();
        income.setOwnerId(ownerId);
        income.setOwnerType(ownerType);
        income.setCategoryId(INCOME_CATEGORIES[random.nextInt(INCOME_CATEGORIES.length)]);
        income.setAmount(randomAmount(MIN_INCOME, MAX_INCOME));
        income.setIncTime(date);
        income.setRemark(randomElement(INCOME_REMARKS) + " - " + date);
        income.setIsDraft(0); // 非草稿
        income.setCreatedAt(LocalDateTime.now().minusSeconds(random.nextInt(3600 * 24)));
        income.setUpdatedAt(income.getCreatedAt());
        return income;
    }

    /**
     * 生成一笔支出记录
     */
    private Expense generateExpense(Long ownerId, String ownerType, LocalDate date, Long familyId) {
        Expense expense = new Expense();
        expense.setOwnerId(ownerId);
        expense.setOwnerType(ownerType);
        expense.setCategoryId(EXPENSE_CATEGORIES[random.nextInt(EXPENSE_CATEGORIES.length)]);
        expense.setAmount(randomAmount(MIN_EXPENSE, MAX_EXPENSE));
        expense.setExpTime(date);
        expense.setRemark(randomElement(EXPENSE_REMARKS) + " - " + date);
        expense.setIsDraft(0); // 非草稿
        expense.setCreatedAt(LocalDateTime.now().minusSeconds(random.nextInt(3600 * 24)));
        expense.setUpdatedAt(expense.getCreatedAt());
        return expense;
    }

    /**
     * 随机生成指定范围内的金额
     */
    private BigDecimal randomAmount(BigDecimal min, BigDecimal max) {
        BigDecimal range = max.subtract(min);
        BigDecimal randomValue = min.add(range.multiply(BigDecimal.valueOf(random.nextDouble())));
        return randomValue.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 随机选择数组中的一个元素
     */
    private String randomElement(String[] array) {
        return array[random.nextInt(array.length)];
    }
}
