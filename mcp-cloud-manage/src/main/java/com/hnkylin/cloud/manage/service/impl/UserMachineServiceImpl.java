package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.common.servervm.McServerVmPageDetailResp;
import com.hnkylin.cloud.core.common.servervm.ServerVmListReq;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudUserMachineDo;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.service.CloudUserMachineService;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmPageParam;
import com.hnkylin.cloud.manage.entity.resp.serverVm.PageServerVmRespDto;
import com.hnkylin.cloud.manage.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserMachineServiceImpl implements UserMachineService {

    @Resource
    private CloudUserMachineService cloudUserMachineService;

    @Resource
    private UserService userService;

    @Resource
    private McHttpService mcHttpService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private McClusterThreadService mcClusterThreadService;

    @Resource
    private RoleService roleService;

    @Override
    public boolean userHasMachine(Integer userId) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setUserId(userId);
        cloudUserMachineDo.setDeleteFlag(false);
        Wrapper<CloudUserMachineDo> wrapper = new QueryWrapper<>(cloudUserMachineDo);
        int existUserCount = cloudUserMachineService.getBaseMapper().selectCount(wrapper);
        return existUserCount > 0;
    }

    @Override
    public Integer countUserMachineByUserIdList(List<Integer> userIdList) {

        return listUserMachineByUserIdList(userIdList).size();
    }

    @Override
    public List<CloudUserMachineDo> listUserMachineByUserIdList(List<Integer> userIdList) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setDeleteFlag(false);
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        queryWrapper.in("user_id", userIdList);
        return cloudUserMachineService.list(queryWrapper);
    }

    @Override
    public List<McServerVmPageDetailResp> listUserMachineByUserIdListAndClusterId(Integer clusterId,
                                                                                  List<Integer> userIdList,
                                                                                  String userName) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setDeleteFlag(false);
        if (Objects.nonNull(clusterId) && clusterId > 0) {
            cloudUserMachineDo.setClusterId(clusterId);
        }
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        if (userIdList.isEmpty()) {
            return new ArrayList<>();
        }
        queryWrapper.in("user_id", userIdList);
        List<CloudUserMachineDo> userMachineList = cloudUserMachineService.list(queryWrapper);

        List<McServerVmPageDetailResp> list = new ArrayList<>();
        if (!userMachineList.isEmpty()) {
            //将用户云服务器安装集群进行分组
            Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap =
                    userMachineList.stream().collect(Collectors.groupingBy(CloudUserMachineDo::getClusterId));

            if (Objects.equals(clusterUserMachineMap.size(), 1)) {
                //单集群
                List<String> uuidList =
                        userMachineList.stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
                ServerVmListReq serverVmListReq = formatSearchMcServerVmParam(uuidList);
                MCResponseData<Object> mcResponse =
                        mcHttpService.hasDataCommonMcRequest(userMachineList.get(0).getClusterId(), serverVmListReq,
                                mcConfigProperties.getServerVmList(), userName, 0);
                if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS,
                        mcResponse.getStatus())) {
                    McPageResp<McServerVmPageDetailResp> mcServerVmPage =
                            JSONObject.parseObject(JSON.toJSONString(mcResponse.getData()), new
                                    TypeReference<McPageResp<McServerVmPageDetailResp>>() {
                                    });
                    list = mcServerVmPage.getRows();
                }
            } else {
                //多集群
                list = manyClusterUserMachine(clusterUserMachineMap, userName, userMachineList.size());
            }
        }
        return list;
    }

    /**
     * 封装请求mc云服务器列表的参数
     *
     * @param uuids
     * @return
     */
    private ServerVmListReq formatSearchMcServerVmParam(List<String> uuids) {
        ServerVmListReq serverVmListReq = new ServerVmListReq();
        serverVmListReq.setPage(KylinCloudManageConstants.FIRST_PAGE);
        serverVmListReq.setRows(uuids.size());
        serverVmListReq.setUuidList(uuids);
        return serverVmListReq;
    }

    /**
     * 多集群中获取云服务器列表
     *
     * @param clusterUserMachineMap
     * @param maxServerVmSize
     * @return
     */
    private List<McServerVmPageDetailResp> manyClusterUserMachine(Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap,
                                                                  String userName, Integer maxServerVmSize) {
        List<Integer> clusterIdList = new ArrayList<>();
        clusterIdList.addAll(clusterUserMachineMap.keySet());
        List<Object> mcRequestObjectList = new ArrayList<>();
        clusterIdList.forEach(clusterId -> {
            List<String> uuidList =
                    clusterUserMachineMap.get(clusterId).stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
            ServerVmListReq serverVmListReq = formatSearchMcServerVmParam(uuidList);
            serverVmListReq.setRows(maxServerVmSize);
            mcRequestObjectList.add(serverVmListReq);
        });

        List<String> mcServerVmList = mcClusterThreadService.threadGetMcResponse(clusterIdList, userName,
                mcConfigProperties.getServerVmList(), mcRequestObjectList);

        List<McServerVmPageDetailResp> mcVmList = new ArrayList<>();
        for (int i = 0; i < clusterIdList.size(); i++) {
            String serverVmList = mcServerVmList.get(i);
            if (Objects.nonNull(serverVmList)) {
                McPageResp<McServerVmPageDetailResp> mcServerVmPage = JSONObject.parseObject(serverVmList, new
                        TypeReference<McPageResp<McServerVmPageDetailResp>>() {
                        });
                mcVmList.addAll(mcServerVmPage.getRows());
            }
        }
        return mcVmList;

    }

    @Override
    public List<CloudUserMachineDo> userVisibleUserMachineList(Integer userId) {
        //查询用户拥有的云服务器uuid
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setDeleteFlag(false);
        //是否是平台管理用户
        boolean platformUser = userService.judgeIfPlatformUser(userId);
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        if (platformUser) {
            return cloudUserMachineService.list(queryWrapper);
        }
        List<CloudUserDo> visibleUserList = userService.userVisibleUserList(userId);
        if (visibleUserList.isEmpty()) {
            return new ArrayList<>();
        }
        queryWrapper.in("user_id", visibleUserList.stream().map(CloudUserDo::getId).collect(Collectors.toList()));
        return cloudUserMachineService.list(queryWrapper);
    }
}
