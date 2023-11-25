package com.hnkylin.cloud.manage.entity.mc.resp;

import com.hnkylin.cloud.manage.enums.McPhysicaHostStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 集群管理-物理主机
 */
@Data
public class McClusterPhysicalHostResp {

    //主机ID
    private String serverId;

    //主机地址
    private String serverAddr;

    //服务器架构
    private String serverArch;


    //cpu模型
    private String cpuModelName;

    //服务器类型
    private String serverType;

    //cpu使用率
    private BigDecimal cpuUtil = BigDecimal.ZERO;

    //内存使用率
    private BigDecimal memUtil = BigDecimal.ZERO;

    //主机状态
    private McPhysicaHostStatus status;


}
