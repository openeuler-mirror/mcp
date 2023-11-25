package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.CloudClusterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_zone")
public class CloudZoneDo extends BaseDo {


    private String name;

    private String remark;

    //资源类型
    private CloudClusterType type;
}
