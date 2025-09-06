package com.wcw.backend.Service;

import com.wcw.backend.Common.BizException;
import com.wcw.backend.DTO.LoginDTO;
import com.wcw.backend.DTO.RegisterDTO;
import com.wcw.backend.Entity.Family;
import com.wcw.backend.Entity.User;
import com.wcw.backend.Mapper.FamilyMapper;
import com.wcw.backend.Mapper.UserMapper;
import com.wcw.backend.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final FamilyMapper familyMapper;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    /* 注册 */
    @Transactional
    public int register(RegisterDTO dto){
        if (userMapper.selectByPhone(dto.getPhone()) != null){
            throw new BizException(400, "手机号已存在");
        }
        // 1. 建家庭
        Family family = new Family();
        family.setFamilyName(dto.getName() + "的家庭");
        familyMapper.insert(family);    // id 会自动给进 family

        // 2. 建用户
        User user = new User();
        user.setFamilyId(family.getId());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setPassword(encoder.encode(dto.getPassword()));
        return userMapper.insert(user);
    }

    /* 登录 */
    public String login(LoginDTO dto){
        User user = userMapper.selectByPhone(dto.getPhone());
        if (user == null || !encoder.matches(dto.getPassword(),user.getPassword())){
            throw new BizException(400, "用户名或密码错误");
        }
        return JwtUtil.createToken(user.getId(), user.getFamilyId());
    }

    public List<User> getAllUser(){
        return userMapper.selectAll();
    }

    public List<Family> getAllFamily(){
        return familyMapper.selectAll();
    }
}
