package com.hnkylin.cloud.manage.service;


import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.common.servervm.McServerVmLogResp;
import com.hnkylin.cloud.core.common.servervm.ServerVmBaseParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchOperateParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmMonitorInfoRespDto;
import com.hnkylin.cloud.core.enums.McStartVmErrorCode;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmOperateLogPageParam;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmPageParam;
import com.hnkylin.cloud.manage.entity.resp.serverVm.PageServerVmRespDto;
import com.hnkylin.cloud.manage.entity.resp.serverVm.ServerVmZoneOrgTreeRespDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 云服务器管理
 */
public interface ServerVmService {

    /**
     * 根据登录用户获取可用区组织数
     *
     * @param loginUserVo
     * @return
     */
    List<ServerVmZoneOrgTreeRespDto> serverVmZoneTree(Integer clusterId, LoginUserVo loginUserVo);


    /**
     * 云服务器列表
     *
     * @param serverVmPageParam
     * @param loginUserVo
     * @return
     */
    PageData<PageServerVmRespDto> serverVmList(ServerVmPageParam serverVmPageParam, LoginUserVo loginUserVo);


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
     * 删除云服务器
     */
    BaseResult<String> deleteServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 批量删除
     */
    BaseResult<String> batchDeleteServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                           LoginUserVo loginUserVo);

    /**
     * 获取mc云服务器图片
     */
    ResponseEntity<byte[]> downLoadServerVmLog(String mcServerVmLogoPath, String mcServerVmLogoName,
                                               String serverVmUuid);


    /*
     * 获取vncUrl
     */
    VncUrlDto getVncUrl(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /**
     * 云服务器详情-基础信息
     *
     * @param serverVmBaseParam
     * @param loginUserVo
     * @return
     */
    KcpServerVmDetailResp serverVmInfo(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器详情-概要信息
     *
     * @param serverVmBaseParam
     * @param loginUserVo
     * @return
     */
    KcpServerVmSummaryResp serverVmSummary(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器详情-日志告警信息
     *
     * @param serverVmBaseParam
     * @param loginUserVo
     * @return
     */
    List<KcpServerVmAlarmEventResp> serverVmAlarmEvent(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /**
     * 云服务器详情-网络信息
     *
     * @param serverVmBaseParam
     * @param loginUserVo
     * @return
     */
    List<KcpServerVmNetworkResp> serverVmNetwork(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务器详情-磁盘信息
     *
     * @param serverVmBaseParam
     * @param loginUserVo
     * @return
     */
    List<KcpServerVmDiskResp> serverVmDisk(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);

    /**
     * 云服务区器详情-监控
     */
    ServerVmMonitorInfoRespDto serverVmMonitor(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo);


    /**
     * 云服务区器详情-操作日志
     */
    PageData<McServerVmLogResp> serverVmOperateLog(ServerVmOperateLogPageParam serverVmOperateLogPageParam,
                                                   LoginUserVo loginUserVo);


}
