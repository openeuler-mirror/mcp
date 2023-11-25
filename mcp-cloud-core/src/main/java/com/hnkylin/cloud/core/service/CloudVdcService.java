package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudVdcDo;

import java.util.List;


public interface CloudVdcService extends IService<CloudVdcDo> {


    /**
     * 获取下级VDC列表
     *
     * @param parentVdcId
     * @return
     */
    List<CloudVdcDo> getChildVdcList(Integer parentVdcId);

    /**
     * 获取下级VDC  id列表
     *
     * @param parentVdcId
     * @return
     */
    List<Integer> getChildVdcIdList(Integer parentVdcId);

    /**
     * 根据可用区获取可用区下vdc列表
     *
     * @param zoneId
     * @return
     */
    List<CloudVdcDo> vdcListByZone(Integer zoneId);


    /**
     * 根据组织ID获取对应的vdc
     *
     * @param orgId
     * @return
     */
    CloudVdcDo getVdcByOrgId(Integer orgId);


    /**
     * 获取可用区所有一级VDC列表
     *
     * @param zoneId
     * @return
     */
    List<CloudVdcDo> getFirstVdcListByZone(Integer zoneId);


    /**
     * 获取所有下级VDC列表
     *
     * @param parentVdcId
     * @return
     */
    List<CloudVdcDo> getAllChildVdcList(Integer parentVdcId);

}
