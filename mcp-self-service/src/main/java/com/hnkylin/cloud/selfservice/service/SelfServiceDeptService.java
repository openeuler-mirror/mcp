package com.hnkylin.cloud.selfservice.service;

import com.hnkylin.cloud.selfservice.entity.resp.DeptRespDto;

import java.util.List;

public interface SelfServiceDeptService {

    /**
     * 获取部门列表
     */
    List<DeptRespDto> queryDeptList();
}
