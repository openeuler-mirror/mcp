package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudVdcStorageDo;


public interface CloudVdcStorageService extends IService<CloudVdcStorageDo> {


    /**
     * 根据VDC获取总存储大小
     *
     * @param vdcId
     * @return
     */
    CloudVdcStorageDo getTotalStorageByVdcId(Integer vdcId);


    /**
     * 根据父VDC获取分配给下级VDC的存储大小
     *
     * @param parentVdcId
     * @return
     */
    CloudVdcStorageDo getChildVdcTotalStorage(Integer parentVdcId);


    /**
     * 通过vdc删除
     *
     * @param vdcId
     */
    void deleteByVdcId(Integer vdcId, Integer deleteUser);


    /**
     * 获取VDC的存储资源
     *
     * @param vdcId
     * @return
     */
    CloudVdcStorageDo getByVdcId(Integer vdcId);
}
