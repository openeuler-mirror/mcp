package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudVdcCpuDo;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.mapper.CloudVdcCpuMapper;
import com.hnkylin.cloud.core.service.CloudVdcCpuService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CloudVdcCpuServiceImpl extends ServiceImpl<CloudVdcCpuMapper, CloudVdcCpuDo>
        implements CloudVdcCpuService {


    @Resource
    private CloudVdcService vdcService;

    @Override
    public Integer totalCpuByVdcId(Integer vdcId) {
        CloudVdcCpuDo queryVdcCpuDo = new CloudVdcCpuDo();
        queryVdcCpuDo.setDeleteFlag(false);
        queryVdcCpuDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcCpuDo> vdcCpuWrapper = new QueryWrapper<>(queryVdcCpuDo);
        List<CloudVdcCpuDo> vdcCpuList = getBaseMapper().selectList(vdcCpuWrapper);
        return vdcCpuList.isEmpty() ? 0 : vdcCpuList.stream().collect(Collectors.summingInt(CloudVdcCpuDo::getVcpus));
    }


    @Override
    public Integer totalCpuByVdcIdAndArchitectureType(Integer vdcId, ArchitectureType architectureType) {
        CloudVdcCpuDo queryVdcCpuDo = new CloudVdcCpuDo();
        queryVdcCpuDo.setDeleteFlag(false);
        queryVdcCpuDo.setVdcId(vdcId);
        queryVdcCpuDo.setArchitecture(architectureType);
        QueryWrapper<CloudVdcCpuDo> vdcCpuWrapper = new QueryWrapper<>(queryVdcCpuDo);
        List<CloudVdcCpuDo> vdcCpuList = getBaseMapper().selectList(vdcCpuWrapper);
        return vdcCpuList.isEmpty() ? null :
                vdcCpuList.stream().collect(Collectors.summingInt(CloudVdcCpuDo::getVcpus));
    }


    @Override
    public Integer totalChildVdcCpuByParentIdAndArchitectureType(Integer parentVdcId,
                                                                 ArchitectureType architectureType) {
        List<Integer> childVdcIdList = vdcService.getChildVdcIdList(parentVdcId);

        if (childVdcIdList.isEmpty()) {
            return 0;
        }
        CloudVdcCpuDo queryVdcCpuDo = new CloudVdcCpuDo();
        queryVdcCpuDo.setDeleteFlag(false);
        if (Objects.nonNull(architectureType)) {
            queryVdcCpuDo.setArchitecture(architectureType);
        }

        QueryWrapper<CloudVdcCpuDo> vdcCpuWrapper = new QueryWrapper<>(queryVdcCpuDo);
        vdcCpuWrapper.in("vdc_id", childVdcIdList);
        List<CloudVdcCpuDo> vdcCpuList = getBaseMapper().selectList(vdcCpuWrapper);
        return vdcCpuList.isEmpty() ? 0 : vdcCpuList.stream().collect(Collectors.summingInt(CloudVdcCpuDo::getVcpus));
    }


    @Override
    public List<CloudVdcCpuDo> listVDdcCpuByVdc(Integer vdcId) {
        CloudVdcCpuDo queryVdcCpuDo = new CloudVdcCpuDo();
        queryVdcCpuDo.setDeleteFlag(false);
        queryVdcCpuDo.setVdcId(vdcId);
        QueryWrapper<CloudVdcCpuDo> vdcCpuWrapper = new QueryWrapper<>(queryVdcCpuDo);
        List<CloudVdcCpuDo> vdcCpuList = getBaseMapper().selectList(vdcCpuWrapper);
        return vdcCpuList;
    }


    @Override
    @Transactional
    public void deleteByVdcId(Integer vdcId, Integer deleteUser) {
        List<CloudVdcCpuDo> cpuDoList = listVDdcCpuByVdc(vdcId);
        if (!cpuDoList.isEmpty()) {
            cpuDoList.forEach(item -> {
                item.setDeleteFlag(true);
                item.setDeleteBy(deleteUser);
                item.setDeleteTime(new Date());
            });
            updateBatchById(cpuDoList);
        }
    }
}
