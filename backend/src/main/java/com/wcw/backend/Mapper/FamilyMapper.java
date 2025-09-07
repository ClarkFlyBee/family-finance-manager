package com.wcw.backend.Mapper;

import com.wcw.backend.Entity.Family;
import com.wcw.backend.Entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyMapper {
    @Insert("INSERT INTO family(family_name) VALUES(#{familyName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")   // 把获取到的主键赋值给对象的id
    int insert(Family family);

    @Select("SELECT * FROM family")
    List<Family> selectAll();

    @Select("SELECT COUNT(*) > 0 FROM family WHERE id = #{id}")
    boolean existsById(Long id);
}
