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
@TableName("cloud_cluster_node")
public class CloudClusterNodeDo extends BaseDo {


    private Integer clusterId;


    private String httpType;

    private String ipAddress;


    private Integer port;

}
