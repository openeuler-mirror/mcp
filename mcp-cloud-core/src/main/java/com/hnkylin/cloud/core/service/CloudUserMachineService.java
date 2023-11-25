package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudUserMachineDo;

import java.util.List;

public interface CloudUserMachineService extends IService<CloudUserMachineDo> {


    /**
     * @param uuid
     * @return
     */
    CloudUserMachineDo getUserMachineDoByUuid(String uuid);


    CloudUserMachineDo getUserMachineDoByUuidAndUserId(String uuid, Integer userId);


}
