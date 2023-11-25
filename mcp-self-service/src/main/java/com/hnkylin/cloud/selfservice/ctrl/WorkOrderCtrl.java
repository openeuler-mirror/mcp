package com.hnkylin.cloud.selfservice.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.WorkOrderDetailParam;
import com.hnkylin.cloud.selfservice.entity.req.WorkOrderPageParam;
import com.hnkylin.cloud.selfservice.entity.resp.*;
import com.hnkylin.cloud.selfservice.service.SelfServerVmService;
import com.hnkylin.cloud.selfservice.service.SelfServiceUserService;
import com.hnkylin.cloud.selfservice.service.SelfWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/workOrder")
@Slf4j
public class WorkOrderCtrl {


    @Resource
    private SelfWorkOrderService selfWorkOrderService;

    @Resource
    private SelfServerVmService selfServerVmService;

    @Resource
    private SelfServiceUserService selfServiceUserService;

    @PostMapping("/pageWorkOrder")
    @ParamCheck
    public BaseResult<PageData<PageWorkOrderRespDto>> pageWorkOrder(@ModelCheck(notNull = true) @RequestBody
                                                                            WorkOrderPageParam
                                                                            workOrderPageParam, @LoginUser
                                                                            LoginUserVo loginUserVo) {

        return BaseResult.success(selfWorkOrderService.pageWorkOrder(workOrderPageParam, loginUserVo));

    }


    /**
     * 注册账号详情
     */
    @PostMapping("/registerUserDetail")
    @ParamCheck
    public BaseResult<WorkOrderUserDetailRespDto> registerUserDetail(@ModelCheck(notNull = true) @RequestBody
                                                                             WorkOrderDetailParam
                                                                             workOrderDetailParam) {

        return BaseResult.success(selfServiceUserService.getWorkOrderUserDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId()));

    }

    /**
     * 修改账号详情
     */
    @PostMapping("/updateUserDetail")
    @ParamCheck
    public BaseResult<WorkOrderUserDetailRespDto> updateUserDetail(@ModelCheck(notNull = true) @RequestBody
                                                                           WorkOrderDetailParam
                                                                           workOrderDetailParam) {

        return BaseResult.success(selfServiceUserService.getWorkOrderUserDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId()));

    }


    /**
     * 申请虚拟机详情
     */
    @PostMapping("/applyServerVmDetail")
    @ParamCheck
    public BaseResult<ApplyServerVmDetailRespDto> getApplyServerVmDetail(@ModelCheck(notNull = true) @RequestBody
                                                                                 WorkOrderDetailParam
                                                                                 workOrderDetailParam, @LoginUser
                                                                                 LoginUserVo loginUserVo) {

        return BaseResult.success(selfServerVmService.getApplyServerVmDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId(), loginUserVo));

    }

    /**
     * 变更云服务器详情
     */
    @PostMapping("/modifyServerVmDetail")
    @ParamCheck
    public BaseResult<ModifyServerVmDetailRespDto> modifyServerVmDetail(@ModelCheck(notNull = true) @RequestBody
                                                                                WorkOrderDetailParam
                                                                                workOrderDetailParam) {

        return BaseResult.success(selfServerVmService.modifyServerVmDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId()));

    }

    /**
     * 申请延期云服务器详情
     */
    @PostMapping("/applyDeferredDetail")
    @ParamCheck
    public BaseResult<ApplyDeferredDetailRespDto> applyDeferredDetail(@ModelCheck(notNull = true) @RequestBody
                                                                              WorkOrderDetailParam
                                                                              workOrderDetailParam) {

        return BaseResult.success(selfServerVmService.applyDeferredDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId()));

    }


}
