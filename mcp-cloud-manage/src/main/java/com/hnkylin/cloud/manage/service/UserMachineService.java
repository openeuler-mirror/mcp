package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.servervm.McServerVmPageDetailResp;
import com.hnkylin.cloud.core.domain.CloudUserMachineDo;

import java.util.List;

public interface UserMachineService {

    /**
     * 用户是否拥有云服务器
     *
     * @param userId
     * @return
     */
    boolean userHasMachine(Integer userId);

    /**
     * 根据用户ID列表计算云服务器数量
     *
     * @param userIdList
     * @return
     */
    Integer countUserMachineByUserIdList(List<Integer> userIdList);


    /**
     * 根据用户ID列表获取用户拥有的云服务器列表
     *
     * @param userIdList
     * @return
     */
    List<CloudUserMachineDo> listUserMachineByUserIdList(List<Integer> userIdList);


    /**
     * 从mc获取云服务器列表
     *
     * @param userIdList
     * @return
     */
    List<McServerVmPageDetailResp> listUserMachineByUserIdListAndClusterId(Integer clusterId, List<Integer> userIdList,
                                                                           String userName);


    /**
     * 用户可见的云服务器
     *
     * @param userId
     * @return
     */
    List<CloudUserMachineDo> userVisibleUserMachineList(Integer userId);


}
