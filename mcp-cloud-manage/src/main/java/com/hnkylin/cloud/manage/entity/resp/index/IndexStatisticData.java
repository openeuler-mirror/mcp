package com.hnkylin.cloud.manage.entity.resp.index;


import com.hnkylin.cloud.manage.entity.mc.resp.McServerVmUseRatioData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页统计数据
 */
@Data
public class IndexStatisticData extends IndexCommonData {

    //可用区数据
    private IndexZoneData zoneData = new IndexZoneData();

    //集群数据
    private IndexClusterData clusterData = new IndexClusterData();

    //物理主机数据
    private IndexPhysicalHostData physicalHostData = new IndexPhysicalHostData();


    //cpu数据
    private IndexCpuData cpuData = new IndexCpuData();

    //内存数据
    private IndexMemData memData = new IndexMemData();

    //储存数据
    private IndexStorageData storageData = new IndexStorageData();


    //物理主机cpu利用率top5
    private List<IndexTopPhysicalHostUsedRatioData> physicalHostCpuTopUseRatioList = new ArrayList<>();
    //物理主机内存利用率top5
    private List<IndexTopPhysicalHostUsedRatioData> physicalHostMemTopUseRatioList = new ArrayList<>();

}
