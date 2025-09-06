package com.wcw.backend.Mapper;


import com.wcw.backend.Entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

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
}
