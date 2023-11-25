package com.hnkylin.cloud.manage.entity.resp.index;

import com.hnkylin.cloud.manage.entity.mc.resp.McServerVmUseRatioData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IndexCommonData {


    //vdc数据
    private IndexVdcData vdcData = new IndexVdcData();


    //云服务器数据
    private IndexServerVmData serverVmData = new IndexServerVmData();

    //用户数据
    private IndexUserData userData = new IndexUserData();


    //提醒数据
    private IndexNoticeData noticeData = new IndexNoticeData();

    //云服务器cpu利用率top5
    private List<McServerVmUseRatioData> serverVmCpuTopUseRatioList = new ArrayList<>();
    //云服务器内存利用率top5
    private List<McServerVmUseRatioData> serverVmMemTopUseRatioList = new ArrayList<>();
}
