package com.hnkylin.cloud.core.common;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
@Data
public class McPageInfo {

    private Integer pager;

    //总页数
    private Integer total;

    //总记录数
    private Integer records;

    private Integer pageSize;
}
