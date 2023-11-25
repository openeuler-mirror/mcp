package com.hnkylin.cloud.selfservice.service;

import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.WorkOrderPageParam;
import com.hnkylin.cloud.selfservice.entity.resp.BaseWorkOrderDetailDto;
import com.hnkylin.cloud.selfservice.entity.resp.PageWorkOrderRespDto;

public interface SelfWorkOrderService {

    /**
     * 分页获取工单
     */
    PageData<PageWorkOrderRespDto> pageWorkOrder(WorkOrderPageParam workOrderPageParam, LoginUserVo loginUserVo);


    /**
     * 封装基础的工单详情
     */
    void formatBaseWorkOrderDetail(Integer workOrderId, BaseWorkOrderDetailDto baseWorkOrderDetailDto);
}
