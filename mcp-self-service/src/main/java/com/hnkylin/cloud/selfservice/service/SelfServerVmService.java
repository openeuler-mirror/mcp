package com.hnkylin.cloud.selfservice.service;

import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.common.servervm.ServerVmBaseParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchOperateParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmMonitorInfoRespDto;
import com.hnkylin.cloud.core.enums.McStartVmErrorCode;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.mc.req.*;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McOperateSystemResp;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McServerVmInfoResp;
import com.hnkylin.cloud.core.common.servervm.McServerVmLogResp;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McServerVmSnapshotResp;
import com.hnkylin.cloud.selfservice.entity.req.*;
import com.hnkylin.cloud.selfservice.entity.resp.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SelfServerVmService {

    /**
     * 获取模板列表
     */
    PageData<ServerVmTemplateRespDto> listServerVmTemplate(SearchTemplateParam searchTemplateParam,
                                                           LoginUserVo loginUserVo);


    /**
     * 获取mc-iso列表
     *
     * @param loginUserVo
     */
    List<IsoRespDto> isoList(LoginUserVo loginUserVo);

    /**
     * 申请云服务器
     */
    void applyServerVm(ApplyServerVmParam applyServerVmParam, LoginUserVo loginUserVo);


    /**
     * 变更云服务器
     */
    void modifyServerVm(ModifyServerVmParam modifyServerVmParam, LoginUserVo loginUserVo);


    /**
     * 变更云服务器
     */
    ServerVmDetailRespDto getServerVmDetail(ServerVmBaseParam serverVmPageParam, LoginUserVo loginUserVo);

    /**
     * 根据工单ID获取申请服务器工单详情
     */
    ApplyServerVmDetailRespDto getApplyServerVmDetailByWorkOrderId(Integer workOrderId, LoginUserVo loginUserVo);

    /**
     * 根据工单ID获取延期云服务器详情
     */
    ApplyDeferredDetailRespDto applyDeferredDetailByWorkOrderId(Integer workOrderId);


    /**
     * 根据工单ID获取获取变更云服务器详情
     */
    ModifyServerVmDetailRespDto modifyServerVmDetailByWorkOrderId(Integer workOrderId);


    /**
     * 申请延期云服务器
     */
    void applyDeferredMachine(ApplyDeferredParam applyDeferredParam, LoginUserVo loginUserVo);

    /**
     * 查询用户云服务器
     */
    PageData<PageServerVmRespDto> listServerVm(ServerVmPageParam serverVmPageParam, LoginUserVo loginUserVo);


    /**
     * 删除云服务器
     */
    BaseResult<String> deleteServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器开机
     */
    McStartVmErrorCode startServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /**
     * 云服务器关机
     */
    boolean shutdownServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /**
     * 云服务器强制关机
     */
    boolean forcedShutdownServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器重启
     */
    boolean restartServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器强制重启
     */
    boolean forcedRestartServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器批量开机
     */
    BaseResult<String> batchStartServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam, LoginUserVo loginUserVo);

    /**
     * 批量删除
     */
    BaseResult<String> batchDeleteServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                           LoginUserVo loginUserVo);


    /**
     * 云服务器批量关机
     */
    BaseResult<String> batchShutdownServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                             LoginUserVo loginUserVo);

    /**
     * 云服务器批量重启
     */
    BaseResult<String> batchRebootServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                           LoginUserVo loginUserVo);


    /**
     * 云服务区器详情-概要
     */
    McServerVmInfoResp serverVmInfo(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务区器详情-监控
     */
    ServerVmMonitorInfoRespDto serverVmMonitor(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /**
     * 云服务区器详情-操作日志
     */
    PageData<McServerVmLogResp> serverVmOperateLog(ServerVmOperateLogPageParam serverVmOperateLogPageParam,
                                                   LoginUserVo loginUserVo);

    /*
     * 云服务区器详情-创建快照
     */
    BaseResult<String> createSnapshot(McServerVmCreateSnapshotReq mcServerVmCreateSnapshotReq, LoginUserVo loginUserVo);

    /*
     * 云服务区器详情-编辑快照
     */
    BaseResult<String> updateSnapshot(McServerVmUpdateSnapshotReq mcServerVmUpdateSnapshotReq, LoginUserVo loginUserVo);

    /*
     * 云服务区器详情-删除快照
     */
    boolean deleteSnapshot(McServerVmDeleteSnapshotReq mcServerVmDeleteSnapshotReq, LoginUserVo loginUserVo);

    /*
     * 云服务区器详情-应用快照
     */
    BaseResult<String> applySnapshot(McServerVmApplySnapshotReq mcServerVmApplySnapshotReq, LoginUserVo loginUserVo);


    /*
     * 云服务区器详情-快照列表
     */
    List<McServerVmSnapshotResp> listSnapshot(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /*
     * 获取vncUrl
     */
    VncUrlDto getVncUrl(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /*
     * 修改云服务器描述
     */
    boolean updateMachineDescription(UpdateServerVmDescriptionParam updateServerVmDescriptionParam,
                                     LoginUserVo loginUserVo);

    /*
     * 修改云服务器名称
     */
    boolean updateMachineName(UpdateServerVmNameParam updateServerVmNameParam,
                              LoginUserVo loginUserVo);


    /**
     * 重置控制台密码
     *
     * @param remotePasswordReq
     * @param loginUserVo
     * @return
     */
    boolean resetRemotePassword(McServerVmResetRemotePasswordReq remotePasswordReq, LoginUserVo loginUserVo);


    /**
     * 获取mc云服务器图片
     */
    ResponseEntity<byte[]> downLoadServerVmLog(String mcServerVmLogoPath, String mcServerVmLogoName);


    /**
     * 获取所有操作系统列表
     *
     * @param queryOperateSystem
     * @param loginUserVo
     * @return
     */
    List<McOperateSystemResp> getAllOperatingSystem(QueryOperateSystem queryOperateSystem, LoginUserVo loginUserVo);


}
