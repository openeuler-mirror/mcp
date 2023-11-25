package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudNetworkConfigDo;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkDetailDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-11-23.
 */
public interface NetworkMapper extends BaseMapper<CloudNetworkConfigDo> {


    /**
     * 根据vdc获取网络列表
     *
     * @param vdcId
     * @return
     */
    List<NetworkDetailDto> networkListByVdcId(@Param("vdcId") Integer vdcId);


}
