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
@TableName("cloud_work_order_servervm_iso")
public class CloudWorkOrderServerVmIsoDo extends BaseDo {

    //工单ID
    private Integer workOrderId;

    private String isoFile;

    private ModifyType modifyType;


    private String oldIsoFile;


}
