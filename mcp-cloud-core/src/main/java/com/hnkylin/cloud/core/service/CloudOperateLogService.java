package com.hnkylin.cloud.core.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.BaseDo;
import com.hnkylin.cloud.core.domain.CloudOperateLogDo;
import com.hnkylin.cloud.core.entity.req.operateLog.BasePageOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.ParentOperateLogParam;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogResp;
import com.hnkylin.cloud.core.enums.OperateLogAction;
import com.hnkylin.cloud.core.enums.OperateLogStatus;
import com.hnkylin.cloud.core.enums.OperateLogType;

import java.util.List;

public interface CloudOperateLogService extends IService<CloudOperateLogDo> {


    /**
     * 根据对象ID获取操作日志
     *
     * @param basePageOperateLogParam
     * @param loginUserId
     * @return
     */
    PageData<OperateLogResp> listOperateLogByObjId(BasePageOperateLogParam basePageOperateLogParam,
                                                   Integer loginUserId);

    boolean hasChildOperateLog(Integer parentLogId);

    /**
     * 根据父任务ID获取子任务列表
     *
     * @param parentOperateLogParam
     * @return
     */
    List<OperateLogResp> listChildOperateLogList(ParentOperateLogParam parentOperateLogParam);


    /**
     * 插入通用操作日志
     *
     * @param baseDo
     * @param objName
     * @param operateLogType
     * @param operateLogAction
     * @param operateUserId
     * @param clientIP
     */
    void createCommonOperateLog(BaseDo baseDo, String objName, OperateLogType operateLogType,
                                OperateLogAction operateLogAction, Integer operateUserId, String clientIP);


    /**
     * 插入云服务器操作失败日志
     *
     * @param objId
     * @param objName
     * @param operateLogType
     * @param operateLogAction
     * @param operateUserId
     * @param clientIP
     * @param result
     */
    void createServerVmFailOperateLog(Integer objId, String objName, OperateLogType operateLogType,
                                      OperateLogAction operateLogAction, Long mcTaskId, Integer clusterId,
                                      Integer operateUserId, String clientIP, String result);

    /**
     * 插入云服务器操正在进行中日志
     *
     * @param objId
     * @param objName
     * @param operateLogType
     * @param operateLogAction
     * @param operateUserId
     * @param clientIP
     */
    void createServerVmRunningOperateLog(Integer objId, String objName, OperateLogType operateLogType,
                                         OperateLogAction operateLogAction, Long mcTaskId, Integer clusterId,
                                         Integer operateUserId, String clientIP);


    /**
     * 获取正在执行中的mc操作日志
     *
     * @return
     */
    List<CloudOperateLogDo> getMcRunningTask();

    CloudOperateLogDo queryCloudOperateLogById(int id);



}
