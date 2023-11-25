package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudVdcMemDo;
import com.hnkylin.cloud.core.enums.ArchitectureType;

import java.util.List;


public interface CloudVdcMemService extends IService<CloudVdcMemDo> {

    /**
     * 根据vdc获取内存大小
     *
     * @param vdcId
     * @return
     */
    CloudVdcMemDo totalMemByVdcId(Integer vdcId);

    /**
     * 获取vdc下不同架构的内存
     *
     * @param vdcId
     * @param architectureType
     * @return
     */
    CloudVdcMemDo totalMemByVdcIdAndArchitectureType(Integer vdcId, ArchitectureType architectureType);


    /**
     * 获取下级VDC 架构下的内存
     *
     * @param parentVdcId
     * @param architectureType
     * @return
     */
    CloudVdcMemDo totalChildVdcMemByParentIdAndArchitectureType(Integer parentVdcId, ArchitectureType architectureType);

    /**
     * 根据vdcId获取vdc内存列表
     *
     * @param vdcId
     * @return
     */
    List<CloudVdcMemDo> listVdcMemByVdcId(Integer vdcId);

    /**
     * 通过vdc删除
     *
     * @param vdcId
     */
    void deleteByVdcId(Integer vdcId, Integer deleteUser);
}
