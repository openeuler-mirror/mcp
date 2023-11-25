package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.workorder.*;
import com.hnkylin.cloud.manage.entity.resp.workorder.*;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
public interface WorkOrderService {

    /**
     * 分页获取工单
     */
    PageData<PageWorkOrderRespDto> pageWorkOrder(WorkOrderPageParam workOrderPageParam, LoginUserVo loginUserVo);

    /**
     * 批量审核
     */
    void batchCheck(BatchCheckParam batchCheckParam, LoginUserVo loginUserVo);


    /**
     * 注册审核-通过
     */
    void checkPassRegister(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo);

    /**
     * 修改帐号审核-通过
     */
    void checkPassUpdateUser(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo);


    /**
     * 拒绝工单
     */
    void refuseWorkOrder(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo);


    /**
     * 审核云服务器申请时-获取申请详情，及模板详情，计算资源等信息
     */
    PassApplyServerVmDetailRespDto passApplyServerVmDetailRespDto(WorkOrderDetailParam workOrderDetailParam,
                                                                  LoginUserVo loginUserVo);

    /**
     * 审核云服务器变更申请时-获取申请详情，及模板详情，计算资源等信息
     */
    PassModifyServerVmDetailRespDto passModifyServerVmDetail(WorkOrderDetailParam workOrderDetailParam,
                                                             LoginUserVo loginUserVo);

    /**
     * 申请云服务器审核通过
     */
    void passApplyServerVm(PassApplyServerVmParam passApplyServerVmParam, LoginUserVo loginUserVo);

    /**
     * 变更云服务器审核通过
     */
    void passModifyServerVm(PassModifyServerVmParam passModifyServerVmParam, LoginUserVo loginUserVo);

    /**
     * 延期云服务器-通过
     */
    void checkPassDeferred(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo);


    /**
     * 根据工单ID获取申请服务器工单详情
     */
    ApplyServerVmDetailRespDto getApplyServerVmDetailByWorkOrderId(Integer workOrderId, LoginUserVo loginUserVo);


    /**
     * 根据工单ID获取变更云服务器详情
     */
    ModifyServerVmDetailRespDto getModifyServerVmDetailByWorkOrderId(Integer workOrderId);


    /**
     * 根据工单ID获取注册申请/修改账号 工单详情
     */
    WorkOrderUserDetailRespDto getWorkOrderUserDetailByWorkOrderId(Integer workOrderId);

    /**
     * 根据工单ID获取延期云服务器详情
     */
    ApplyDeferredDetailRespDto applyDeferredDetailByWorkOrderId(Integer workOrderId);

    /**
     * 获取登录用户待审核的工单数量
     *
     * @param loginUserVo
     * @return
     */
    UserWaitCheckCountParam getWaitCheckCount(LoginUserVo loginUserVo);


    /**
     * 审核变更VDC-获取变更详情
     *
     * @param workOrderDetailParam
     * @param loginUserVo
     * @return
     */
    PassModifyVdcDetailRespDto passModifyVdcDetail(WorkOrderDetailParam workOrderDetailParam, LoginUserVo loginUserVo);

    /**
     * 变更VDC通过
     *
     * @param passModifyVdcResourceParam
     * @param loginUserVo
     */
    void passModifyVdc(PassModifyVdcResourceParam passModifyVdcResourceParam, LoginUserVo loginUserVo);


    /**
     * 申请变更VDC详情
     *
     * @param workOrderDetailParam
     * @return
     */
    ApplyModifyVdcDetailRespDto applyModifyVdcDetail(WorkOrderDetailParam workOrderDetailParam);


}
