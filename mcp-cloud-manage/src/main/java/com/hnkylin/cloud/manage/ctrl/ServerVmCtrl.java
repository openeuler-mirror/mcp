package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.common.servervm.McServerVmLogResp;
import com.hnkylin.cloud.core.common.servervm.ServerVmBaseParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchOperateParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmMonitorInfoRespDto;
import com.hnkylin.cloud.core.enums.McStartVmErrorCode;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseServerVmConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.servervm.QueryTransferOrgParams;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmOperateLogPageParam;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmPageParam;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmTransferParam;
import com.hnkylin.cloud.manage.entity.resp.org.ParentOrgRespDto;
import com.hnkylin.cloud.manage.entity.resp.serverVm.PageServerVmRespDto;
import com.hnkylin.cloud.manage.entity.resp.serverVm.ServerVmZoneOrgTreeRespDto;
import com.hnkylin.cloud.manage.service.ServerVmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/serverVm")
public class ServerVmCtrl {


    @Resource
    private ServerVmService serverVmService;


    @PostMapping("/zoneOrgTree")
    public BaseResult<List<ServerVmZoneOrgTreeRespDto>> serverVmZoneTree(@ModelCheck(notNull = true) @RequestBody BaseClusterParam baseClusterParam
            , @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(serverVmService.serverVmZoneTree(baseClusterParam.getClusterId(), loginUserVo));

    }


    @PostMapping("/serverVmList")
    @ParamCheck
    public BaseResult<PageData<PageServerVmRespDto>> serverVmList(@ModelCheck(notNull = true) @RequestBody ServerVmPageParam serverVmPageParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(serverVmService.serverVmList(serverVmPageParam, loginUserVo));

    }

    @PostMapping("/startServerVm")
    @ParamCheck
    public BaseResult<String> startServerVm(@ModelCheck(notNull = true) @RequestBody
                                                    ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                    loginUserVo) {

        McStartVmErrorCode errorCode = serverVmService.startServerVm(serverVmBaseParam, loginUserVo);
        if (Objects.equals(errorCode, McStartVmErrorCode.SUCCESS)) {
            return BaseResult.success(null);
        }
        return BaseResult.error(errorCode.getErrorMsg());

    }


