package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudNetworkConfigDo;
import com.hnkylin.cloud.core.domain.CloudVdcMemDo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface CloudNetworkConfigService extends IService<CloudNetworkConfigDo> {


    /**
     * 根据vdc 获取网络列表
     *
     * @param vdcId
     * @return
     */
    List<CloudNetworkConfigDo> listNetworkListByVdcId(Integer vdcId);


    /**
     * 通过vdc删除
     *
     * @param vdcId
     */
    void deleteByVdcId(Integer vdcId, Integer deleteUser);


}
