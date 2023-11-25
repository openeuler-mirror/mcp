package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudUserMachineDo;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.mapper.CloudUserMachineMapper;
import com.hnkylin.cloud.core.service.CloudUserMachineService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CloudUserMachineServiceImpl extends ServiceImpl<CloudUserMachineMapper, CloudUserMachineDo> implements
        CloudUserMachineService {


    @Override
    public CloudUserMachineDo getUserMachineDoByUuid(String uuid) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setMachineUuid(uuid);
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        CloudUserMachineDo userMachineDo = getOne(queryWrapper);
        return userMachineDo;
    }

    @Override
    public CloudUserMachineDo getUserMachineDoByUuidAndUserId(String uuid, Integer userId) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setMachineUuid(uuid);
        cloudUserMachineDo.setUserId(userId);
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        CloudUserMachineDo userMachineDo = getOne(queryWrapper);
        return userMachineDo;
    }


}
