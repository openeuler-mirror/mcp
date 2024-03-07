package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.entity.req.operateLog.BasePageOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.ParentOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.QueryOperateLogActionParam;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogActionResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogStatusResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogTypeResp;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.operateLog.OperateLogDetailParam;
import com.hnkylin.cloud.manage.entity.req.operateLog.PageOperateLogParam;
import com.hnkylin.cloud.manage.entity.resp.operateLog.OperateDetailResp;

import java.util.List;

public interface OperateLogService {

    /**
     * 查询任务类型
     *
     * @return
     */
    List<OperateLogTypeResp> getOperateLogType();

    /**
     * 查询任务操作
     *
     * @param queryOperateLogActionParam
     * @return
     */
    List<OperateLogActionResp> getOperateLogAction(QueryOperateLogActionParam queryOperateLogActionParam);


    /**
     * 查询任务状态
     *
     * @return
     */
    List<OperateLogStatusResp> getOperateLogStatus();


    /**
     * 分页查询操作日志
     *
     * @param pagOperateLogParam
     * @param loginUserVo
     * @return
     */
    PageData<OperateDetailResp> pageOperateLog(PageOperateLogParam pagOperateLogParam, LoginUserVo loginUserVo);


    /**
     * 获取子任务
     *
     * @param parentOperateLogParam
     * @return
     */
    List<OperateDetailResp> childOperateLogList(ParentOperateLogParam parentOperateLogParam);

    /**
     * 任务详情
     *
     * @param operateLogDetailParam
     * @return
     */
    OperateDetailResp operateLogDetail(OperateLogDetailParam operateLogDetailParam);


    /**
     * 根据对象ID获取操作日志
     *
     * @param basePageOperateLogParam
     * @param loginUserId
     * @return
     */
    PageData<OperateLogResp> listOperateLogByObjId(BasePageOperateLogParam basePageOperateLogParam,
                                                   Integer loginUserId);

}
