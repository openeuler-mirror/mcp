package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import com.hnkylin.cloud.core.enums.WorkOrderType;
import lombok.Data;

import java.util.List;

@Data
public class WorkOrderPageParam extends BasePageParam {

    private WorkOrderStatus searchOrderStatus;

    private WorkOrderType searchOrderType;

    //private Integer organizationId;

    private String searchKey;


    private List<Integer> visibleUserIdList;

    private String startTime;

    private String endTime;
}
