package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudOrgVdcDo;

import java.util.List;


public interface CloudOrgVdcService extends IService<CloudOrgVdcDo> {

    /**
     * 根据组织ID获取 组织和VDC对应关系
     *
     * @param orgId
     * @return
     */
    CloudOrgVdcDo queryOrgVdcByOrgId(Integer orgId);


    /**
     * 获取可用区下组织和vdc关联记录
     *
     * @param zoneId
     * @return
     */
    List<Integer> orgIdListByZoneId(Integer zoneId);

}
