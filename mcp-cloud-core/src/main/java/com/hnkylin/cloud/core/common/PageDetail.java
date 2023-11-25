package com.hnkylin.cloud.core.common;

import lombok.Data;

@Data
public class PageDetail {


    private Integer currentPage;

    private Integer currentSize;

    private long total;

    private Integer totalPage;
}
