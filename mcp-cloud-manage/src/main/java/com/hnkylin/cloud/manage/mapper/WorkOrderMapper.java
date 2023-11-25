package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudWorkOrderDo;
import com.hnkylin.cloud.manage.entity.req.workorder.WorkOrderPageParam;
import com.hnkylin.cloud.manage.entity.resp.workorder.PageWorkOrderRespDto;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-6-24.
 */
public interface WorkOrderMapper extends BaseMapper<CloudWorkOrderDo> {

    List<PageWorkOrderRespDto> pageWorkOrder(WorkOrderPageParam workOrderPageParam);
}
