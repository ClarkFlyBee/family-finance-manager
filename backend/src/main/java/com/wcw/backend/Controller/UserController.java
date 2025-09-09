// src/main/java/com/wcw/backend/Controller/UserController.java
package com.wcw.backend.Controller;

import com.wcw.backend.Common.BizException;
import com.wcw.backend.Common.Result;
import com.wcw.backend.DTO.JoinFamilyDTO;
import com.wcw.backend.DTO.PasswordChangeDTO;
import com.wcw.backend.Entity.Family;
import com.wcw.backend.Entity.User;
import com.wcw.backend.Mapper.FamilyMapper;
import com.wcw.backend.Mapper.UserMapper;
import com.wcw.backend.Util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户个人信息管理控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final FamilyMapper familyMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取当前用户详细信息（包含家庭名称）
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserDetailedInfo(HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userMapper.selectById(userId);

        if (user == null) {
            return Result.fail(404, "用户不存在");
        }

        // 查询家庭信息
        Family family = familyMapper.selectById(user.getFamilyId());
        String familyName = Optional.ofNullable(family)
                .map(Family::getFamilyName)
                .orElse("未知家庭");

        // 构造返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("name", user.getName());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());
        data.put("familyId", user.getFamilyId());
        data.put("familyName", familyName);
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());

        return Result.ok(data);
    }

    /**
     * 加入家庭：通过输入 familyId（邀请码形式）
     */
    @PostMapping("/join-family")
    public Result<String> joinFamily(@Valid @RequestBody JoinFamilyDTO dto, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Long familyId = dto.getFamilyId();

        // 检查家庭是否存在
        Family family = familyMapper.selectById(familyId);
        if (family == null) {
            return Result.fail(400, "家庭不存在，请检查家庭ID是否正确");
        }

        // 更新用户 familyId
        User user = new User();
        user.setId(userId);
        user.setFamilyId(familyId);

        int updated = userMapper.updateFamilyId(user);
        if (updated == 0) {
            return Result.fail(500, "加入家庭失败，请重试");
        }

        return Result.ok("成功加入家庭：" + family.getFamilyName());
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@Valid @RequestBody PasswordChangeDTO dto, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail(400, "原密码错误");
        }

        // 更新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        int updated = userMapper.updatePassword(userId, encodedNewPassword);
        if (updated == 0) {
            return Result.fail(500, "密码修改失败");
        }

        return Result.ok("密码修改成功");
    }
}