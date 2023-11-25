package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 集群管理-资源使用情况
 */
@Data
public class McClusterBaseResource {


    private List<McClusterServerResourceResp> severs;


    //集群资源使用情况
    private McClusterResourceUsedResp resourceInfo;

    private String ksvdVersion;

    private String licenseExpire;


    private Integer onlinePhysicalHost = 0;

    private Integer offlinePhysicalHost = 0;

    private Integer totalPhysicalHost = 0;

    private Integer onlineMachine = 0;

    private Integer offlineMachine = 0;

    private Integer totalMachine = 0;

    private Integer clusterId = 0;

}
