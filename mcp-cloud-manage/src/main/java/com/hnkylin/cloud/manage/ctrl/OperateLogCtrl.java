package com.hnkylin.cloud.manage.ctrl;


import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.core.entity.req.operateLog.BasePageOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.ParentOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.QueryOperateLogActionParam;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogActionResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogStatusResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogTypeResp;
import com.hnkylin.cloud.core.service.CloudOperateLogService;
import com.hnkylin.cloud.manage.entity.req.operateLog.OperateLogDetailParam;
import com.hnkylin.cloud.manage.entity.req.operateLog.PageOperateLogParam;
import com.hnkylin.cloud.manage.entity.resp.operateLog.OperateDetailResp;
import com.hnkylin.cloud.manage.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api/operateLog")
@Slf4j
public class OperateLogCtrl {

    @Resource
    private CloudOperateLogService cloudOperateLogService;

    @Resource
    private OperateLogService operateLogService;

    @PostMapping("/listOperateLogByObjId")
    @ParamCheck
    public BaseResult<PageData<OperateLogResp>> listOperateLogByObjId(@RequestBody BasePageOperateLogParam basePageOperateLogParam,
                                                                      @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(operateLogService.listOperateLogByObjId(basePageOperateLogParam,
                loginUserVo.getUserId()));
    }


    @PostMapping("/listChildOperateLogList")
    @ParamCheck
    public BaseResult<List<OperateLogResp>> listChildOperateLogList(@RequestBody ParentOperateLogParam parentOperateLogParam) {

        return BaseResult.success(cloudOperateLogService.listChildOperateLogList(parentOperateLogParam));
    }

    @PostMapping("/getOperateLogType")
    public BaseResult<List<OperateLogTypeResp>> getOperateLogType() {

        return BaseResult.success(operateLogService.getOperateLogType());
    }


    @PostMapping("/getOperateLogAction")
    public BaseResult<List<OperateLogActionResp>> getOperateLogAction(@RequestBody QueryOperateLogActionParam queryOperateLogActionParam) {

        return BaseResult.success(operateLogService.getOperateLogAction(queryOperateLogActionParam));
    }


    @PostMapping("/getOperateLogStatus")
    public BaseResult<List<OperateLogStatusResp>> getOperateLogStatus() {

        return BaseResult.success(operateLogService.getOperateLogStatus());
    }

    @PostMapping("/pageOperateLog")
    @ParamCheck
    public BaseResult<PageData<OperateDetailResp>> pageOperateLog(@ModelCheck(notNull = true) @RequestBody
                                                                          PageOperateLogParam pagOperateLogParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(operateLogService.pageOperateLog(pagOperateLogParam, loginUserVo));

    }

    @PostMapping("/childOperateLogList")
    @ParamCheck
    public BaseResult<List<OperateDetailResp>> childOperateLogList(@RequestBody ParentOperateLogParam parentOperateLogParam) {

        return BaseResult.success(operateLogService.childOperateLogList(parentOperateLogParam));
    }


    @PostMapping("/operateLogDetail")
    @ParamCheck
    public BaseResult<OperateDetailResp> operateLogDetail(@RequestBody OperateLogDetailParam operateLogDetailParam) {

        return BaseResult.success(operateLogService.operateLogDetail(operateLogDetailParam));
    }

}
