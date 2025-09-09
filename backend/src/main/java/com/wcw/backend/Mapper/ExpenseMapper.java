package com.wcw.backend.Mapper;

import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Entity.Expense;
import com.wcw.backend.VO.ExpenseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

    @Select("""
    SELECT 
        DATE_FORMAT(exp_time, #{format}) as day,
        SUM(amount) as amount
    FROM expense
    WHERE owner_id = #{query.ownerId}
      AND exp_time BETWEEN #{query.startTime} AND #{query.endTime}
    GROUP BY DATE_FORMAT(exp_time, #{format})
    ORDER BY day
    """)
    List<Map<String, Object>> sumExpenseByPeriod(@Param("query") QueryDTO queryDTO, @Param("format") String format);
}
