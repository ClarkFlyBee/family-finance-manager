package com.wcw.backend.Mapper;


import com.wcw.backend.Entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper{
    @Select("SELECT * FROM `user` " +
            "where phone = #{phone}")
    User selectByPhone(String phone);

    @Select("SELECT * FROM `user` " +
            "where id = #{id}")
    User selectById(Long id);

    @Select("SELECT * FROM `user`")
    List<User> selectAll();

    @Insert("INSERT INTO `user`(family_id, name, phone, password) VALUES(#{familyId}, #{name}, #{phone}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT COUNT(*) > 0 FROM `user` WHERE id = #{id}")
    boolean existsById(Long id);

    // 更新用户的 familyId
    @Update("UPDATE user SET family_id = #{familyId}, updated_at = NOW() WHERE id = #{id}")
    int updateFamilyId(User user);

    // 更新密码
    @Update("UPDATE user SET password = #{encodedPassword}, updated_at = NOW() WHERE id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("encodedPassword") String encodedPassword);

}
