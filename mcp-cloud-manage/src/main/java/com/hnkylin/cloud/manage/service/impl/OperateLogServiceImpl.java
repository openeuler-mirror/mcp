package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.DateUtils;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.entity.req.operateLog.BasePageOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.ParentOperateLogParam;
import com.hnkylin.cloud.core.entity.req.operateLog.QueryOperateLogActionParam;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogActionResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogStatusResp;
import com.hnkylin.cloud.core.entity.resp.operateLog.OperateLogTypeResp;
import com.hnkylin.cloud.core.enums.OperateLogAction;
import com.hnkylin.cloud.core.enums.OperateLogStatus;
import com.hnkylin.cloud.core.enums.OperateLogType;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.config.KcpI18nUtil;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.operateLog.OperateLogDetailParam;
import com.hnkylin.cloud.manage.entity.req.operateLog.PageOperateLogParam;
import com.hnkylin.cloud.manage.entity.resp.operateLog.OperateDetailResp;
import com.hnkylin.cloud.manage.service.OperateLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private KcpI18nUtil kcpI18nUtil;

    @Resource
    private CloudOperateLogService cloudOperateLogService;

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private CloudZoneService cloudZoneService;

    @Override
    public List<OperateLogTypeResp> getOperateLogType() {
        List<OperateLogTypeResp> operateLogTypeRespList = new ArrayList<>();
        for (OperateLogType type : OperateLogType.values()) {
            OperateLogTypeResp operateLogTypeResp = new OperateLogTypeResp();
            operateLogTypeResp.setType(type);
            operateLogTypeResp.setI8nMessage(kcpI18nUtil.operateLogTypeMessage(type));
            operateLogTypeRespList.add(operateLogTypeResp);
        }
        return operateLogTypeRespList;
    }

    @Override
    public List<OperateLogActionResp> getOperateLogAction(QueryOperateLogActionParam queryOperateLogActionParam) {
        List<OperateLogAction> actionList = new ArrayList<>();
        if (Objects.isNull(queryOperateLogActionParam.getType())) {
            actionList = Arrays.asList(OperateLogAction.values());
        } else {
            actionList = OperateLogAction.getActionListByType(queryOperateLogActionParam.getType());
        }
        List<OperateLogActionResp> operateLogActionRespList = new ArrayList<>();
        actionList.forEach(item -> {
            OperateLogActionResp actionResp = new OperateLogActionResp();
            actionResp.setAction(item);
            actionResp.setI8nMessage(kcpI18nUtil.operateLogActionMessage(item));
            operateLogActionRespList.add(actionResp);
        });
        return operateLogActionRespList;
    }

    @Override
    public List<OperateLogStatusResp> getOperateLogStatus() {
        List<OperateLogStatus> statusList = Arrays.asList(OperateLogStatus.values());

        List<OperateLogStatusResp> operateLogStatusRespList = new ArrayList<>();
        statusList.forEach(item -> {
            OperateLogStatusResp statusResp = new OperateLogStatusResp();
            statusResp.setStatus(item);
            statusResp.setI8nMessage(kcpI18nUtil.operateLogStatusMessage(item));
            operateLogStatusRespList.add(statusResp);
        });
        return operateLogStatusRespList;
    }

    @Override
    public PageData<OperateDetailResp> pageOperateLog(PageOperateLogParam pagOperateLogParam, LoginUserVo loginUserVo) {

        CloudOperateLogDo queryOperateLog = new CloudOperateLogDo();
        queryOperateLog.setDeleteFlag(false);
        queryOperateLog.setParentId(0);
        if (StringUtils.isNotBlank(pagOperateLogParam.getType())) {
            queryOperateLog.setType(pagOperateLogParam.getType());
        }
        if (StringUtils.isNotBlank(pagOperateLogParam.getAction())) {
            queryOperateLog.setAction(pagOperateLogParam.getAction());
        }

        if (StringUtils.isNotBlank(pagOperateLogParam.getStatus())) {
            queryOperateLog.setStatus(pagOperateLogParam.getStatus());
        }
        QueryWrapper<CloudOperateLogDo> queryWrapper = new QueryWrapper<>(queryOperateLog);

        if (StringUtils.isNotBlank(pagOperateLogParam.getStartDate())) {
            queryWrapper.ge("create_time", DateUtils.parse(pagOperateLogParam.getStartDate() + DateUtils.DAY_START,
                    DateUtils.DATE_ALL_PATTEN));
        }
        if (StringUtils.isNotBlank(pagOperateLogParam.getEndDate())) {
            queryWrapper.le("create_time", DateUtils.parse(pagOperateLogParam.getEndDate() + DateUtils.DAY_END,
                    DateUtils.DATE_ALL_PATTEN));
        }
        queryWrapper.orderByDesc("id");
        PageHelper.startPage(pagOperateLogParam.getPageNo(), pagOperateLogParam.getPageSize());

        List<CloudOperateLogDo> operateLogDoList = cloudOperateLogService.list(queryWrapper);
        if (operateLogDoList.isEmpty()) {
            return new PageData(null);
        }
        List<OperateDetailResp> operateLogRespList = new ArrayList<>();
        operateLogDoList.forEach(item -> {
            operateLogRespList.add(createOperateDetail(item));
        });
        PageInfo<CloudOperateLogDo> pageInfo = new PageInfo<>(operateLogDoList);
        PageData pageData = new PageData(pageInfo);
        pageData.setList(operateLogRespList);
        return pageData;

    }


    /**
     * 创建任务列表响应实体
     *
     * @param operateLogDo
     */
    private OperateDetailResp createOperateDetail(CloudOperateLogDo operateLogDo) {
        OperateDetailResp operateDetailResp = new OperateDetailResp();
        operateDetailResp.setType(kcpI18nUtil.operateLogTypeMessage(OperateLogType.valueOf(operateLogDo.getType())));
        operateDetailResp.setTypeEnum(operateLogDo.getType());
        operateDetailResp.setAction(kcpI18nUtil.operateLogActionMessage(OperateLogAction.valueOf(operateLogDo.getAction())));
        operateDetailResp.setActionEnum(operateLogDo.getAction());
        operateDetailResp.setStatus(kcpI18nUtil.operateLogStatusMessage(OperateLogStatus.valueOf(operateLogDo.getStatus())));
        operateDetailResp.setEnumStatus(OperateLogStatus.valueOf(operateLogDo.getStatus()));
        operateDetailResp.setObjName(operateLogDo.getObjName());
        operateDetailResp.setOperateLogId(operateLogDo.getId());
        operateDetailResp.setResult(operateLogDo.getResult());
        operateDetailResp.setDetail(operateLogDo.getDetail());



        operateDetailResp.setPercent(operateLogDo.getPercent());
        operateDetailResp.setStartTime(DateUtils.format(operateLogDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
        if (Objects.nonNull(operateLogDo.getEndTime())) {
            operateDetailResp.setEndTime(DateUtils.format(operateLogDo.getEndTime(), DateUtils.DATE_ALL_PATTEN));
        }
        operateDetailResp.setClientIp(operateLogDo.getClientIp());
        CloudUserDo userDo = cloudUserService.getById(operateLogDo.getCreateBy());
        operateDetailResp.setOperateUser(userDo.getUserName());

        CloudOrganizationDo organizationDo = cloudOrganizationService.getById(userDo.getOrganizationId());
        operateDetailResp.setOrgName(organizationDo.getOrganizationName());

        if (!Objects.equals(organizationDo.getId(), cloudOrganizationService.getDefaultTopOrg().getId())) {
            CloudVdcDo vdcDo = cloudVdcService.getVdcByOrgId(organizationDo.getId());
            CloudZoneDo cloudZoneDo = cloudZoneService.getById(vdcDo.getZoneId());
            operateDetailResp.setZoneName(cloudZoneDo.getName());
        }
        operateDetailResp.setHasChildOperateLog(cloudOperateLogService.hasChildOperateLog(operateLogDo.getId()));
        return operateDetailResp;
    }



    @Override
    public List<OperateDetailResp> childOperateLogList(ParentOperateLogParam parentOperateLogParam) {
        CloudOperateLogDo queryOperateLog = new CloudOperateLogDo();
        queryOperateLog.setDeleteFlag(false);
        queryOperateLog.setParentId(parentOperateLogParam.getParentLogId());
        QueryWrapper<CloudOperateLogDo> queryWrapper = new QueryWrapper<>(queryOperateLog);
        queryWrapper.orderByAsc("id");
        List<CloudOperateLogDo> operateLogDoList = cloudOperateLogService.list(queryWrapper);
        List<OperateDetailResp> operateLogRespList = new ArrayList<>();
        operateLogDoList.forEach(item -> {
            operateLogRespList.add(createOperateDetail(item));
        });
        return operateLogRespList;
    }

    @Override
    public OperateDetailResp operateLogDetail(OperateLogDetailParam operateLogDetailParam) {
        CloudOperateLogDo operateLogDo = cloudOperateLogService.getById(operateLogDetailParam.getOperateLog());

        return createOperateDetail(operateLogDo);
    }

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
        List<CloudOperateLogDo> operateLogDoList = cloudOperateLogService.list(queryWrapper);
        if (operateLogDoList.isEmpty()) {
            return new PageData(null);
        }
        List<OperateLogResp> operateLogRespList = new ArrayList<>();
        operateLogDoList.forEach(item -> {
            OperateLogResp operateLogResp = formatOperateLogResp(item);
            operateLogResp.setHasChildOperateLog(cloudOperateLogService.hasChildOperateLog(item.getId()));
            operateLogRespList.add(operateLogResp);

        });
        PageInfo<CloudOperateLogDo> pageInfo = new PageInfo<>(operateLogDoList);
        PageData pageData = new PageData(pageInfo);
        pageData.setList(operateLogRespList);
        return pageData;
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
        operateLogResp.setTypeDesc(kcpI18nUtil.operateLogTypeMessage(OperateLogType.valueOf(operateLogDo.getType())));
        operateLogResp.setActionDesc(kcpI18nUtil.operateLogActionMessage(OperateLogAction.valueOf(operateLogDo.getAction())));
        operateLogResp.setStartTime(DateUtils.format(operateLogDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
        if (Objects.nonNull(operateLogDo.getEndTime())) {
            operateLogResp.setEndTime(DateUtils.format(operateLogDo.getEndTime(), DateUtils.DATE_ALL_PATTEN));
        }
        CloudUserDo userDo = cloudUserService.getById(operateLogDo.getCreateBy());
        operateLogResp.setOperateUser(userDo.getUserName());

        CloudOrganizationDo organizationDo = cloudOrganizationService.getById(userDo.getOrganizationId());
        operateLogResp.setOrgName(organizationDo.getOrganizationName());

        if (!Objects.equals(organizationDo.getId(), cloudOrganizationService.getDefaultTopOrg().getId())) {
            CloudVdcDo vdcDo = cloudVdcService.getVdcByOrgId(organizationDo.getId());
            CloudZoneDo cloudZoneDo = cloudZoneService.getById(vdcDo.getZoneId());
            operateLogResp.setZoneName(cloudZoneDo.getName());
        }
        operateLogResp.setClientIp(operateLogDo.getClientIp());
        return operateLogResp;
    }
}
