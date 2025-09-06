package com.wcw.backend.Controller;

import com.wcw.backend.Common.Result;
import com.wcw.backend.DTO.LoginDTO;
import com.wcw.backend.DTO.RegisterDTO;
import com.wcw.backend.Entity.User;
import com.wcw.backend.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto){
        return Result.ok(userService.register(dto));
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto){
        return Result.ok(Map.of("token", userService.login(dto)));
    }

    @GetMapping("/user/list")
    public Result<?> getAllUser(){
        return Result.ok(userService.getAllUser());
    }

    @GetMapping("/family/list")
    public Result<?> getAllFamily(){
        return Result.ok(userService.getAllFamily());
    }
}
