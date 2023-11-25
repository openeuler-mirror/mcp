package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.cluster.CheckClusterNameAndPasswordParam;
import com.hnkylin.cloud.manage.entity.resp.index.IndexStatisticData;
import com.hnkylin.cloud.manage.entity.resp.index.OrgLeaderIndexStatisticData;
import com.hnkylin.cloud.manage.service.IndexStatisticService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by kylin-ksvd on 21-8-5.
 */
@RestController
@RequestMapping("/api")
public class IndexStatisticCtrl {

    @Resource
    private IndexStatisticService indexStatisticService;

    @PostMapping("/indexStatistic")
    public BaseResult<IndexStatisticData> statisticData(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(indexStatisticService.statisticData(loginUserVo));

    }


    /**
     * 组织管理员登录首页数据
     *
     * @param loginUserVo
     * @return
     */
    @PostMapping("/orgLeaderStatistic")
    public BaseResult<OrgLeaderIndexStatisticData> orgLeaderStatistic(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(indexStatisticService.orgLeaderStatistic(loginUserVo));

    }


}