    @PostMapping("/shutdownServerVm")
    @ParamCheck
    public BaseResult<String> shutdownServerVm(@ModelCheck(notNull = true) @RequestBody
                                                       ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                       loginUserVo) {
        boolean result = serverVmService.shutdownServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseServerVmConstants.SHUTDOWN_VM_ERR);

    }

    @PostMapping("/forcedShutdownServerVm")
    @ParamCheck
    public BaseResult<String> forcedShutdownServerVm(@ModelCheck(notNull = true) @RequestBody
                                                             ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                             LoginUserVo loginUserVo) {
        boolean result = serverVmService.forcedShutdownServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseServerVmConstants.FORCED_SHUTDOWN_VM_ERR);

    }


    @PostMapping("/restartServerVm")
    @ParamCheck
    public BaseResult<String> restartServerVm(@ModelCheck(notNull = true) @RequestBody
                                                      ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                      loginUserVo) {
        boolean result = serverVmService.restartServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseServerVmConstants.RESTART_VM_ERR);

    }

    @PostMapping("/forcedRestartServerVm")
    @ParamCheck
    public BaseResult<String> forcedRestartServerVm(@ModelCheck(notNull = true) @RequestBody
                                                            ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                            LoginUserVo loginUserVo) {
        boolean result = serverVmService.forcedRestartServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseServerVmConstants.FORCED_RESTART_VM_ERR);

    }

    @PostMapping("/batchStartServerVm")
    @ParamCheck
    public BaseResult<String> batchStartServerVm(@ModelCheck(notNull = true) @RequestBody
                                                         ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                 @LoginUser LoginUserVo loginUserVo) {

        return serverVmService.batchStartServerVm(serverVmBatchOperateParam, loginUserVo);


    }

    @PostMapping("/batchShutdownServerVm")
    @ParamCheck
    public BaseResult<String> batchShutdownServerVm(@ModelCheck(notNull = true) @RequestBody
                                                            ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                    @LoginUser LoginUserVo loginUserVo) {
        return serverVmService.batchShutdownServerVm(serverVmBatchOperateParam, loginUserVo);

    }

    @PostMapping("/batchRebootServerVm")
    @ParamCheck
    public BaseResult<String> batchRebootServerVm(@ModelCheck(notNull = true) @RequestBody
                                                          ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  @LoginUser LoginUserVo loginUserVo) {
        return serverVmService.batchRebootServerVm(serverVmBatchOperateParam, loginUserVo);
    }

    @PostMapping("/deleteServerVm")
    @ParamCheck
    public BaseResult<String> deleteServerVm(@ModelCheck(notNull = true) @RequestBody
                                                     ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                     loginUserVo) {

        return serverVmService.deleteServerVm(serverVmBaseParam, loginUserVo);
    }

    @PostMapping("/batchDeleteServerVm")
    @ParamCheck
    public BaseResult<String> batchDeleteServerVm(@ModelCheck(notNull = true) @RequestBody
                                                          ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  @LoginUser LoginUserVo loginUserVo) {

        return serverVmService.batchDeleteServerVm(serverVmBatchOperateParam, loginUserVo);
    }

    @GetMapping("/downLoadServerVmLog")
    public BaseResult<ResponseEntity<byte[]>> downLoadServerVmLog(@RequestParam(name = "mcServerVmLogoPath") String mcServerVmLogoPath,
                                                                  @RequestParam(name = "mcServerVmLogoName") String mcServerVmLogoName,
                                                                  @RequestParam(name = "serverVmUuid") String serverVmUuid) {

        return BaseResult.success(serverVmService.downLoadServerVmLog(mcServerVmLogoPath, mcServerVmLogoName,
                serverVmUuid));

    }

    @PostMapping("/getVncUrl")
    @ParamCheck
    public BaseResult<VncUrlDto> getVncUrl(@ModelCheck(notNull = true) @RequestBody
                                                   ServerVmBaseParam serverVmBaseParam,
                                           @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.getVncUrl(serverVmBaseParam, loginUserVo));
    }

    @PostMapping("/serverVmInfo")
    @ParamCheck
    public BaseResult<KcpServerVmDetailResp> serverVmInfo(@ModelCheck(notNull = true) @RequestBody
                                                                  ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                                  LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.serverVmInfo(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmSummary")
    @ParamCheck
    public BaseResult<KcpServerVmSummaryResp> serverVmSummary(@ModelCheck(notNull = true) @RequestBody
                                                                      ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                                      LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.serverVmSummary(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmAlarmEvent")
    @ParamCheck
    public BaseResult<List<KcpServerVmAlarmEventResp>> serverVmAlarmEvent(@ModelCheck(notNull = true) @RequestBody
                                                                                  ServerVmBaseParam serverVmBaseParam
            , @LoginUser
                                                                                  LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.serverVmAlarmEvent(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmNetwork")
    @ParamCheck
    public BaseResult<List<KcpServerVmNetworkResp>> serverVmNetwork(@ModelCheck(notNull = true) @RequestBody
                                                                            ServerVmBaseParam serverVmBaseParam,
                                                                    @LoginUser
                                                                            LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.serverVmNetwork(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmDisk")
    @ParamCheck
    public BaseResult<List<KcpServerVmDiskResp>> serverVmDisk(@ModelCheck(notNull = true) @RequestBody
                                                                      ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                                      LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.serverVmDisk(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmMonitor")
    @ParamCheck
    public BaseResult<ServerVmMonitorInfoRespDto> serverVmMonitor(@ModelCheck(notNull = true) @RequestBody
                                                                          ServerVmBaseParam serverVmBaseParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(serverVmService.serverVmMonitor(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmOperateLog")
    @ParamCheck
    public BaseResult<PageData<McServerVmLogResp>> serverVmOperateLog(@ModelCheck(notNull = true) @RequestBody
                                                                              ServerVmOperateLogPageParam
                                                                              serverVmOperateLogPageParam,
                                                                      @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(serverVmService.serverVmOperateLog(serverVmOperateLogPageParam, loginUserVo));

    }

    @PostMapping("/transferCanSelectOrg")
    public BaseResult<List<ParentOrgRespDto>> transferCanSelectOrg(@RequestBody QueryTransferOrgParams queryTransferOrgParams,
                                                                   @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(serverVmService.transferCanSelectOrg(queryTransferOrgParams, loginUserVo));

    }

    @PostMapping("/serverVmTransfer")
    @ParamCheck
    public BaseResult<String> serverVmTransfer(@ModelCheck(notNull = true) @RequestBody
                                                           ServerVmTransferParam serverVmTransferParam,
                                               @LoginUser LoginUserVo loginUserVo) {
        serverVmService.serverVmTransfer(serverVmTransferParam, loginUserVo);
        return BaseResult.success(null);

    }


}
