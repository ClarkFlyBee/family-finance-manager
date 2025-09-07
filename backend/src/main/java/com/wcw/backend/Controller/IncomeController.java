package com.wcw.backend.Controller;

import com.wcw.backend.Common.PageResult;
import com.wcw.backend.Common.Result;
import com.wcw.backend.DTO.IncomeDTO;
import com.wcw.backend.DTO.IncomeQueryDTO;
import com.wcw.backend.Entity.Income;
import com.wcw.backend.Service.IncomeService;
import com.wcw.backend.Util.JwtUtil;
import com.wcw.backend.VO.IncomeVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public Result<Income> create(@RequestBody @Valid IncomeDTO dto,
                                 HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Income income = incomeService.createIncome(dto, userId);
        return Result.ok(income);
    }

    @GetMapping("/{id}")
    public Result<IncomeVO> get(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        IncomeVO vo = incomeService.getIncomeById(id, userId);
        return Result.ok(vo);
    }

    @GetMapping
    public Result<PageResult> list(IncomeQueryDTO query,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        PageResult result = incomeService.listIncomes(query, page, size, userId);
        return Result.ok(result);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody @Valid IncomeDTO dto,
                               HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        incomeService.updateIncome(id, dto, userId);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        incomeService.deleteIncome(id, userId);
        return Result.ok();
    }
}
