package com.hnkylin.cloud.manage.entity.resp.index;

import lombok.Data;

@Data
public class OrgLeaderIndexStatisticData extends IndexCommonData {


    //vdc-cpu资源
    private OrgLeaderVdcUsedData vdcCpuUsedData = new OrgLeaderVdcUsedData();

    //vdc-内存
    private OrgLeaderVdcUsedData vdcMemUsedData = new OrgLeaderVdcUsedData();


    //vdc-存储
    private OrgLeaderVdcUsedData vdcStorageUsedData = new OrgLeaderVdcUsedData();


}
