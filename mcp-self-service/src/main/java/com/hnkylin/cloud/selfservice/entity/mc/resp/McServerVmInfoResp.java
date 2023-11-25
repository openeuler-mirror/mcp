package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

/**
 * 云服务器详情
 * Created by kylin-ksvd on 21-7-14.
 */
@Data
public class McServerVmInfoResp {


    //基础信息
    private McServerVmDetailResp machineInfo;

    //概要信息
    private McServerVmDeviceInfo deviceInfo;


}
