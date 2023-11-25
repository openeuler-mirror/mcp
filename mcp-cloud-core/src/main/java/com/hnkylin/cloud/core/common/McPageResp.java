package com.hnkylin.cloud.core.common;

import lombok.Data;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-7-16.
 */
@Data
public class McPageResp<T> {

    private Integer pager;

    //总页数
    private Integer total;

    //总记录数
    private Integer records;

    private List<T> rows;
}
