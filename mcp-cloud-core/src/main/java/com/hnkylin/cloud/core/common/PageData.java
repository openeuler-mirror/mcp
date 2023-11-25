package com.hnkylin.cloud.core.common;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class PageData<T> {

    private List<T> list;

    private PageDetail pageInfo;


    public PageData(PageInfo<T> data) {

        if (data != null) {
            this.list = data.getList();
        } else {
            this.list = new ArrayList<T>();
        }
        changePageInfo(data);
    }

    public PageData(McPageInfo mcPageInfo, List<T> data) {

        if (data != null) {
            this.list = data;
        } else {
            this.list = null;
        }
        changeMcPageInfo(mcPageInfo);
    }

    private void changePageInfo(PageInfo<T> data) {
        this.pageInfo = new PageDetail();
        if (data != null) {
            pageInfo.setCurrentPage(data.getPageNum());
            pageInfo.setCurrentSize(data.getSize());
            pageInfo.setTotal(data.getTotal());
            pageInfo.setTotalPage(data.getPages());
        } else {
            pageInfo.setCurrentPage(0);
            pageInfo.setCurrentSize(0);
            pageInfo.setTotal(0L);
            pageInfo.setTotalPage(0);
        }
    }

    private void changeMcPageInfo(McPageInfo mcPageInfo) {

        this.pageInfo = new PageDetail();
        pageInfo.setCurrentPage(mcPageInfo.getPager());
        pageInfo.setCurrentSize(mcPageInfo.getPageSize());
        pageInfo.setTotal(mcPageInfo.getRecords());
        pageInfo.setTotalPage(mcPageInfo.getTotal());


    }

}