package com.hnkylin.cloud.selfservice.entity.mc.resp;

import com.hnkylin.cloud.core.enums.McServerVmStatus;
import lombok.Data;

/**
 * 云服务器基础信息
 * Created by kylin-ksvd on 21-7-15.
 */
@Data
public class McServerVmDetailResp {

    //云服务器名称
    private String alisname;

    //云服务器描述
    private String description;

    //云服务器uuid
    private String uuid;

    //云服务器创建时间
    private String createDate;

    //云服务器ip
    private String ipAddress;

    //云服务器操作系统
    private String osName;

    //云服务器状态
    private McServerVmStatus status;

    private String architecture;

    private String systemType;

    private String logo;

    private String logoName;

    private String remoteUrl;

    private String remotePassword;


}
