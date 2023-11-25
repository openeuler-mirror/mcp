package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页统计数据
 */
@Data
public class IndexNoticeData {


    //待审核工单
    private Integer waitCheckCount = 0;

    //告警数
    private Integer alarmCount = 0;
}
