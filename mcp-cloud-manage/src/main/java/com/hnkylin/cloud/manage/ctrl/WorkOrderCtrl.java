package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.workorder.*;
import com.hnkylin.cloud.manage.entity.resp.workorder.*;
import com.hnkylin.cloud.manage.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@RestController
@RequestMapping("/api/workOrder")
@Slf4j
public class WorkOrderCtrl {


    @Resource
    private WorkOrderService workOrderService;


    @PostMapping("/pageWorkOrder")
    @ParamCheck
    public BaseResult<PageData<PageWorkOrderRespDto>> pageWorkOrder(@ModelCheck(notNull = true) @RequestBody
                                                                            WorkOrderPageParam workOrderPageParam,
                                                                    @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(workOrderService.pageWorkOrder(workOrderPageParam, loginUserVo));

    }


    /**
     * 批量审核
     *
     * @param batchCheckParam
     * @return
     */
    @ParamCheck
    @PostMapping("/batchCheck")
    public BaseResult<String> batchCheck(@ModelCheck(notNull = true) @RequestBody BatchCheckParam batchCheckParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        workOrderService.batchCheck(batchCheckParam, loginUserVo);
        return BaseResult.success(null);
    }


    /**
     * 注册审核
     *
     * @param commonCheckParam 用户参数
     * @return
     */
    @ParamCheck
    @PostMapping("/checkPassRegister")
    public BaseResult<String> checkPassRegister(@ModelCheck(notNull = true) @RequestBody CommonCheckParam commonCheckParam,
                                                @LoginUser LoginUserVo loginUserVo) {
        workOrderService.checkPassRegister(commonCheckParam, loginUserVo);
        return BaseResult.success(null);
    }

    /**
     * 修改帐号审核
     *
     * @param commonCheckParam 用户参数
     * @return
     */
    @ParamCheck
    @PostMapping("/checkPassUpdateUser")
    public BaseResult<String> checkPassUpdateUser(@ModelCheck(notNull = true) @RequestBody CommonCheckParam
                                                          commonCheckParam, @LoginUser LoginUserVo loginUserVo) {
        workOrderService.checkPassUpdateUser(commonCheckParam, loginUserVo);
        return BaseResult.success(null);
    }

    /**
     * 延期云服务器审核
     *
     * @param commonCheckParam 用户参数
     * @return
     */
    @ParamCheck
    @PostMapping("/checkPassDeferred")
    public BaseResult<String> checkPassDeferred(@ModelCheck(notNull = true) @RequestBody CommonCheckParam
                                                        commonCheckParam, @LoginUser LoginUserVo loginUserVo) {
        workOrderService.checkPassDeferred(commonCheckParam, loginUserVo);
        return BaseResult.success(null);
    }

    /**
     * 拒绝工单
     */
    @PostMapping("/refuseWorkOrder")
    @ParamCheck
    public BaseResult<String> refuseWorkOrder(@ModelCheck(notNull = true) @RequestBody CommonCheckParam
                                                      commonCheckParam, @LoginUser LoginUserVo loginUserVo) {
        workOrderService.refuseWorkOrder(commonCheckParam, loginUserVo);
        return BaseResult.success(null);

    }

    /**
     * 申请虚拟机 通过
     */
    @PostMapping("/passApplyServerVm")
    @ParamCheck
    public BaseResult<String> passApplyServerVm(@ModelCheck(notNull = true) @RequestBody PassApplyServerVmParam
                                                        passApplyServerVmParam, @LoginUser LoginUserVo
                                                        loginUserVo) {
        workOrderService.passApplyServerVm(passApplyServerVmParam, loginUserVo);
        return BaseResult.success(null);

    }


    /**
     * 审核云服务器申请时-获取申请详情，及模板详情，计算资源等信息
     */
    @PostMapping("/passApplyServerVmDetail")
    @ParamCheck
    public BaseResult<PassApplyServerVmDetailRespDto> passApplyServerVmDetail(@ModelCheck(notNull = true) @RequestBody WorkOrderDetailParam workOrderDetailParam, @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(workOrderService.passApplyServerVmDetailRespDto(workOrderDetailParam, loginUserVo));

    }

