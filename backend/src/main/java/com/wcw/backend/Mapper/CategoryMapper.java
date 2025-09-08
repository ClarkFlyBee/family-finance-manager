package com.wcw.backend.Mapper;

import com.wcw.backend.Entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectAll();

    // 根据 ID 查询
    Category selectById(Long id);

    // 查询用户可用的分类（系统内置 + 用户自定义）
    List<Category> selectByOwner(@Param("userId") Long userId, @Param("type") String type);

    // 插入
    int insert(Category category);

    // 更新
    int update(Category category);

    // 删除（仅允许删除用户自定义）
    int deleteById(Long id);
}