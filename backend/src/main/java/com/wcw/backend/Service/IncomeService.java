package com.wcw.backend.Service;

import com.wcw.backend.Common.BizException;
import com.wcw.backend.Common.PageResult;
import com.wcw.backend.DTO.IncomeDTO;
import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Entity.Category;
import com.wcw.backend.Entity.Income;
import com.wcw.backend.Mapper.CategoryMapper;
import com.wcw.backend.Mapper.FamilyMapper;
import com.wcw.backend.Mapper.IncomeMapper;
import com.wcw.backend.Mapper.UserMapper;
import com.wcw.backend.VO.ExpenseVO;
import com.wcw.backend.VO.IncomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService{

    private final IncomeMapper incomeMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final FamilyMapper familyMapper;

    public Income createIncome(IncomeDTO dto, Long userId) {
        // 校验分类
        Category category = categoryMapper.selectById(dto.getCategoryId());

        if (category == null || !"I".equals(category.getType())) {
            throw new BizException(400, "收入分类不存在或类型错误");
        }

        // 校验归属
        if ("M".equals(dto.getOwnerType()) && !userMapper.existsById(dto.getOwnerId())) {
            throw new BizException(400, "用户不存在");
        }
        if ("F".equals(dto.getOwnerType()) && !familyMapper.existsById(dto.getOwnerId())) {
            throw new BizException(400, "家庭不存在");
        }

        // 生成编号
        String incNo = "IN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        Income income = new Income();
        BeanUtils.copyProperties(dto, income);
        income.setIncNo(incNo);
        income.setCreatedAt(LocalDateTime.now());
        income.setUpdatedAt(LocalDateTime.now());

        incomeMapper.insert(income);
        return income;
    }

    public IncomeVO getIncomeById(Long id, Long userId) {
        Income income = incomeMapper.selectById(id);
        if (income == null) {
            throw new BizException(400, "收入记录不存在");
        }

        checkAuth(userId, income);

        IncomeVO incomeVO = new IncomeVO();
        incomeVO.setId(income.getId());
        incomeVO.setIncNo(income.getIncNo());
        incomeVO.setOwnerType(income.getOwnerType());
        incomeVO.setOwnerId(income.getOwnerId());
        if (income.getOwnerType().equals("M")) {
            incomeVO.setOwnerName(userMapper.selectById(income.getOwnerId()).getName());
        } else if(income.getOwnerType().equals("F")) {
            incomeVO.setOwnerName(familyMapper.selectById(income.getOwnerId()).getFamilyName());
        }
        incomeVO.setCategoryName(categoryMapper.selectById(income.getCategoryId()).getName());
        incomeVO.setCategoryId(income.getCategoryId());
        incomeVO.setAmount(income.getAmount());
        incomeVO.setIncTime(income.getIncTime());
        incomeVO.setRemark(income.getRemark());
        incomeVO.setCreatedAt(income.getCreatedAt());

        return incomeVO;
    }

    public PageResult listIncomes(QueryDTO query, int page, int size, Long userId) {
        int offset = (page - 1) * size;

        long total = incomeMapper.selectIncomeCount(query);
        List<IncomeVO> records = incomeMapper.selectIncomePage(query, offset, size);

        for  (IncomeVO record : records) {
            if (record.getOwnerType().equals("M")) {
                record.setOwnerName(userMapper.selectById(record.getOwnerId()).getName());
            } else if(record.getOwnerType().equals("F")) {
                record.setOwnerName(familyMapper.selectById(record.getOwnerId()).getFamilyName());
            }
        }

        return new PageResult(total, records);
    }

    public void updateIncome(Long id, IncomeDTO dto, Long userId) {
        Income income = incomeMapper.selectById(id);
        if (income == null) {
            throw new BizException(400, "收入记录不存在");
        }

        // 权限校验（仅创建者可修改）
        checkAuth(userId, income);

        BeanUtils.copyProperties(dto, income);
        income.setUpdatedAt(LocalDateTime.now());
        incomeMapper.update(income);
    }

    public void deleteIncome(Long id, Long userId) {
        Income income = incomeMapper.selectById(id);
        if (income == null) {
            throw new RuntimeException("记录不存在");
        }

        // 权限校验（仅创建者可删除）
        checkAuth(userId, income);


        incomeMapper.deleteById(id);
    }

    private void checkAuth(Long userId, Income income) {
        if ("M".equals(income.getOwnerType()) && !income.getOwnerId().equals(userId)) {
            throw new BizException(400, "无权访问");
        }
        if ("F".equals(income.getOwnerType()) && income.getOwnerId().equals(userMapper.selectById(userId).getFamilyId())) {
            throw new BizException(400, "无权访问");
        }
    }
}
