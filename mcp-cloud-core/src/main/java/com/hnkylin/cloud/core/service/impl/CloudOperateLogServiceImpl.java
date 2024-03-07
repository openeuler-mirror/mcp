package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.DateUtils;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.BaseDo;
import com.hnkylin.cloud.core.domain.CloudOperateLogDo;
import com.hnkylin.cloud.core.entity.req.operateLog.BasePageOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.ParentOperateLogParam;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogResp;
import com.hnkylin.cloud.core.enums.OperateLogAction;
import com.hnkylin.cloud.core.enums.OperateLogStatus;
import com.hnkylin.cloud.core.enums.OperateLogType;
import com.hnkylin.cloud.core.mapper.CloudOperateLogMapper;
import com.hnkylin.cloud.core.service.CloudOperateLogService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class CloudOperateLogServiceImpl extends ServiceImpl<CloudOperateLogMapper, CloudOperateLogDo>
        implements CloudOperateLogService {


    @Override
    public PageData<OperateLogResp> listOperateLogByObjId(BasePageOperateLogParam basePageOperateLogParam,
                                                          Integer loginUserId) {

        CloudOperateLogDo queryOperateLog = new CloudOperateLogDo();
        queryOperateLog.setObjId(basePageOperateLogParam.getObjId());
        queryOperateLog.setType(basePageOperateLogParam.getType().name());
        queryOperateLog.setDeleteFlag(false);
        queryOperateLog.setParentId(0);
        QueryWrapper<CloudOperateLogDo> queryWrapper = new QueryWrapper<>(queryOperateLog);
        queryWrapper.orderByDesc("id");
        PageHelper.startPage(basePageOperateLogParam.getPageNo(), basePageOperateLogParam.getPageSize());
        List<CloudOperateLogDo> operateLogDoList = list(queryWrapper);
        if (operateLogDoList.isEmpty()) {
            return new PageData(null);
        }
        List<OperateLogResp> operateLogRespList = new ArrayList<>();
        operateLogDoList.forEach(item -> {
            OperateLogResp operateLogResp = formatOperateLogResp(item);
            operateLogResp.setHasChildOperateLog(hasChildOperateLog(item.getId()));
            operateLogRespList.add(operateLogResp);

        });
        PageInfo<CloudOperateLogDo> pageInfo = new PageInfo<>(operateLogDoList);
        PageData pageData = new PageData(pageInfo);
        pageData.setList(operateLogRespList);
        return pageData;
    }

    /**
     * 判断是否有子任务
     *
     * @param parentLogId
     * @return
     */
    @Override
    public boolean hasChildOperateLog(Integer parentLogId) {
        CloudOperateLogDo queryOperateLog = new CloudOperateLogDo();
        queryOperateLog.setParentId(parentLogId);
        queryOperateLog.setDeleteFlag(false);
        QueryWrapper<CloudOperateLogDo> queryWrapper = new QueryWrapper<>(queryOperateLog);
        List<CloudOperateLogDo> operateLogDoList = list(queryWrapper);
        return !operateLogDoList.isEmpty();
    }

    @Override
    public List<OperateLogResp> listChildOperateLogList(ParentOperateLogParam parentOperateLogParam) {
        CloudOperateLogDo queryOperateLog = new CloudOperateLogDo();
        queryOperateLog.setDeleteFlag(false);
        queryOperateLog.setParentId(parentOperateLogParam.getParentLogId());
        QueryWrapper<CloudOperateLogDo> queryWrapper = new QueryWrapper<>(queryOperateLog);
        queryWrapper.orderByAsc("id");
        List<CloudOperateLogDo> operateLogDoList = list(queryWrapper);
        List<OperateLogResp> operateLogRespList = new ArrayList<>();
        operateLogDoList.forEach(item -> {
            operateLogRespList.add(formatOperateLogResp(item));

        });

        return operateLogRespList;
    }

    /**
     * 封装操作日志详情
     *
     * @param operateLogDo
     * @return
     */
    private OperateLogResp formatOperateLogResp(CloudOperateLogDo operateLogDo) {
        OperateLogResp operateLogResp = new OperateLogResp();
        operateLogResp.setOperateLogId(operateLogDo.getId());
        operateLogResp.setAction(OperateLogAction.valueOf(operateLogDo.getAction()));
        operateLogResp.setType(OperateLogType.valueOf(operateLogDo.getType()));
        operateLogResp.setStatus(OperateLogStatus.valueOf(operateLogDo.getStatus()));
        operateLogResp.setDetail(operateLogDo.getDetail());
        operateLogResp.setResult(operateLogDo.getResult());
        operateLogResp.setPercent(operateLogDo.getPercent());
        operateLogResp.setObjId(operateLogDo.getObjId());
        operateLogResp.setObjName(operateLogDo.getObjName());
        operateLogResp.setStartTime(DateUtils.format(operateLogDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
        if (Objects.nonNull(operateLogDo.getEndTime())) {
            operateLogResp.setEndTime(DateUtils.format(operateLogDo.getEndTime(), DateUtils.DATE_ALL_PATTEN));
        }
        return operateLogResp;
    }


    @Override
    public void createCommonOperateLog(BaseDo baseDo, String objName, OperateLogType operateLogType,
                                       OperateLogAction operateLogAction,
                                       Integer operateUserId, String clientIP) {
        CloudOperateLogDo operateLogDo = new CloudOperateLogDo();
        operateLogDo.setParentId(0);
        operateLogDo.setType(operateLogType.name());
        operateLogDo.setAction(operateLogAction.name());
        operateLogDo.setStatus(OperateLogStatus.SUCCESS.name());
        operateLogDo.setPercent("100");
        operateLogDo.setObjId(baseDo.getId());
        operateLogDo.setObjName(objName);
        operateLogDo.setDetail(operateLogAction.getDesc());
        operateLogDo.setClientIp(clientIP);
        operateLogDo.setCreateBy(operateUserId);
        operateLogDo.setCreateTime(new Date());
        operateLogDo.setEndTime(new Date());
        save(operateLogDo);
    }


    @Override
    public void createServerVmFailOperateLog(Integer objId, String objName, OperateLogType operateLogType,
                                             OperateLogAction operateLogAction, Long mcTaskId, Integer clusterId,
                                             Integer operateUserId,
                                             String clientIP, String result) {
        CloudOperateLogDo operateLogDo = new CloudOperateLogDo();
        operateLogDo.setParentId(0);
        operateLogDo.setType(operateLogType.name());
        operateLogDo.setAction(operateLogAction.name());
        operateLogDo.setStatus(OperateLogStatus.FAIL.name());
        operateLogDo.setPercent("100");
        operateLogDo.setObjId(objId);
        operateLogDo.setObjName(objName);
        operateLogDo.setMcTaskId(mcTaskId);
        operateLogDo.setDetail(operateLogAction.getDesc());
        operateLogDo.setResult(result);
        operateLogDo.setClientIp(clientIP);
        operateLogDo.setCreateBy(operateUserId);
        operateLogDo.setCreateTime(new Date());
        operateLogDo.setEndTime(new Date());
        operateLogDo.setClusterId(clusterId);
        save(operateLogDo);
    }

    @Override
    public void createServerVmRunningOperateLog(Integer objId, String objName, OperateLogType operateLogType,
                                                OperateLogAction operateLogAction, Long mcTaskId, Integer clusterId,
                                                Integer operateUserId, String clientIP) {
        CloudOperateLogDo operateLogDo = new CloudOperateLogDo();
        operateLogDo.setParentId(0);
        operateLogDo.setType(operateLogType.name());
        operateLogDo.setAction(operateLogAction.name());
        operateLogDo.setStatus(OperateLogStatus.RUNNING.name());
        operateLogDo.setPercent("100");
        operateLogDo.setObjId(objId);
        operateLogDo.setObjName(objName);
        operateLogDo.setMcTaskId(mcTaskId);
        operateLogDo.setClusterId(clusterId);
        operateLogDo.setDetail(operateLogAction.getDesc());
        operateLogDo.setClientIp(clientIP);
        operateLogDo.setCreateBy(operateUserId);
        operateLogDo.setCreateTime(new Date());
        save(operateLogDo);
    }

    @Override
    public List<CloudOperateLogDo> getMcRunningTask() {
        CloudOperateLogDo operateLogDo = new CloudOperateLogDo();
        QueryWrapper<CloudOperateLogDo> wrapper = new QueryWrapper<>(operateLogDo);
        wrapper.gt("mc_task_id", 0);
        wrapper.gt("cluster_id", 0);
        List<String> statusList = new ArrayList<>();
        statusList.add(OperateLogStatus.RUNNING.name());
        statusList.add(OperateLogStatus.CANCELING.name());
        statusList.add(OperateLogStatus.WAIT_START.name());
        wrapper.in("status", statusList);
        return getBaseMapper().selectList(wrapper);
    }









    @Override
    public CloudOperateLogDo queryCloudOperateLogById(int id) {
        return getBaseMapper().selectById(id);
    }


}
