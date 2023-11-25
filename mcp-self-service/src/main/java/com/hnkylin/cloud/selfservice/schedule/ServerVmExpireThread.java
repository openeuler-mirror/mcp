package com.hnkylin.cloud.selfservice.schedule;


import com.alibaba.fastjson.JSON;
import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.core.common.MCServerVmConstants;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudUserMachineDo;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import com.hnkylin.cloud.core.service.CloudUserMachineService;
import com.hnkylin.cloud.core.service.CloudUserService;
import com.hnkylin.cloud.selfservice.config.MCConfigProperties;
import com.hnkylin.cloud.core.common.servervm.ServerVmBaseReq;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchReq;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McServerVmStatusResp;
import com.hnkylin.cloud.selfservice.service.McHttpService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 云服务器到期后具体执行的线程
 */
@Slf4j
public class ServerVmExpireThread implements Runnable {


    private CloudUserMachineService cloudUserMachineService;

    private MCConfigProperties mcConfigProperties;

    private McHttpService mcHttpService;

    private CloudUserService cloudUserService;

    private CloudUserMachineDo userMachine;

    public ServerVmExpireThread(CloudUserMachineService cloudUserMachineService,
                                MCConfigProperties mcConfigProperties, McHttpService mcHttpService,
                                CloudUserService cloudUserService, CloudUserMachineDo userMachine) {
        this.cloudUserMachineService = cloudUserMachineService;
        this.mcConfigProperties = mcConfigProperties;
        this.mcHttpService = mcHttpService;
        this.cloudUserService = cloudUserService;
        this.userMachine = userMachine;
    }

    @Override
    public void run() {
        Date now = new Date();


        CloudUserDo cloudUserDo = cloudUserService.getById(userMachine.getUserId());
        //调用mc接口判断云服务器是否离线状态
        boolean isOfflineFlag = mcServerVmIsOffline(userMachine.getMachineUuid(), cloudUserDo.getUserName());

        log.info("runServerVmExpireTask,uuid:{},isOfflineFlag:{}", userMachine.getMachineUuid(), isOfflineFlag);

        //先获取云服务器状态，
        //到期策略-关机：如果是关机状态，则不用调用mc关机接口，如果不是离线状态，则进行关机操作
        //到期策略-销毁：如果是关机状态，则调用mc销毁接口，如果不是离线状态，则需要先进行关机操作，在调用销毁接口
        if (!isOfflineFlag) {
            //不是离线状态
            //调用mc接口进行关机
            boolean shutdownResult = forcedShutdownServerVm(userMachine.getMachineUuid(), cloudUserDo.getUserName());
            log.info("runServerVmExpireTask-forcedShutdown,uuid:{},shutdownResult:{}",
                    userMachine.getMachineUuid(), shutdownResult);
            if (shutdownResult) {
                //关机成功，因为MC中关机是异步任务上报，所以等2分钟后，在查一次云服务器状态，如果是离线，则说明关机成功，
                //2分钟后不是离线，则说明关机失败了，这次任务就忽略该条过期的云服务器，下次定时任务在执行
                try {
                    Thread.sleep(2 * 60 * 1000);
                    boolean offlineFlag = mcServerVmIsOffline(userMachine.getMachineUuid(), cloudUserDo.getUserName());
                    log.info("runServerVmExpireTask-wait-2min,uuid:{},isOfflineFlag:{}", userMachine.getMachineUuid(),
                            offlineFlag);
                    if (!offlineFlag) {
                        log.info("runServerVmExpireTask-forcedShutdown-error,uuid:{},shutdown error,tomorrow task run",
                                userMachine.getMachineUuid());
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                //如果关机失败，则同样这次任务就忽略该条过期的云服务器，下次定时任务在执行该云服务器
                log.info("runServerVmExpireTask-forcedShutdown-error,uuid:{},shutdown error,tomorrow task run",
                        userMachine.getMachineUuid());
                return;
            }
        }

        if (Objects.equals(userMachine.getDeadlineType(), ServerVmDeadlineType.DESTROY)) {
            //销毁策略，调用mc回收云服务器接口
            //调用mc回收云服务器接口
            boolean destroyResult = destroyServerVm(userMachine.getMachineUuid(), cloudUserDo.getUserName());
            log.info("runServerVmExpireTask-destroyResult,uuid:{},destroyResult:{}",
                    userMachine.getMachineUuid(), destroyResult);
            //如果销毁成功，则设置改用户拥有的云服务器已呗删除。(mc中放入了回收站)
            if (destroyResult) {
                userMachine.setDeleteFlag(Boolean.TRUE);
                userMachine.setDeleteTime(now);
                userMachine.setDeadlineFlag(Boolean.TRUE);
                userMachine.setUpdateTime(now);
                cloudUserMachineService.updateById(userMachine);
            }
        } else if (Objects.equals(userMachine.getDeadlineType(), ServerVmDeadlineType.POWER_OFF)) {
            userMachine.setDeadlineFlag(Boolean.TRUE);
            userMachine.setUpdateTime(now);
            cloudUserMachineService.updateById(userMachine);
        }

    }


    /**
     * 判断mc中云服务器接口是否是关机状态
     */
    public boolean mcServerVmIsOffline(String machineUuid, String userName) {
        McServerVmStatusResp mcStatus = new McServerVmStatusResp();
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(machineUuid);

        CloudUserMachineDo userMachineDo = cloudUserMachineService.getUserMachineDoByUuid(machineUuid);

        String httpUrl = mcConfigProperties.getServerVmStatusUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, httpUrl, userName, 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            mcStatus = JSON.parseObject(JSON.toJSONString(mcResponse.getData()), McServerVmStatusResp.class);

        }

        return Objects.equals(McServerVmStatus.OFFLINE, mcStatus.getStatus());
    }

    /**
     * 调用mc接口进行关机
     */
    private boolean forcedShutdownServerVm(String machineUuid, String userName) {
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(machineUuid);

        CloudUserMachineDo userMachineDo = cloudUserMachineService.getUserMachineDoByUuid(machineUuid);

        return mcHttpService.noDataCommonMcRequest(userMachineDo.getClusterId(), serverVmBaseReq,
                mcConfigProperties.getForcedShutdownServerVmUrl(), userName, 0);
    }

    /**
     * 销毁-调用mc接口将云服务器放入回收站中
     */
    private boolean destroyServerVm(String machineUuid, String userName) {
        List<String> serverVmUuids = new ArrayList();
        serverVmUuids.add(machineUuid);


        ServerVmBatchReq serverVmBatchReq = new ServerVmBatchReq();
        serverVmBatchReq.setUuid(String.join(",", serverVmUuids));

        CloudUserMachineDo userMachineDo = cloudUserMachineService.getUserMachineDoByUuid(machineUuid);

        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBatchReq, mcConfigProperties.getBatchRemoveMachineToRecycleUrl(), userName, 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            return true;
        }
        return false;
    }


}
