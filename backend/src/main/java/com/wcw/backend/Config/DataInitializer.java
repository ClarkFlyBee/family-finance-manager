package com.wcw.backend.Config;

import com.wcw.backend.Entity.Category;
import com.wcw.backend.Mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryMapper categoryMapper;

    // 使用 Map 来组织数据：key = type, value = List of (parentName, categoryName)
    // 如果 parentName 为 null，表示是大类
    private static final Map<String, List<Object[]>> INIT_DATA = new LinkedHashMap<>();

    static {
        // 收入大类
        INIT_DATA.put("I", new ArrayList<>(Arrays.asList(
                new Object[]{null, "工资收入"},
                new Object[]{null, "投资理财"},
                new Object[]{null, "兼职副业"},
                new Object[]{null, "红包礼金"},
                new Object[]{null, "其他收入"},

                new Object[]{"工资收入", "基本工资"},
                new Object[]{"工资收入", "绩效奖金"},
                new Object[]{"工资收入", "年终奖"},

                new Object[]{"投资理财", "银行利息"},
                new Object[]{"投资理财", "股票收益"},
                new Object[]{"投资理财", "基金收益"},
                new Object[]{"投资理财", "理财产品"},

                new Object[]{"兼职副业", "自由职业"},
                new Object[]{"兼职副业", "网络兼职"},

                new Object[]{"红包礼金", "节日红包"},
                new Object[]{"红包礼金", "生日礼金"},

                new Object[]{"其他收入", "退款收入"},
                new Object[]{"其他收入", "意外之财"}
        )));

        // 支出大类
        INIT_DATA.put("E", new ArrayList<>(Arrays.asList(
                new Object[]{null, "日常饮食"},
                new Object[]{null, "交通出行"},
                new Object[]{null, "居住开销"},
                new Object[]{null, "娱乐休闲"},
                new Object[]{null, "购物消费"},
                new Object[]{null, "医疗健康"},
                new Object[]{null, "学习提升"},
                new Object[]{null, "人情往来"},
                new Object[]{null, "服饰美容"},
                new Object[]{null, "其他支出"},

                new Object[]{"日常饮食", "早餐"},
                new Object[]{"日常饮食", "午餐"},
                new Object[]{"日常饮食", "晚餐"},
                new Object[]{"日常饮食", "零食饮料"},
                new Object[]{"日常饮食", "水果生鲜"},

                new Object[]{"交通出行", "公交地铁"},
                new Object[]{"交通出行", "打车出行"},
                new Object[]{"交通出行", "私家车费"},
                new Object[]{"交通出行", "加油充电"},

                new Object[]{"居住开销", "房租"},
                new Object[]{"居住开销", "房贷"},
                new Object[]{"居住开销", "水电煤"},
                new Object[]{"居住开销", "物业费"},
                new Object[]{"居住开销", "宽带网络"},

                new Object[]{"娱乐休闲", "电影演出"},
                new Object[]{"娱乐休闲", "旅游出行"},
                new Object[]{"娱乐休闲", "游戏充值"},
                new Object[]{"娱乐休闲", "KTV酒吧"},

                new Object[]{"购物消费", "日用品"},
                new Object[]{"购物消费", "数码产品"},
                new Object[]{"购物消费", "家居用品"},

                new Object[]{"医疗健康", "门诊费用"},
                new Object[]{"医疗健康", "药品费用"},
                new Object[]{"医疗健康", "体检费用"},

                new Object[]{"学习提升", "书籍购买"},
                new Object[]{"学习提升", "在线课程"},
                new Object[]{"学习提升", "培训费用"},

                new Object[]{"人情往来", "请客吃饭"},
                new Object[]{"人情往来", "送礼支出"},
                new Object[]{"人情往来", "红包支出"},

                new Object[]{"服饰美容", "服装鞋帽"},
                new Object[]{"服饰美容", "化妆品"},
                new Object[]{"服饰美容", "理发美容"}
        )));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ 开始初始化分类数据...");

        LocalDateTime now = LocalDateTime.now();

        // 查询所有已存在的分类，按 name + type 建立索引
        List<Category> existingCategories = categoryMapper.selectAll();
        Map<String, Category> existingMap = existingCategories.stream()
                .collect(Collectors.toMap(
                        c -> c.getName() + "_" + c.getType(),
                        c -> c,
                        (e1, e2) -> e1  // 保留第一个
                ));

        // 用于保存新插入或已存在的分类 ID
        Map<String, Long> idMap = new HashMap<>();
        existingCategories.forEach(c -> idMap.put(c.getName() + "_" + c.getType(), c.getId()));

        int insertedCount = 0;

        for (Map.Entry<String, List<Object[]>> entry : INIT_DATA.entrySet()) {
            String type = entry.getKey();
            for (Object[] row : entry.getValue()) {
                String parentName = (String) row[0];
                String name = (String) row[1];

                String key = name + "_" + type;
                if (existingMap.containsKey(key)) {
                    // 已存在，跳过插入，但确保 idMap 有记录
                    idMap.putIfAbsent(key, existingMap.get(key).getId());
                    continue;
                }

                Category category = new Category();
                category.setType(type);
                category.setName(name);
                category.setIsSystem(1);
                category.setVisibility("P");
                category.setCreatedBy(null);
                category.setCreatedAt(now);
                category.setUpdatedAt(now);

                // 设置 parentId
                if (parentName != null) {
                    String parentKey = parentName + "_" + type;
                    Long parentId = idMap.get(parentKey);
                    if (parentId != null) {
                        category.setParentId(parentId);
                    } else {
                        System.err.println("❌ 父分类不存在: " + parentKey);
                        continue;
                    }
                } else {
                    category.setParentId(0L);
                }

                int result = categoryMapper.insert(category);
                if (result > 0) {
                    Long newId = category.getId(); // MyBatis 会自动回填主键
                    idMap.put(key, newId);
                    existingMap.put(key, category);
                    System.out.println("✅ 插入分类: " + name + " (" + type + ")，ID: " + newId);
                    insertedCount++;
                }
            }
        }

        System.out.println("✅ 分类数据初始化完成，共插入 " + insertedCount + " 条记录。");
    }
}