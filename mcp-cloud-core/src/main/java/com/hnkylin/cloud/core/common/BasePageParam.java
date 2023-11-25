package com.hnkylin.cloud.core.common;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BasePageParam {
    @FieldCheck(minNum = 1, minNumMessage = "分页数不能小于1")
    private Integer pageNo;

    @FieldCheck(minNum = 1, minNumMessage = "每页显示数量不能小于1")
    private Integer pageSize;
}
