package com.wcw.backend.Controller;

import com.wcw.backend.Common.PageResult;
import com.wcw.backend.Common.Result;
import com.wcw.backend.DTO.ExpenseDTO;
import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Entity.Expense;
import com.wcw.backend.Service.ExpenseService;
import com.wcw.backend.Util.JwtUtil;
import com.wcw.backend.VO.ExpenseVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public Result<Expense> create(@RequestBody @Valid ExpenseDTO dto,
                                  HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Expense expense = expenseService.createExpense(dto, userId);
        return Result.ok(expense);
    }

    @GetMapping("/{id}")
    public Result<ExpenseVO> get(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        ExpenseVO vo = expenseService.getExpenseById(id, userId);
        return Result.ok(vo);
    }

    @GetMapping
    public Result<PageResult> list(QueryDTO query,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        PageResult result = expenseService.listExpenses(query, page, size, userId);
        return Result.ok(result);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody @Valid ExpenseDTO dto,
                               HttpServletRequest request){
        Long userId = JwtUtil.getUserId(request);
        expenseService.updateExpense(id, dto, userId);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request){
        Long userId = JwtUtil.getUserId(request);
        expenseService.deleteExpense(id, userId);
        return Result.ok();
    }

}
