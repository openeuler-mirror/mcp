package com.hnkylin.cloud.selfservice.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.common.servervm.ServerVmBaseParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchOperateParam;
import com.hnkylin.cloud.core.common.servervm.ServerVmMonitorInfoRespDto;
import com.hnkylin.cloud.selfservice.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.core.enums.McStartVmErrorCode;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.mc.req.*;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McOperateSystemResp;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McServerVmInfoResp;
import com.hnkylin.cloud.core.common.servervm.McServerVmLogResp;
import com.hnkylin.cloud.selfservice.entity.mc.resp.McServerVmSnapshotResp;
import com.hnkylin.cloud.selfservice.entity.req.*;
import com.hnkylin.cloud.selfservice.entity.resp.*;
import com.hnkylin.cloud.selfservice.service.SelfServerVmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/serverVm")
@Slf4j
public class ServerVmCtrl {


    @Resource
    private SelfServerVmService selfServerVmService;

    @PostMapping("/listServerVmTemplate")
    public BaseResult<PageData<ServerVmTemplateRespDto>> listServerVmTemplate(@RequestBody SearchTemplateParam searchTemplateParam,
                                                                              @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.listServerVmTemplate(searchTemplateParam, loginUserVo));

    }

    @PostMapping("/isoList")
    public BaseResult<List<IsoRespDto>> isoList(@LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.isoList(loginUserVo));
    }

    @PostMapping("/applyServerVm")
    @ParamCheck
    public BaseResult<String> applyServerVm(@ModelCheck(notNull = true) @RequestBody
                                                    ApplyServerVmParam applyServerVmParam, @LoginUser LoginUserVo
                                                    loginUserVo) {
        selfServerVmService.applyServerVm(applyServerVmParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/modifyServerVm")
    @ParamCheck
    public BaseResult<String> modifyServerVm(@ModelCheck(notNull = true) @RequestBody
                                                     ModifyServerVmParam modifyServerVmParam, @LoginUser LoginUserVo
                                                     loginUserVo) {
        selfServerVmService.modifyServerVm(modifyServerVmParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/applyDeferredMachine")
    @ParamCheck
    public BaseResult<String> applyDeferredMachine(@ModelCheck(notNull = true) @RequestBody
                                                           ApplyDeferredParam applyDeferredParam, @LoginUser
                                                           LoginUserVo loginUserVo) {
        selfServerVmService.applyDeferredMachine(applyDeferredParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/getServerVmDetail")
    public BaseResult<ServerVmDetailRespDto> getServerVmDetail(@ModelCheck(notNull = true) @RequestBody
                                                                       ServerVmBaseParam serverVmPageParam,
                                                               @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.getServerVmDetail(serverVmPageParam, loginUserVo));

    }

    @PostMapping("/listServerVm")
    @ParamCheck
    public BaseResult<PageData<PageServerVmRespDto>> listServerVm(@ModelCheck(notNull = true) @RequestBody
                                                                          ServerVmPageParam serverVmPageParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(selfServerVmService.listServerVm(serverVmPageParam, loginUserVo));

    }


    @PostMapping("/deleteServerVm")
    @ParamCheck
    public BaseResult<String> deleteServerVm(@ModelCheck(notNull = true) @RequestBody
                                                     ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                     loginUserVo) {

        return selfServerVmService.deleteServerVm(serverVmBaseParam, loginUserVo);
    }

    @PostMapping("/startServerVm")
    @ParamCheck
    public BaseResult<String> startServerVm(@ModelCheck(notNull = true) @RequestBody
                                                    ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                    loginUserVo) {

        McStartVmErrorCode errorCode = selfServerVmService.startServerVm(serverVmBaseParam, loginUserVo);
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
        boolean result = selfServerVmService.shutdownServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.SHUTDOWN_VM_ERR);

    }

    @PostMapping("/forcedShutdownServerVm")
    @ParamCheck
    public BaseResult<String> forcedShutdownServerVm(@ModelCheck(notNull = true) @RequestBody
                                                             ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                             LoginUserVo loginUserVo) {
        boolean result = selfServerVmService.forcedShutdownServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.FORCED_SHUTDOWN_VM_ERR);

    }


    @PostMapping("/restartServerVm")
    @ParamCheck
    public BaseResult<String> restartServerVm(@ModelCheck(notNull = true) @RequestBody
                                                      ServerVmBaseParam serverVmBaseParam, @LoginUser LoginUserVo
                                                      loginUserVo) {
        boolean result = selfServerVmService.restartServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.RESTART_VM_ERR);

    }

    @PostMapping("/forcedRestartServerVm")
    @ParamCheck
    public BaseResult<String> forcedRestartServerVm(@ModelCheck(notNull = true) @RequestBody
                                                            ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                            LoginUserVo loginUserVo) {
        boolean result = selfServerVmService.forcedRestartServerVm(serverVmBaseParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.FORCED_RESTART_VM_ERR);

    }

    @PostMapping("/batchStartServerVm")
    @ParamCheck
    public BaseResult<String> batchStartServerVm(@ModelCheck(notNull = true) @RequestBody
                                                         ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                 @LoginUser LoginUserVo loginUserVo) {

        return selfServerVmService.batchStartServerVm(serverVmBatchOperateParam, loginUserVo);


    }

    @PostMapping("/batchShutdownServerVm")
    @ParamCheck
    public BaseResult<String> batchShutdownServerVm(@ModelCheck(notNull = true) @RequestBody
                                                            ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                    @LoginUser LoginUserVo loginUserVo) {
        return selfServerVmService.batchShutdownServerVm(serverVmBatchOperateParam, loginUserVo);

    }

    @PostMapping("/batchRebootServerVm")
    @ParamCheck
    public BaseResult<String> batchRebootServerVm(@ModelCheck(notNull = true) @RequestBody
                                                          ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  @LoginUser LoginUserVo loginUserVo) {
        return selfServerVmService.batchRebootServerVm(serverVmBatchOperateParam, loginUserVo);


    }

    @PostMapping("/batchDeleteServerVm")
    @ParamCheck
    public BaseResult<String> batchDeleteServerVm(@ModelCheck(notNull = true) @RequestBody
                                                          ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  @LoginUser LoginUserVo loginUserVo) {

        return selfServerVmService.batchDeleteServerVm(serverVmBatchOperateParam, loginUserVo);
    }


    @PostMapping("/serverVmInfo")
    @ParamCheck
    public BaseResult<McServerVmInfoResp> serverVmInfo(@ModelCheck(notNull = true) @RequestBody
                                                               ServerVmBaseParam serverVmBaseParam, @LoginUser
                                                               LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.serverVmInfo(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmMonitor")
    @ParamCheck
    public BaseResult<ServerVmMonitorInfoRespDto> serverVmMonitor(@ModelCheck(notNull = true) @RequestBody
                                                                          ServerVmBaseParam serverVmBaseParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.serverVmMonitor(serverVmBaseParam, loginUserVo));

    }

    @PostMapping("/serverVmOperateLog")
    @ParamCheck
    public BaseResult<PageData<McServerVmLogResp>> serverVmOperateLog(@ModelCheck(notNull = true) @RequestBody
                                                                              ServerVmOperateLogPageParam
                                                                              serverVmOperateLogPageParam,
                                                                      @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.serverVmOperateLog(serverVmOperateLogPageParam, loginUserVo));

    }

    @PostMapping("/createSnapshot")
    @ParamCheck
    public BaseResult<String> createSnapshot(@ModelCheck(notNull = true) @RequestBody
                                                     McServerVmCreateSnapshotReq
                                                     mcServerVmCreateSnapshotReq,
                                             @LoginUser LoginUserVo loginUserVo) {

        return selfServerVmService.createSnapshot(mcServerVmCreateSnapshotReq, loginUserVo);

    }

    @PostMapping("/updateSnapshot")
    @ParamCheck
    public BaseResult<String> updateSnapshot(@ModelCheck(notNull = true) @RequestBody
                                                     McServerVmUpdateSnapshotReq
                                                     mcServerVmUpdateSnapshotReq,
                                             @LoginUser LoginUserVo loginUserVo) {


        return selfServerVmService.updateSnapshot(mcServerVmUpdateSnapshotReq, loginUserVo);
    }

    @PostMapping("/deleteSnapshot")
    @ParamCheck
    public BaseResult<String> deleteSnapshot(@ModelCheck(notNull = true) @RequestBody
                                                     McServerVmDeleteSnapshotReq
                                                     mcServerVmDeleteSnapshotReq,
                                             @LoginUser LoginUserVo loginUserVo) {

        boolean result = selfServerVmService.deleteSnapshot(mcServerVmDeleteSnapshotReq, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);

    }

    @PostMapping("/applySnapshot")
    @ParamCheck
    public BaseResult<String> applySnapshot(@ModelCheck(notNull = true) @RequestBody
                                                    McServerVmApplySnapshotReq
                                                    mcServerVmApplySnapshotReq,
                                            @LoginUser LoginUserVo loginUserVo) {

        return selfServerVmService.applySnapshot(mcServerVmApplySnapshotReq, loginUserVo);

    }

    @PostMapping("/listSnapshot")
    @ParamCheck
    public BaseResult<List<McServerVmSnapshotResp>> listSnapshot(@ModelCheck(notNull = true) @RequestBody
                                                                         ServerVmBaseParam serverVmBaseParam,
                                                                 @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.listSnapshot(serverVmBaseParam, loginUserVo));
    }

    @PostMapping("/getVncUrl")
    @ParamCheck
    public BaseResult<VncUrlDto> getVncUrl(@ModelCheck(notNull = true) @RequestBody
                                                   ServerVmBaseParam serverVmBaseParam,
                                           @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.getVncUrl(serverVmBaseParam, loginUserVo));
    }

    @PostMapping("/updateMachineDescription")
    @ParamCheck
    public BaseResult<String> updateMachineDescription(@ModelCheck(notNull = true) @RequestBody
                                                               UpdateServerVmDescriptionParam updateServerVmDescriptionParam,
                                                       @LoginUser LoginUserVo loginUserVo) {

        boolean result = selfServerVmService.updateMachineDescription(updateServerVmDescriptionParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);

    }

    @PostMapping("/updateMachineName")
    @ParamCheck
    public BaseResult<VncUrlDto> updateMachineName(@ModelCheck(notNull = true) @RequestBody
                                                           UpdateServerVmNameParam updateServerVmNameParam,
                                                   @LoginUser LoginUserVo loginUserVo) {

        boolean result = selfServerVmService.updateMachineName(updateServerVmNameParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);

    }

    @PostMapping("/resetRemotePassword")
    @ParamCheck
    public BaseResult<String> resetRemotePassword(@ModelCheck(notNull = true) @RequestBody
                                                          McServerVmResetRemotePasswordReq remotePasswordReq,
                                                  @LoginUser LoginUserVo loginUserVo) {

        boolean result = selfServerVmService.resetRemotePassword(remotePasswordReq, loginUserVo);
        if (result) {
            return BaseResult.success(null);

        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);
    }

    @GetMapping("/downLoadServerVmLog")
    public BaseResult<ResponseEntity<byte[]>> downLoadServerVmLog(@RequestParam(name = "mcServerVmLogoPath") String mcServerVmLogoPath,
                                                                  @RequestParam(name = "mcServerVmLogoName") String mcServerVmLogoName) {

        return BaseResult.success(selfServerVmService.downLoadServerVmLog(mcServerVmLogoPath, mcServerVmLogoName));

    }

    @PostMapping("/getAllOperatingSystem")
    @ParamCheck
    public BaseResult<List<McOperateSystemResp>> getAllOperatingSystem(@ModelCheck(notNull = true) @RequestBody QueryOperateSystem queryOperateSystem, @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.getAllOperatingSystem(queryOperateSystem, loginUserVo));
    }


}
