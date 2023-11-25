package com.hnkylin.cloud.core.common.servervm;

import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.McServerVmTaskStatus;
import lombok.Data;

/**
 * 分页获取云服务器列表
 * Created by kylin-ksvd on 21-7-9.
 */
@Data
public class McServerVmPageDetailResp {


    //云服务器id
    private Integer id;

    //云服务器uuid
    private String uuid;

    //云服务器名称
    private String aliasName;

    //云服务器状态
    private McServerVmStatus status;

    //云服务器任务状态
    private McServerVmTaskStatus taskStatus;

    private String architecture;

    //云服务器ip
    private String ip;

    //操作系统
    private String os;

    //cpu数
    private Integer cpus;

    //内存大小
    private Integer memory;

    //磁盘大小
    private String disks;

    //cpu利用率
    private String cpuRate;

    //内存利用率
    private String memoryRate;

    //磁盘利用率
    private String diskRate;

    //创建时间
    private String createDate;

    //内存单位
    private String memoryUnit;

    //描述
    private String description;

    private String logo;

    private String logoName;

}
