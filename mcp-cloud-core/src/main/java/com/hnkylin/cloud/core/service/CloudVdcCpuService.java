package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudVdcCpuDo;
import com.hnkylin.cloud.core.enums.ArchitectureType;

import java.util.List;


public interface CloudVdcCpuService extends IService<CloudVdcCpuDo> {

    /**
     * 根据VDC获取总cpu
     *
     * @param vdcId
     * @return
     */
    Integer totalCpuByVdcId(Integer vdcId);


    /**
     * 获取vdc下不同架构的cpu数
     *
     * @param vdcId
     * @param architectureType
     * @return
     */
    Integer totalCpuByVdcIdAndArchitectureType(Integer vdcId, ArchitectureType architectureType);


    /**
     * 获取下级VDC 架构下的cpu数
     *
     * @param parentVdcId
     * @param architectureType
     * @return
     */
    Integer totalChildVdcCpuByParentIdAndArchitectureType(Integer parentVdcId, ArchitectureType architectureType);


    /**
     * 根据vdcId 获取vdc的cpu列表
     *
     * @param vdcId
     * @return
     */
    List<CloudVdcCpuDo> listVDdcCpuByVdc(Integer vdcId);

    /**
     * 通过vdc删除
     *
     * @param vdcId
     */
    void deleteByVdcId(Integer vdcId, Integer deleteUser);


}
