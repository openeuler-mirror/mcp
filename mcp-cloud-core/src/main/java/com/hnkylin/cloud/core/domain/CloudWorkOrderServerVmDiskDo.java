package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.DiskUnit;
import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_servervm_disk")
public class CloudWorkOrderServerVmDiskDo extends BaseDo {

    //工单ID
    private Integer workOrderId;

    //硬盘大小
    private Integer diskSize;

    private DiskUnit diskUnit = DiskUnit.GB;

    //用途
    private String purpose;

    //类型
    private ApplyMcServerVmType type;

    private Long diskId;

    private ModifyType modifyType;

    private Integer oldDiskSize;

    private DiskUnit oldDiskUnit = DiskUnit.GB;


}
