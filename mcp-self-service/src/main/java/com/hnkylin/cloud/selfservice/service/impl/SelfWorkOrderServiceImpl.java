package com.hnkylin.cloud.selfservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.DateUtils;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudWorkOrderDo;
import com.hnkylin.cloud.core.service.CloudUserService;
import com.hnkylin.cloud.core.service.CloudWorkOrderService;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.WorkOrderPageParam;
import com.hnkylin.cloud.selfservice.entity.resp.BaseWorkOrderDetailDto;
import com.hnkylin.cloud.selfservice.entity.resp.PageWorkOrderRespDto;
import com.hnkylin.cloud.selfservice.service.SelfWorkOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SelfWorkOrderServiceImpl implements SelfWorkOrderService {

    @Resource
    private CloudWorkOrderService cloudWorkOrderService;

    @Resource
    private CloudUserService cloudUserService;

    @Override
    public PageData<PageWorkOrderRespDto> pageWorkOrder(WorkOrderPageParam workOrderPageParam,
                                                        LoginUserVo loginUserVo) {

        CloudWorkOrderDo cloudWorkOrderDo = new CloudWorkOrderDo();
        cloudWorkOrderDo.setDeleteFlag(Boolean.FALSE);
        cloudWorkOrderDo.setUserId(loginUserVo.getUserId());
        QueryWrapper<CloudWorkOrderDo> wrapper = new QueryWrapper<>(cloudWorkOrderDo);
        wrapper.orderByDesc("id");

        PageHelper.startPage(workOrderPageParam.getPageNo(), workOrderPageParam.getPageSize());
        List<CloudWorkOrderDo> workOrderDoList = cloudWorkOrderService.list(wrapper);

        PageInfo<CloudWorkOrderDo> pageInfo = new PageInfo<>(workOrderDoList);

        List<PageWorkOrderRespDto> list = new ArrayList<>(workOrderDoList.size());
        workOrderDoList.forEach(workOrder -> {
            PageWorkOrderRespDto workOrderRespDto = new PageWorkOrderRespDto();
            workOrderRespDto.setWorkOrderId(workOrder.getId());
            workOrderRespDto.setType(workOrder.getType());
            workOrderRespDto.setTypeDesc(workOrder.getType().getDesc());
            workOrderRespDto.setTarget(workOrder.getTarget());
            workOrderRespDto.setStatus(workOrder.getStatus());
            workOrderRespDto.setStatusDesc(workOrder.getStatus().getDesc());
            workOrderRespDto.setApplyReason(workOrder.getApplyReason());
            workOrderRespDto.setAuditOpinion(workOrder.getAuditOpinion());
            workOrderRespDto.setApplyTime(DateUtils.format(workOrder.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
            list.add(workOrderRespDto);
        });
        PageData pageData = new PageData(pageInfo);
        pageData.setList(list);

        return pageData;
    }

    @Override
    public void formatBaseWorkOrderDetail(Integer workOrderId, BaseWorkOrderDetailDto baseWorkOrderDetailDto) {
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderId);
        baseWorkOrderDetailDto.setWorkOrderTarget(cloudWorkOrderDo.getTarget());
        baseWorkOrderDetailDto.setWorkOrderType(cloudWorkOrderDo.getType());
        baseWorkOrderDetailDto.setWorkOrderTypeDesc(cloudWorkOrderDo.getType().getDesc());
        baseWorkOrderDetailDto.setApplyReason(cloudWorkOrderDo.getApplyReason());
        baseWorkOrderDetailDto.setStatus(cloudWorkOrderDo.getStatus());
        baseWorkOrderDetailDto.setStatusDesc(cloudWorkOrderDo.getStatus().getDesc());
        baseWorkOrderDetailDto.setUserId(cloudWorkOrderDo.getUserId());
        baseWorkOrderDetailDto.setApplyTime(DateUtils.format(cloudWorkOrderDo.getCreateTime(),
                DateUtils.DATE_ALL_PATTEN));
        baseWorkOrderDetailDto.setAuditOpinion(cloudWorkOrderDo.getAuditOpinion());

        if (Objects.nonNull(cloudWorkOrderDo.getAuditTime())) {
            baseWorkOrderDetailDto.setAuditionTime(DateUtils.format(cloudWorkOrderDo.getAuditTime(),
                    DateUtils.DATE_ALL_PATTEN));
        }
        if (Objects.nonNull(cloudWorkOrderDo.getAuditBy()) && cloudWorkOrderDo.getAuditBy() > 0) {
            CloudUserDo auditionUser = cloudUserService.getById(cloudWorkOrderDo.getAuditBy());
            baseWorkOrderDetailDto.setAuditionUser(auditionUser.getUserName());
            if (StringUtils.isNotBlank(auditionUser.getRealName())) {
                baseWorkOrderDetailDto.setAuditionUser(auditionUser.getRealName());
            }
        }
    }
}
