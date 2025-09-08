package com.wcw.backend.Common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {
    private long total;     // 总记录数
    private List<?> data;   // 当前页数据

}
