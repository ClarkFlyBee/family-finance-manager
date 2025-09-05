package com.wcw.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 测试数据库连通情况
    @GetMapping("/db-check")
    public Map<String,Object> dbCheck(){
        // 简单的SQL
         Map<String,Object> result = jdbcTemplate.queryForMap("select 1 as ok");
         return result;
    }

    // 测试SQL语句能否正常执行
    @GetMapping("/sql-check")
    public List<Map<String,Object>> SQLCheck(){
        // 查
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM t_test ORDER BY id");

        // 插
        jdbcTemplate.update("INSERT INTO t_test(name) VALUES (?)", "Controller-Test-" + System.currentTimeMillis());

        // 再查
        return jdbcTemplate.queryForList("SELECT * FROM t_test ORDER BY id DESC LIMIT 10");
    }
}
