package com.hnkylin.cloud.selfservice.entity.resp;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@Data
public class PageUserMachineRespDto {


    //用户拥有云服务器ID
    private Integer userMachineId;
    //云服务器id
    private Integer machineId;
    //云服务器名称
    private String machineName;
    //云服务器状态
    private String machineStatus;
    //ip地址
    private String ip;
    //操作系统
    private String osMachine;
    //cpu数
    private Integer cpu;
    //内存
    private Integer mem;
    //内存单位
    private String memUnit;
    //磁盘大小
    private String disk;
    //cpu使用率
    private String cpuUsed;
    //内存使用率
    private String memUsed;
    //磁盘使用率
    private String diskUsed;
    //创建时间
    private String createTime;
    //截至时间
    private String deadlineTime;
    //uuid
    private String uuid;
    //描述
    private String description;


}
