package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.resp.index.IndexStatisticData;
import com.hnkylin.cloud.manage.entity.resp.index.OrgLeaderIndexStatisticData;

public interface IndexStatisticService {

    /**
     * 系统管理员-首页数据统计
     *
     * @param loginUserVo
     * @return
     */
    IndexStatisticData statisticData(LoginUserVo loginUserVo);


    /**
     * 非系统管理员-首页数据
     *
     * @param loginUserVo
     * @return
     */
    OrgLeaderIndexStatisticData orgLeaderStatistic(LoginUserVo loginUserVo);


}
