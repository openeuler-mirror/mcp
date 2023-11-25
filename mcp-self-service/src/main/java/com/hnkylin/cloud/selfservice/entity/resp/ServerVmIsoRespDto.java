package com.hnkylin.cloud.selfservice.entity.resp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServerVmIsoRespDto {


    //模板名称
    private String templateName;

    private Integer clusterId;

    private String clusterName;

    private String clusterRemark;


}
