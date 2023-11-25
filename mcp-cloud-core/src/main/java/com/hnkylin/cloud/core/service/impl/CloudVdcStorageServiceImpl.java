package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudVdcCpuDo;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.core.domain.CloudVdcMemDo;
import com.hnkylin.cloud.core.domain.CloudVdcStorageDo;
import com.hnkylin.cloud.core.enums.StorageUnit;
import com.hnkylin.cloud.core.mapper.CloudVdcStorageMapper;
import com.hnkylin.cloud.core.service.CloudVdcService;
import com.hnkylin.cloud.core.service.CloudVdcStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudVdcStorageServiceImpl extends ServiceImpl<CloudVdcStorageMapper, CloudVdcStorageDo>
        implements CloudVdcStorageService {


    @Resource
    private CloudVdcService vdcService;

    @Override
    public CloudVdcStorageDo getTotalStorageByVdcId(Integer vdcId) {
        CloudVdcStorageDo totalStorageDo = new CloudVdcStorageDo();
        totalStorageDo.setStorage(0);
        totalStorageDo.setUnit(StorageUnit.GB);

        CloudVdcStorageDo queryVdcStorageDo = new CloudVdcStorageDo();
        queryVdcStorageDo.setDeleteFlag(false);
        queryVdcStorageDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcStorageDo> wrapper = new QueryWrapper<>(queryVdcStorageDo);
        List<CloudVdcStorageDo> list = getBaseMapper().selectList(wrapper);
        if (!list.isEmpty()) {
            int totalStorage = list.stream().collect(Collectors.summingInt(CloudVdcStorageDo::getStorage));
            totalStorageDo.setStorage(totalStorage);
            totalStorageDo.setUnit(list.get(0).getUnit());
        }


        return totalStorageDo;
    }


    @Override
    public CloudVdcStorageDo getChildVdcTotalStorage(Integer parentVdcId) {
        List<Integer> childVdcIdList = vdcService.getChildVdcIdList(parentVdcId);
        CloudVdcStorageDo childTotalStorageDo = new CloudVdcStorageDo();
        childTotalStorageDo.setStorage(0);
        childTotalStorageDo.setUnit(StorageUnit.GB);
        if (!childVdcIdList.isEmpty()) {
            CloudVdcStorageDo queryVdcStorageDo = new CloudVdcStorageDo();
            queryVdcStorageDo.setDeleteFlag(false);
            QueryWrapper<CloudVdcStorageDo> wrapper = new QueryWrapper<>(queryVdcStorageDo);
            wrapper.in("vdc_id", childVdcIdList);
            List<CloudVdcStorageDo> list = getBaseMapper().selectList(wrapper);
            if (!list.isEmpty()) {
                int totalStorage = list.stream().collect(Collectors.summingInt(CloudVdcStorageDo::getStorage));
                childTotalStorageDo.setStorage(totalStorage);
                childTotalStorageDo.setUnit(list.get(0).getUnit());
            }
        }

        return childTotalStorageDo;
    }

    @Override
    @Transactional
    public void deleteByVdcId(Integer vdcId, Integer deleteUser) {
        CloudVdcStorageDo queryVdcStorageDo = new CloudVdcStorageDo();
        queryVdcStorageDo.setDeleteFlag(false);
        queryVdcStorageDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcStorageDo> wrapper = new QueryWrapper<>(queryVdcStorageDo);
        List<CloudVdcStorageDo> list = getBaseMapper().selectList(wrapper);
        if (!list.isEmpty()) {
            list.forEach(item -> {
                item.setDeleteFlag(true);
                item.setDeleteBy(deleteUser);
                item.setDeleteTime(new Date());
            });
            updateBatchById(list);
        }
    }

    @Override
    public CloudVdcStorageDo getByVdcId(Integer vdcId) {
        CloudVdcStorageDo queryVdcStorageDo = new CloudVdcStorageDo();
        queryVdcStorageDo.setDeleteFlag(false);
        queryVdcStorageDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcStorageDo> wrapper = new QueryWrapper<>(queryVdcStorageDo);
        List<CloudVdcStorageDo> list = getBaseMapper().selectList(wrapper);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
