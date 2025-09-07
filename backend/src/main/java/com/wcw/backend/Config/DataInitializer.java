package com.wcw.backend.Config;

import com.wcw.backend.Entity.Category;
import com.wcw.backend.Mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryMapper categoryMapper;

    // 定义所有初始化数据：parent_id, type, name, is_system, visibility
    private static final List<Object[]> INIT_DATA = Arrays.asList(
            // 收入大类 (type = 'I')
            new Object[]{0L, "I", "工资收入", 1, "P"},
            new Object[]{0L, "I", "投资理财", 1, "P"},
            new Object[]{0L, "I", "兼职副业", 1, "P"},
            new Object[]{0L, "I", "红包礼金", 1, "P"},
            new Object[]{0L, "I", "其他收入", 1, "P"},

            // 收入子类 - 工资收入
            new Object[]{1L, "I", "基本工资", 1, "P"},
            new Object[]{1L, "I", "绩效奖金", 1, "P"},
            new Object[]{1L, "I", "年终奖", 1, "P"},

            // 投资理财
            new Object[]{2L, "I", "银行利息", 1, "P"},
            new Object[]{2L, "I", "股票收益", 1, "P"},
            new Object[]{2L, "I", "基金收益", 1, "P"},
            new Object[]{2L, "I", "理财产品", 1, "P"},

            // 兼职副业
            new Object[]{3L, "I", "自由职业", 1, "P"},
            new Object[]{3L, "I", "网络兼职", 1, "P"},

            // 红包礼金
            new Object[]{4L, "I", "节日红包", 1, "P"},
            new Object[]{4L, "I", "生日礼金", 1, "P"},

            // 其他收入
            new Object[]{5L, "I", "退款收入", 1, "P"},
            new Object[]{5L, "I", "意外之财", 1, "P"},

            // 支出大类 (type = 'E')
            new Object[]{0L, "E", "日常饮食", 1, "P"},
            new Object[]{0L, "E", "交通出行", 1, "P"},
            new Object[]{0L, "E", "居住开销", 1, "P"},
            new Object[]{0L, "E", "娱乐休闲", 1, "P"},
            new Object[]{0L, "E", "购物消费", 1, "P"},
            new Object[]{0L, "E", "医疗健康", 1, "P"},
            new Object[]{0L, "E", "学习提升", 1, "P"},
            new Object[]{0L, "E", "人情往来", 1, "P"},
            new Object[]{0L, "E", "服饰美容", 1, "P"},
            new Object[]{0L, "E", "其他支出", 1, "P"},

            // 支出子类 - 日常饮食
            new Object[]{11L, "E", "早餐", 1, "P"},
            new Object[]{12L, "E", "午餐", 1, "P"},
            new Object[]{13L, "E", "晚餐", 1, "P"},
            new Object[]{14L, "E", "零食饮料", 1, "P"},
            new Object[]{15L, "E", "水果生鲜", 1, "P"},

            // 交通出行
            new Object[]{16L, "E", "公交地铁", 1, "P"},
            new Object[]{17L, "E", "打车出行", 1, "P"},
            new Object[]{18L, "E", "私家车费", 1, "P"},
            new Object[]{19L, "E", "加油充电", 1, "P"},

            // 居住开销
            new Object[]{20L, "E", "房租", 1, "P"},
            new Object[]{21L, "E", "房贷", 1, "P"},
            new Object[]{22L, "E", "水电煤", 1, "P"},
            new Object[]{23L, "E", "物业费", 1, "P"},
            new Object[]{24L, "E", "宽带网络", 1, "P"},

            // 娱乐休闲
            new Object[]{25L, "E", "电影演出", 1, "P"},
            new Object[]{26L, "E", "旅游出行", 1, "P"},
            new Object[]{27L, "E", "游戏充值", 1, "P"},
            new Object[]{28L, "E", "KTV酒吧", 1, "P"},

            // 购物消费
            new Object[]{29L, "E", "日用品", 1, "P"},
            new Object[]{30L, "E", "数码产品", 1, "P"},
            new Object[]{31L, "E", "家居用品", 1, "P"},

            // 医疗健康
            new Object[]{32L, "E", "门诊费用", 1, "P"},
            new Object[]{33L, "E", "药品费用", 1, "P"},
            new Object[]{34L, "E", "体检费用", 1, "P"},

            // 学习提升
            new Object[]{35L, "E", "书籍购买", 1, "P"},
            new Object[]{36L, "E", "在线课程", 1, "P"},
            new Object[]{37L, "E", "培训费用", 1, "P"},

            // 人情往来
            new Object[]{38L, "E", "请客吃饭", 1, "P"},
            new Object[]{39L, "E", "送礼支出", 1, "P"},
            new Object[]{40L, "E", "红包支出", 1, "P"},

            // 服饰美容
            new Object[]{41L, "E", "服装鞋帽", 1, "P"},
            new Object[]{42L, "E", "化妆品", 1, "P"},
            new Object[]{43L, "E", "理发美容", 1, "P"}
    );

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ 开始初始化分类数据...");

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 查询所有已存在的分类（按 name + type 去重）
        List<Category> existingCategories = categoryMapper.selectByOwner(null, null);
        Map<String, Category> existingMap = existingCategories.stream()
                .collect(Collectors.toMap(
                        c -> c.getName() + "_" + c.getType(),
                        c -> c,
                        (existing, replacement) -> existing  // 保留已存在的
                ));

        int insertedCount = 0;

        for (Object[] data : INIT_DATA) {
            Long parentId = (Long) data[0];
            String type = (String) data[1];
            String name = (String) data[2];
            Integer isSystem = (Integer) data[3];
            String visibility = (String) data[4];

            // 唯一键：name + type
            String key = name + "_" + type;

            if (existingMap.containsKey(key)) {
                // System.out.println("跳过已存在的分类: " + name + " (" + type + ")");
                continue;
            }

            Category category = new Category();
            category.setParentId(parentId);
            category.setType(type);
            category.setName(name);
            category.setIsSystem(isSystem);
            category.setVisibility(visibility);
            category.setCreatedBy(null);
            category.setCreatedAt(now);
            category.setUpdatedAt(now);

            int result = categoryMapper.insert(category);
            if (result > 0) {
                System.out.println("✅ 插入分类: " + name + " (" + type + ")");
                insertedCount++;
            }
        }

        System.out.println("✅ 分类数据初始化完成，共插入 " + insertedCount + " 条记录。");
    }
}