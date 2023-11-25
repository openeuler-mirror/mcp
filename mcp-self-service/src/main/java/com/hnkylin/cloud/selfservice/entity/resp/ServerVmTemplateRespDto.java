package com.hnkylin.cloud.selfservice.entity.resp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServerVmTemplateRespDto {


    //模板名称
    private String templateName;

    //模板ID
    private Integer templateId;

    //操作系统
    private String osMachine;

    //系统类型
    private String systemType;

    //架构
    private String architecture;

    //描述
    private String description;

    //cpu
    private Integer cpu;

    //内存
    private Integer mem;


    //磁盘
    private String diskInfo;

    private List<ServerVmNetworkDto> networks;

    //磁盘信息
    private List<ServerVmDiskDto> disks;

    private Integer clusterId;

    private String clusterName;

    private String clusterRemark;


}
