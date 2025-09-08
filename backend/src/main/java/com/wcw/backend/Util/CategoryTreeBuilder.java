package com.wcw.backend.Util;

import com.wcw.backend.Entity.Category;

import java.util.*;

public class CategoryTreeBuilder {
    /**
     * 扁平分类列表转换为树形结构
     */
    public static List<Category> build(List<Category> categories){
        if (categories == null || categories.isEmpty()){
            return Collections.emptyList();
        }

        Map<Long, Category> map = new HashMap<>();
        List<Category> root = new ArrayList<>();

        // 1. 所有节点入 map
        for (Category category : categories){
            map.put(category.getId(), category);
            category.setChildren(new ArrayList<>());
        }

        // 构建父子关系
        for (Category category : categories){
            Long parentId = category.getParentId();
            if (parentId == null || parentId == 0){
                root.add(category);
            } else {
                Category parent = map.get(parentId);
                if (parent != null){
                    parent.getChildren().add(category);
                }
            }
        }

        return root;
    }
}
