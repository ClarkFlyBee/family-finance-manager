package com.wcw.backend.Mapper;

import com.wcw.backend.DTO.QueryDTO;
import com.wcw.backend.Entity.Income;
import com.wcw.backend.VO.IncomeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface IncomeMapper {

    // 新增
    int insert(Income income);

    // 根据 ID 查询
    Income selectById(Long id);

    // 分页 + 条件查询（返回 VO）
    List<IncomeVO> selectIncomePage(@Param("query") QueryDTO query,
                                    @Param("offset") int offset,
                                    @Param("size") int size);

    // 查询总数（用于分页）
    long selectIncomeCount(@Param("query") QueryDTO query);

    // 更新
    int update(Income income);

    // 删除（物理删除）
    int deleteById(Long id);

    @Select("""
           SELECT
                DATE_FORMAT(inc_time, #{format}) as day,
                SUM(amount) as amount
            FROM income
            WHERE owner_id = #{query.ownerId}
              AND owner_type = #{query.ownerType}
              AND inc_time BETWEEN #{query.startTime} AND #{query.endTime}
            GROUP BY DATE_FORMAT(inc_time, #{format})
            ORDER BY day
    """)
    List<Map<String, Object>> sumIncomeByPeriod(@Param("query") QueryDTO queryDTO, @Param("format") String format);
}
