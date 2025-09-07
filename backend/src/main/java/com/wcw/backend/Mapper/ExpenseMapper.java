package com.wcw.backend.Mapper;

import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Entity.Expense;
import com.wcw.backend.VO.ExpenseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExpenseMapper {

    int insert(Expense e);

    Expense selectById(Long id);

    List<ExpenseVO> selectExpensePage(@Param("query")QueryDTO query,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    long selectExpenseCount(@Param("query")QueryDTO query);

    int update(Expense expense);

    int deleteById(Long id);
}
