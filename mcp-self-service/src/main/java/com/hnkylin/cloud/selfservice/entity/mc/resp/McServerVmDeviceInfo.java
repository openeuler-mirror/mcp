package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

import java.util.List;

/**
 * 云服务器概要信息，cpu内存，网卡
 * Created by kylin-ksvd on 21-7-15.
 */
@Data
public class McServerVmDeviceInfo {


    private List<McServerVmDiskInfo> disks;
    private McServerVmCpuInfo cpu;
    private McServerVmMemoryInfo memory;
    private List<McServerVmNetworkInfo> interfaces;


}
