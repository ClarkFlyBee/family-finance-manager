package com.wcw.backend.Service;

import com.wcw.backend.Common.BizException;
import com.wcw.backend.Common.PageResult;
import com.wcw.backend.DTO.ExpenseDTO;
import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Entity.*;
import com.wcw.backend.Mapper.CategoryMapper;
import com.wcw.backend.Mapper.ExpenseMapper;
import com.wcw.backend.Mapper.FamilyMapper;
import com.wcw.backend.Mapper.UserMapper;
import com.wcw.backend.VO.ExpenseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseMapper expenseMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final FamilyMapper familyMapper;

    public Expense createExpense(ExpenseDTO dto, Long userId){
        Category category = categoryMapper.selectById(dto.getCategoryId());

        if (category == null || !"E".equals(category.getType())) {
            throw new BizException(400, "支出分类不存在或类型错误");
        }

        if ("M".equals(dto.getOwnerType()) && !userMapper.existsById(dto.getOwnerId())) {
            throw new BizException(400, "用户不存在");
        }
        if ("F".equals(dto.getOwnerType()) && !familyMapper.existsById(dto.getOwnerId())) {
            throw new BizException(400, "家庭不存在");
        }

        String expNo = "Ex" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        Expense expense = new Expense();
        BeanUtils.copyProperties(dto, expense);
        expense.setExpNo(expNo);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());

        expenseMapper.insert(expense);
        return expense;
    }

    public ExpenseVO getExpenseById(Long id, Long userId) {
        Expense expense = expenseMapper.selectById(id);
        if (expense == null){
            throw new BizException(400, "支出记录不存在");
        }

        checkAuth(userId, expense);

        ExpenseVO expenseVO = new ExpenseVO();
        expenseVO.setId(expense.getId());
        expenseVO.setExpNo(expense.getExpNo());
        expenseVO.setOwnerType(expense.getOwnerType());
        expenseVO.setOwnerId(expense.getOwnerId());
        if (expense.getOwnerType().equals("M")) {
            expenseVO.setOwnerName(userMapper.selectById(expense.getOwnerId()).getName());
        } else if(expense.getOwnerType().equals("F")) {
            expenseVO.setOwnerName(familyMapper.selectById(expense.getOwnerId()).getFamilyName());
        }
        expenseVO.setCategoryName(categoryMapper.selectById(expense.getCategoryId()).getName());
        expenseVO.setCategoryId(expense.getCategoryId());
        expenseVO.setAmount(expense.getAmount());
        expenseVO.setExpTime(expense.getExpTime());
        expenseVO.setRemark(expense.getRemark());
        expenseVO.setCreatedAt(expense.getCreatedAt());

        return expenseVO;
    }

    public PageResult listExpenses(QueryDTO query, int page, int size, Long userId){
        int offset = (page - 1) * size;
        long total = expenseMapper.selectExpenseCount(query);
        List<ExpenseVO> records = expenseMapper.selectExpensePage(query, offset, size);

        for  (ExpenseVO record : records) {
            if (record.getOwnerType().equals("M")) {
                record.setOwnerName(userMapper.selectById(record.getOwnerId()).getName());
            } else if(record.getOwnerType().equals("F")) {
                record.setOwnerName(familyMapper.selectById(record.getOwnerId()).getFamilyName());
            }
        }

        return new PageResult(total, records);
    }

    public void updateExpense(Long id, ExpenseDTO dto, Long userId){
        Expense expense = expenseMapper.selectById(id);
        if (expense == null){
            throw new BizException(400, "支出记录不存在");
        }

        checkAuth(userId, expense);

        BeanUtils.copyProperties(dto, expense);
        expense.setUpdatedAt(LocalDateTime.now());
        expenseMapper.update(expense);
    }

    public void deleteExpense(Long id, Long userId){
        Expense expense = expenseMapper.selectById(id);
        if (expense == null){
            throw new RuntimeException("记录不存在");
        }
        checkAuth(userId, expense);
        expenseMapper.deleteById(id);
    }

    private void checkAuth(Long userId, Expense expense) {
        if (
                (
                        "M".equals(expense.getOwnerType())
                                && !expense.getOwnerId().equals(userId)
                )
                ||
                (
                        "F".equals(expense.getOwnerType())
                                && !expense.getOwnerId().equals(userMapper.selectById(userId).getFamilyId())
                )
        ) {
            throw new BizException(400, "无权访问");
        }
    }

}
