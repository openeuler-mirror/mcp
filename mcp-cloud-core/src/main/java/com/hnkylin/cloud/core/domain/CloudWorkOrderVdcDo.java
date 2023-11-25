package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_vdc")
public class CloudWorkOrderVdcDo extends BaseDo {


    private Integer workOrderId;


    private Integer vdcId;

    private Integer oldStorage;

    private Integer applyStorage;

    private Integer realStorage;

    private StorageUnit storageUnit;

}
