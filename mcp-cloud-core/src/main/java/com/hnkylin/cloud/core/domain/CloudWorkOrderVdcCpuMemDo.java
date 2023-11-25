package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.ArchitectureResourceType;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_vdc_cpu_mem")
public class CloudWorkOrderVdcCpuMemDo extends BaseDo {


    private Integer workOrderId;

    private ArchitectureResourceType resourceType;

    private ArchitectureType architecture;

    private Integer oldSize;

    private Integer applySize;

    private Integer realSize;

    private MemUnit unit;

}
