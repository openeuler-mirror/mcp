package com.hnkylin.cloud.selfservice.service;

import com.hnkylin.cloud.selfservice.entity.resp.OrganizationTreeDto;

import java.util.List;

public interface SelfServiceOrganizationService {


    /**
     * 获取组织列表
     */
    List<OrganizationTreeDto> queryOrganizationTree();
}
