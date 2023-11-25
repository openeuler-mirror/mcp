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
@TableName("cloud_cluster")
public class CloudClusterDo extends BaseDo {


    private String name;

    private String remark;

    private CloudClusterType type;


    private String clusterAdminName;

    private String clusterAdminPassword;

}
