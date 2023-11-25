package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudVdcMemDo;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.mapper.CloudVdcMemMapper;
import com.hnkylin.cloud.core.service.CloudVdcMemService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CloudVdcMemServiceImpl extends ServiceImpl<CloudVdcMemMapper, CloudVdcMemDo>
        implements CloudVdcMemService {


    @Resource
    private CloudVdcService vdcService;

    @Override
    public CloudVdcMemDo totalMemByVdcId(Integer vdcId) {
        CloudVdcMemDo totalMemDo = new CloudVdcMemDo();
        totalMemDo.setMem(0);
        totalMemDo.setMemUnit(MemUnit.GB);

        CloudVdcMemDo queryVdcMemDo = new CloudVdcMemDo();
        queryVdcMemDo.setDeleteFlag(false);
        queryVdcMemDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcMemDo> wrapper = new QueryWrapper<>(queryVdcMemDo);
        List<CloudVdcMemDo> list = getBaseMapper().selectList(wrapper);
        if (!list.isEmpty()) {
            int totalMem = list.stream().collect(Collectors.summingInt(CloudVdcMemDo::getMem));
            totalMemDo.setMem(totalMem);
            totalMemDo.setMemUnit(list.get(0).getMemUnit());
        }
        return totalMemDo;
    }


    @Override
    public CloudVdcMemDo totalMemByVdcIdAndArchitectureType(Integer vdcId, ArchitectureType architectureType) {

        CloudVdcMemDo queryVdcMemDo = new CloudVdcMemDo();
        queryVdcMemDo.setDeleteFlag(false);
        queryVdcMemDo.setVdcId(vdcId);
        queryVdcMemDo.setArchitecture(architectureType);
        QueryWrapper<CloudVdcMemDo> wrapper = new QueryWrapper<>(queryVdcMemDo);
        List<CloudVdcMemDo> list = getBaseMapper().selectList(wrapper);

        if (!list.isEmpty()) {
            CloudVdcMemDo totalMemDo = new CloudVdcMemDo();
            int totalMem = list.stream().collect(Collectors.summingInt(CloudVdcMemDo::getMem));
            totalMemDo.setMem(totalMem);
            totalMemDo.setMemUnit(list.get(0).getMemUnit());
            return totalMemDo;
        }
        return null;
    }

    @Override
    public CloudVdcMemDo totalChildVdcMemByParentIdAndArchitectureType(Integer parentVdcId,
                                                                       ArchitectureType architectureType) {
        CloudVdcMemDo totalChildMemDo = new CloudVdcMemDo();
        totalChildMemDo.setMem(0);
        totalChildMemDo.setMemUnit(MemUnit.GB);
        List<Integer> childVdcIdList = vdcService.getChildVdcIdList(parentVdcId);

        if (childVdcIdList.isEmpty()) {
            return totalChildMemDo;
        }
        CloudVdcMemDo queryVdcMemDo = new CloudVdcMemDo();
        queryVdcMemDo.setDeleteFlag(false);
        if (Objects.nonNull(architectureType)) {
            queryVdcMemDo.setArchitecture(architectureType);
        }

        QueryWrapper<CloudVdcMemDo> wrapper = new QueryWrapper<>(queryVdcMemDo);
        wrapper.in("vdc_id", childVdcIdList);
        List<CloudVdcMemDo> list = getBaseMapper().selectList(wrapper);
        if (!list.isEmpty()) {
            int totalMem = list.stream().collect(Collectors.summingInt(CloudVdcMemDo::getMem));
            totalChildMemDo.setMem(totalMem);
            totalChildMemDo.setMemUnit(list.get(0).getMemUnit());

        }
        return totalChildMemDo;
    }


    @Override
    public List<CloudVdcMemDo> listVdcMemByVdcId(Integer vdcId) {
        CloudVdcMemDo queryVdcMemDo = new CloudVdcMemDo();
        queryVdcMemDo.setDeleteFlag(false);
        queryVdcMemDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcMemDo> wrapper = new QueryWrapper<>(queryVdcMemDo);
        List<CloudVdcMemDo> list = getBaseMapper().selectList(wrapper);
        return list;
    }

    @Override
    @Transactional
    public void deleteByVdcId(Integer vdcId, Integer deleteUser) {
        List<CloudVdcMemDo> list = listVdcMemByVdcId(vdcId);
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
