package com.wcw.backend.Controller;

import com.wcw.backend.Common.Result;
import com.wcw.backend.Entity.Category;
import com.wcw.backend.Service.CategoryService;
import com.wcw.backend.Util.CategoryTreeBuilder;
import com.wcw.backend.Util.JwtUtil;
import com.wcw.backend.VO.ExpenseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取单个分类
     * GET /api/category/1
     */
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Category category = categoryService.getCategoryById(id);
        return Result.ok(category);
    }

    /**
     * 获取分类列表（树形）
     * GET /api/category?type=E
     */
    @GetMapping()
    public Result<Map<String, Object>> getCategoryList(
            @RequestParam String type,
            HttpServletRequest request){
        Long userId = JwtUtil.getUserId(request);
        List<Category> flatList = categoryService.getCategoriesByTypeAndUser(type, userId);
        List<Category> tree = CategoryTreeBuilder.build(flatList);

        Map<String, Object> data = new HashMap<>();
        data.put("list", tree);

        return Result.ok(data);
    }

    /**
     * 添加分类
     * POST /api/category
     */
    @PostMapping("/add")
    public Result<Category> addCategory(@RequestBody Category category,
                                        HttpServletRequest request){
        Long userId = JwtUtil.getUserId(request);

        // 强制设置为用户自定义分类
        category.setIsSystem(0);
        category.setCreatedBy(userId);

        categoryService.addCategory(category);
        return Result.ok(category);
    }

    /**
     * 修改分类
     * PUT /api/category/1
     */
    @PutMapping("/{id}")
    public Result<Integer> updateCategory(@PathVariable Long id,
                                          @RequestBody Category category,
                                          HttpServletRequest request){
        Long userId = JwtUtil.getUserId(request);
        int result = categoryService.updateCategory(id, category, userId);
        return Result.ok(result);
    }

    /**
     * 删除分类
     * DELETE /api/category/1
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id,
                                          HttpServletRequest request){
        Long userId = JwtUtil.getUserId(request);
        categoryService.deleteCategory(id,userId);
        return Result.ok();
    }


}
