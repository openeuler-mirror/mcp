package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_vdc_cpu")
public class CloudVdcCpuDo extends BaseDo {


    private Integer vdcId;

    private Integer vcpus;

    private ArchitectureType architecture;

}
