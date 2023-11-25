package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudNetworkConfigDo;
import com.hnkylin.cloud.core.mapper.CloudNetworkConfigMapper;
import com.hnkylin.cloud.core.service.CloudNetworkConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
public class CloudNetworkConfigServiceImpl extends ServiceImpl<CloudNetworkConfigMapper, CloudNetworkConfigDo> implements CloudNetworkConfigService {


    @Override
    public List<CloudNetworkConfigDo> listNetworkListByVdcId(Integer vdcId) {
        CloudNetworkConfigDo networkConfigDo = new CloudNetworkConfigDo();
        networkConfigDo.setDeleteFlag(false);
        networkConfigDo.setVdcId(vdcId);
        QueryWrapper<CloudNetworkConfigDo> wrapper = new QueryWrapper<>(networkConfigDo);
        List<CloudNetworkConfigDo> list = getBaseMapper().selectList(wrapper);
        return list;
    }

    @Override
    @Transactional
    public void deleteByVdcId(Integer vdcId, Integer deleteUser) {
        List<CloudNetworkConfigDo> list = listNetworkListByVdcId(vdcId);
        if (!list.isEmpty()) {
            list.forEach(item -> {
                item.setDeleteFlag(true);
                item.setDeleteBy(deleteUser);
                item.setDeleteTime(new Date());
            });
            updateBatchById(list);
        }
    }
}
