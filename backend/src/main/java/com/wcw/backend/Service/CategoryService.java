package com.wcw.backend.Service;

import com.wcw.backend.Common.BizException;
import com.wcw.backend.Entity.Category;
import com.wcw.backend.Mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> getCategoriesByTypeAndUser(String type, Long userId) {
        return categoryMapper.selectByOwner(userId, type);
    }

    public Category getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BizException(404, "分类不存在");
        }
        return category;
    }

    public int addCategory(Category category) {
        int result = categoryMapper.insert(category);
        if (result == 0) {
            throw new BizException(500, "添加分类失败");
        }
        return result;
    }

    public int updateCategory(Long id, Category category, Long userId) {
        // 先取出对应需要修改的分类在数据库中的样子
        Category dbcategory = categoryMapper.selectById(id);

        // 安全检验
        if (dbcategory.getIsSystem() == 1){
            throw new BizException(403, "系统内置分类不可修改");
        }
        if (!dbcategory.getCreatedBy().equals(userId)){
            throw new BizException(403, "无权修改此类");
        }

        int result = categoryMapper.update(category);
        if (result == 0) {
            throw new BizException(500, "更新失败");
        }
        return result;
    }

    public void deleteCategory(Long id, Long userId) {
        Category dbCategory = getCategoryById(id);

        if (dbCategory.getIsSystem() == 1) {
            throw new BizException(403, "系统内置分类不可删除");
        }
        if (!dbCategory.getCreatedBy().equals(userId)) {
            throw new BizException(403, "无权删除该分类");
        }

        int result = categoryMapper.deleteById(id);
        if (result == 0) {
            throw new BizException(500, "删除失败");
        }
    }

}
