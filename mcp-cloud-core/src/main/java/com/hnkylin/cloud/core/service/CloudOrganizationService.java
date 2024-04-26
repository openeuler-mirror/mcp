package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudOrganizationDo;

import java.util.List;

public interface CloudOrganizationService extends IService<CloudOrganizationDo> {

    List<CloudOrganizationDo> queryAllOrgList();


    /**
     * 通过组织名称获取组织
     *
     * @param organizationName
     * @return
     */
    CloudOrganizationDo queryByOrganizationNameName(String organizationName);


    /**
     * 根据组织ID获取所有子组织(包括自己)
     *
     * @param orgId
     * @return
     */
    List<CloudOrganizationDo> listChildOrgByOrgId(Integer orgId);


    /**
     * 获取系统默认最顶级组织
     *
     * @return
     */
    CloudOrganizationDo getDefaultTopOrg();

    /**
     * 根据vdc集合获取组织列表
     *
     * @param vdcList
     * @return
     */
    List<CloudOrganizationDo> getOrgListByVdcList(List<Integer> vdcList);


}