    /**
     * 申请虚拟机详情
     */
    @PostMapping("/applyServerVmDetail")
    @ParamCheck
    public BaseResult<ApplyServerVmDetailRespDto> getApplyServerVmDetail(@ModelCheck(notNull = true) @RequestBody
                                                                                 WorkOrderDetailParam
                                                                                 workOrderDetailParam,
                                                                         @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(workOrderService.getApplyServerVmDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId(), loginUserVo));

    }


    /**
     * 审核云服务器变更申请时-获取申请详情，及模板详情，计算资源等信息
     */
    @PostMapping("/passModifyServerVmDetail")
    @ParamCheck
    public BaseResult<PassModifyServerVmDetailRespDto> passModifyServerVmDetail(@ModelCheck(notNull = true) @RequestBody WorkOrderDetailParam workOrderDetailParam, @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(workOrderService.passModifyServerVmDetail(workOrderDetailParam, loginUserVo));

    }


    /**
     * 变更云服务器 通过
     */
    @PostMapping("/passModifyServerVm")
    @ParamCheck
    public BaseResult<String> passModifyServerVm(@ModelCheck(notNull = true) @RequestBody PassModifyServerVmParam
                                                         passModifyServerVmParam, @LoginUser LoginUserVo
                                                         loginUserVo) {
        workOrderService.passModifyServerVm(passModifyServerVmParam, loginUserVo);
        return BaseResult.success(null);

    }

    /**
     * 变更云服务器详情
     */
    @PostMapping("/modifyServerVmDetail")
    @ParamCheck
    public BaseResult<ModifyServerVmDetailRespDto> modifyServerVmDetail(@ModelCheck(notNull = true) @RequestBody
                                                                                WorkOrderDetailParam
                                                                                workOrderDetailParam) {

        return BaseResult.success(workOrderService.getModifyServerVmDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId()));

    }

    /**
     * 注册账号详情
     */
    @PostMapping("/registerUserDetail")
    @ParamCheck
    public BaseResult<WorkOrderUserDetailRespDto> registerUserDetail(@ModelCheck(notNull = true) @RequestBody
                                                                             WorkOrderDetailParam
                                                                             workOrderDetailParam) {

        return BaseResult.success(workOrderService.getWorkOrderUserDetailByWorkOrderId(workOrderDetailParam
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

        return BaseResult.success(workOrderService.getWorkOrderUserDetailByWorkOrderId(workOrderDetailParam
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

        return BaseResult.success(workOrderService.applyDeferredDetailByWorkOrderId(workOrderDetailParam
                .getWorkOrderId()));

    }

    /**
     * 申请延期云服务器详情
     */
    @PostMapping("/getWaitCheckCount")
    public BaseResult<UserWaitCheckCountParam> getWaitCheckCount(@LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(workOrderService.getWaitCheckCount(loginUserVo));

    }

    /**
     * 审核变更VDC-获取变更详情
     */
    @PostMapping("/passModifyVdcDetail")
    @ParamCheck
    public BaseResult<PassModifyVdcDetailRespDto> passModifyVdcDetail(@ModelCheck(notNull = true) @RequestBody WorkOrderDetailParam workOrderDetailParam, @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(workOrderService.passModifyVdcDetail(workOrderDetailParam, loginUserVo));

    }


    @PostMapping("/passModifyVdc")
    @ParamCheck
    public BaseResult<PassModifyVdcDetailRespDto> passModifyVdc(@ModelCheck(notNull = true) @RequestBody PassModifyVdcResourceParam passModifyVdcResourceParam, @LoginUser LoginUserVo loginUserVo) {
        workOrderService.passModifyVdc(passModifyVdcResourceParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/applyModifyVdcDetail")
    @ParamCheck
    public BaseResult<ApplyModifyVdcDetailRespDto> applyModifyVdcDetail(@ModelCheck(notNull = true) @RequestBody
                                                                                WorkOrderDetailParam
                                                                                workOrderDetailParam) {
        return BaseResult.success(workOrderService.applyModifyVdcDetail(workOrderDetailParam));

    }


}
