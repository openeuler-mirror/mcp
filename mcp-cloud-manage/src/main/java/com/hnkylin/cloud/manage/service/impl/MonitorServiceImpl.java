package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.service.CloudAlarmConfigService;
import com.hnkylin.cloud.core.service.CloudAlarmLogService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import com.hnkylin.cloud.core.service.CloudZoneService;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.*;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.monitor.*;
import com.hnkylin.cloud.manage.entity.resp.monitor.KcpAlarmSettingsResp;
import com.hnkylin.cloud.manage.entity.resp.monitor.PageKcpAlarmLogResp;
import com.hnkylin.cloud.manage.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MonitorServiceImpl implements MonitorService {

    @Resource
    private McHttpService mcHttpService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private ClusterService clusterService;

    @Resource
    private McClusterThreadService mcClusterThreadService;

    @Resource
    private CloudAlarmConfigService cloudAlarmConfigService;

    @Resource
    private CloudAlarmLogService cloudAlarmLogService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private CloudZoneService cloudZoneService;

    @Resource
    private OrgService orgService;


    @Resource
    private VdcService vdcService;


    @Override
    public PhysicalHostEventTypeResp serverEventTypeList(BaseClusterParam clusterParam, LoginUserVo loginUserVo) {


        MCResponseData<Object> eventTypeResponse = mcHttpService.hasDataCommonMcRequest(clusterParam.getClusterId(),
                null, mcConfigProperties.getServerEventsFilterUrl(), loginUserVo.getUserName(), 0);
        PhysicalHostEventTypeResp eventTypeResp = new PhysicalHostEventTypeResp();
        if (Objects.nonNull(eventTypeResponse) && Objects.equals(MCServerVmConstants.SUCCESS,
                eventTypeResponse.getStatus())) {
            eventTypeResp = JSON.parseObject(JSON.toJSONString(eventTypeResponse.getData()),
                    PhysicalHostEventTypeResp.class);

        }
        return eventTypeResp;
    }

    @Override
    public PageData<PhysicalHostEventResp> pagePhysicalHostEvent(PhysicalHostEventParam physicalHostEventParam,
                                                                 LoginUserVo loginUserVo) {
        McServerEventParamReq mcServerEventParamReq = new McServerEventParamReq();
        BeanUtils.copyProperties(physicalHostEventParam, mcServerEventParamReq);
        mcServerEventParamReq.setPage(physicalHostEventParam.getPageNo());
        mcServerEventParamReq.setRows(physicalHostEventParam.getPageSize());
        if (StringUtils.isBlank(mcServerEventParamReq.getSord())) {
            mcServerEventParamReq.setSord("desc");
        }
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(physicalHostEventParam.getClusterId(), mcServerEventParamReq,
                        mcConfigProperties.getPagePhysicalHostEventUrl(), loginUserVo.getUserName(), 0);
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {

            McPageResp<PhysicalHostEventResp> mcPageResp =
                    JSONObject.parseObject(JSON.toJSONString(mcResponse.getData()), new
                            TypeReference<McPageResp<PhysicalHostEventResp>>() {
                            });

            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(mcPageResp.getPager());
            mcPageInfo.setPageSize(mcPageResp.getRows().size());
            mcPageInfo.setRecords(mcPageResp.getRecords());
            mcPageInfo.setTotal(mcPageResp.getTotal());
            return new PageData(mcPageInfo, mcPageResp.getRows());
        }
        return new PageData(null);
    }

    @Override
    public AlarmLogTypeResp alarmLogFilterList(BaseClusterParam clusterParam, LoginUserVo loginUserVo) {


        MCResponseData<Object> eventTypeResponse = mcHttpService.hasDataCommonMcRequest(clusterParam.getClusterId(),
                null, mcConfigProperties.getAlarmLogFilterUrl(), loginUserVo.getUserName(), 0);
        AlarmLogTypeResp alarmLogTypeResp = new AlarmLogTypeResp();
        if (Objects.nonNull(eventTypeResponse) && Objects.equals(MCServerVmConstants.SUCCESS,
                eventTypeResponse.getStatus())) {
            alarmLogTypeResp = JSON.parseObject(JSON.toJSONString(eventTypeResponse.getData()),
                    AlarmLogTypeResp.class);

        }
        return alarmLogTypeResp;
    }

    @Override
    public PageData<AlarmLogResp> pageAlarmLog(AlarmLogParam alarmLogParam,
                                               LoginUserVo loginUserVo) {
        McAlarmLogParamReq mcAlarmLogParamReq = new McAlarmLogParamReq();
        BeanUtils.copyProperties(alarmLogParam, mcAlarmLogParamReq);
        mcAlarmLogParamReq.setPage(alarmLogParam.getPageNo());
        mcAlarmLogParamReq.setRows(alarmLogParam.getPageSize());

        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(alarmLogParam.getClusterId(), mcAlarmLogParamReq,
                        mcConfigProperties.getPageAlarmLogUrl(), loginUserVo.getUserName(), 0);
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {

            McPageResp<AlarmLogResp> mcPageResp = JSONObject.parseObject(JSON.toJSONString(mcResponse.getData()), new
                    TypeReference<McPageResp<AlarmLogResp>>() {
                    });

            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(mcPageResp.getPager());
            mcPageInfo.setPageSize(mcPageResp.getRows().size());
            mcPageInfo.setRecords(mcPageResp.getRecords());
            mcPageInfo.setTotal(mcPageResp.getTotal());

            ignoreNotifications(loginUserVo, alarmLogParam.getClusterId());
            return new PageData(mcPageInfo, mcPageResp.getRows());
        }

        return new PageData(null);
    }

    private void ignoreNotifications(LoginUserVo loginUserVo, Integer clusterId) {

        new Thread(() -> { //Lambda表达
            mcHttpService.hasDataCommonMcRequest(clusterId,
                    null, mcConfigProperties.getIgnoreNotificationsUrl(), loginUserVo.getUserName(), 0);
        }).start();

    }

    @Override
    public ServerVirtualizationEventTypeResp serverVirtualizationEventsFilter(BaseClusterParam clusterParam,
                                                                              LoginUserVo loginUserVo) {


        MCResponseData<Object> eventTypeResponse = mcHttpService.hasDataCommonMcRequest(clusterParam.getClusterId(),
                null, mcConfigProperties.getServerVirtualizationEventsFilterUrl(), loginUserVo.getUserName(), 0);
        ServerVirtualizationEventTypeResp serverVirtualizationEventTypeResp = new ServerVirtualizationEventTypeResp();
        if (Objects.nonNull(eventTypeResponse) && Objects.equals(MCServerVmConstants.SUCCESS,
                eventTypeResponse.getStatus())) {
            serverVirtualizationEventTypeResp = JSON.parseObject(JSON.toJSONString(eventTypeResponse.getData()),
                    ServerVirtualizationEventTypeResp.class);

        }
        return serverVirtualizationEventTypeResp;
    }


    @Override
    public PageData<ServerVirtualizationEventResp> pageServerVirtualizationEvent(ServerVirtualizationEventParam serverVirtualizationEventParam,
                                                                                 LoginUserVo loginUserVo) {
        McServerVirtualizationEventParamReq mcServerVirtualizationEventParamReq =
                new McServerVirtualizationEventParamReq();
        BeanUtils.copyProperties(serverVirtualizationEventParam, mcServerVirtualizationEventParamReq);
        mcServerVirtualizationEventParamReq.setPage(serverVirtualizationEventParam.getPageNo());
        mcServerVirtualizationEventParamReq.setRows(serverVirtualizationEventParam.getPageSize());

        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(serverVirtualizationEventParam.getClusterId(),
                        mcServerVirtualizationEventParamReq,
                        mcConfigProperties.getPageServerVirtualizationEventUrl(), loginUserVo.getUserName(), 0);
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {

            McPageResp<ServerVirtualizationEventResp> mcPageResp =
                    JSONObject.parseObject(JSON.toJSONString(mcResponse.getData()), new
                            TypeReference<McPageResp<ServerVirtualizationEventResp>>() {
                            });

            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(mcPageResp.getPager());
            mcPageInfo.setPageSize(mcPageResp.getRows().size());
            mcPageInfo.setRecords(mcPageResp.getRecords());
            mcPageInfo.setTotal(mcPageResp.getTotal());
            return new PageData(mcPageInfo, mcPageResp.getRows());
        }
        return new PageData(null);
    }

    @Override
    public List<AlarmSettingsResp> listAlarmSettings(QueryMcAlarmSettingsParam queryMcAlarmSettingsParam,
                                                     LoginUserVo loginUserVo) {
        //调用mc获取响应
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(queryMcAlarmSettingsParam.getClusterId(),
                        queryMcAlarmSettingsParam, mcConfigProperties.getListAlarmSettingsUrl(),
                        loginUserVo.getUserName(), 0);
        List<AlarmSettingsResp> alarmSettingsList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            alarmSettingsList = JSON.parseArray(JSONArray.toJSONString(mcResponse.getData()), AlarmSettingsResp.class);
        }
        return alarmSettingsList;
    }

    @Override
    public boolean updateAlarmSettings(McUpdateAlarmSettingsParam updateAlarmSettingsParam, LoginUserVo loginUserVo) {

        return mcHttpService.noDataCommonMcRequest(updateAlarmSettingsParam.getClusterId(), updateAlarmSettingsParam,
                mcConfigProperties.getUpdateAlarmSettingsUrl(), loginUserVo.getUserName(), 0);
    }

    @Override
    public AlarmIntervalResp getAlarmInterval(BaseClusterParam clusterParam, LoginUserVo loginUserVo) {
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterParam.getClusterId(),
                null, mcConfigProperties.getGetAlarmIntervalUrl(), loginUserVo.getUserName(), 0);
        AlarmIntervalResp alarmIntervalResp = new AlarmIntervalResp();
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            alarmIntervalResp = JSON.parseObject(JSON.toJSONString(mcResponse.getData()),
                    AlarmIntervalResp.class);
        }
        return alarmIntervalResp;
    }

    @Override
    public boolean updateAlarmInterval(McUpdateAlarmIntervalParam updateAlarmIntervalParam, LoginUserVo loginUserVo) {
        return mcHttpService.noDataCommonMcRequest(updateAlarmIntervalParam.getClusterId(), updateAlarmIntervalParam,
                mcConfigProperties.getUpdateAlarmIntervalUrl(), loginUserVo.getUserName(), 0);
    }


    @Override
    public AlarmNotificationsResp alarmNotifications(LoginUserVo loginUserVo) {
        AlarmNotificationsResp alarmNotificationsResp = new AlarmNotificationsResp();
        List<CloudClusterDo> clusterDoList = clusterService.visibleClusterByUserId(loginUserVo.getUserId());
        if (!clusterDoList.isEmpty()) {
            if (Objects.equals(clusterDoList.size(), 1)) {
                MCResponseData<Object> mcResponse =
                        mcHttpService.hasDataCommonMcRequest(clusterDoList.get(0).getId(),
                                null, mcConfigProperties.getAlarmNotificationsUrl(), loginUserVo.getUserName(), 0);
                AlarmNotificationsResp mcAlarmNotificationsResp = new AlarmNotificationsResp();
                if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS,
                        mcResponse.getStatus())) {
                    mcAlarmNotificationsResp = JSON.parseObject(JSON.toJSONString(mcResponse.getData()),
                            AlarmNotificationsResp.class);
                    return mcAlarmNotificationsResp;
                }
            } else {
                List<String> resultList =
                        mcClusterThreadService.threadGetMcResponse(clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList()),
                                loginUserVo.getUserName(), mcConfigProperties.getAlarmNotificationsUrl(),
                                new ArrayList<>());
                List<AlarmNotificationsResp> mcAlarmNoticeList = new ArrayList<>();
                Stream.iterate(0, i -> i + 1).limit(clusterDoList.size()).forEach(i -> {
                    String mcAlarmNotificationsRespStr = resultList.get(i);
                    if (Objects.nonNull(mcAlarmNotificationsRespStr)) {
                        AlarmNotificationsResp mcAlarmNotificationsResp = JSON.parseObject(mcAlarmNotificationsRespStr,
                                AlarmNotificationsResp.class);
                        mcAlarmNoticeList.add(mcAlarmNotificationsResp);
                    }
                });
                Integer notificationsCount =
                        mcAlarmNoticeList.stream().mapToInt(AlarmNotificationsResp::getNotificationsCount).sum();
                alarmNotificationsResp.setNotificationsCount(notificationsCount);
            }

        }
        //获取当天存VDC-资源告警情况
        int todayKcpNotice = getTodayKcpAlarmCount(loginUserVo);
        alarmNotificationsResp.setNotificationsCount(alarmNotificationsResp.getNotificationsCount() + todayKcpNotice);
        return alarmNotificationsResp;
    }


    /**
     * 获取当天VDC-资源告警数量
     *
     * @param loginUserVo
     * @return
     */
    private Integer getTodayKcpAlarmCount(LoginUserVo loginUserVo) {
        CloudAlarmLogDo queryAlarmLogDo = new CloudAlarmLogDo();
        QueryWrapper queryWrapper = new QueryWrapper(queryAlarmLogDo);

        String today = DateUtils.format(new Date(), DateUtils.DATE_YYYY_MM_DD);
        queryWrapper.ge("create_time", DateUtils.parse(today + DateUtils.DAY_START,
                DateUtils.DATE_ALL_PATTEN));

        List<CloudVdcDo> cloudVdcDoList = vdcService.visibleVdcListByUserId(loginUserVo.getUserId());
        if (cloudVdcDoList.isEmpty()) {
            return 0;
        }
        queryWrapper.in("alarm_target_id", cloudVdcDoList.stream().map(CloudVdcDo::getId).collect(Collectors.toList()));

        return cloudAlarmLogService.count(queryWrapper);
    }


    @Override
    public List<KcpAlarmSettingsResp> listKcpAlarmSettings(LoginUserVo loginUserVo) {

        CloudAlarmConfigDo cloudAlarmConfigDo = new CloudAlarmConfigDo();
        cloudAlarmConfigDo.setDeleteFlag(false);
        QueryWrapper queryWrapper = new QueryWrapper(cloudAlarmConfigDo);
        List<CloudAlarmConfigDo> alarmConfigDoList = cloudAlarmConfigService.list(queryWrapper);
        List<KcpAlarmSettingsResp> kcpAlarmSettingsList = new ArrayList<>();
        alarmConfigDoList.forEach(item -> {
            KcpAlarmSettingsResp kcpAlarmSettingsResp = new KcpAlarmSettingsResp();
            BeanUtils.copyProperties(item, kcpAlarmSettingsResp);
            kcpAlarmSettingsResp.setKcpSettingId(item.getId());
            kcpAlarmSettingsList.add(kcpAlarmSettingsResp);
        });
        return kcpAlarmSettingsList;
    }

    @Override
    public void updateKcpAlarmSetting(UpdateKcpAlarmSettingParam updateAlarmIntervalParam, LoginUserVo loginUserVo) {
        CloudAlarmConfigDo cloudAlarmConfigDo =
                cloudAlarmConfigService.getById(updateAlarmIntervalParam.getKcpSettingId());
        cloudAlarmConfigDo.setGeneralAlarm(updateAlarmIntervalParam.getGeneralAlarm());
        cloudAlarmConfigDo.setSeverityAlarm(updateAlarmIntervalParam.getSeverityAlarm());
        cloudAlarmConfigDo.setUrgentAlarm(updateAlarmIntervalParam.getUrgentAlarm());
        //cloudAlarmConfigDo.setDurationTime(updateAlarmIntervalParam.getDurationTime());
        cloudAlarmConfigDo.setUpdateBy(loginUserVo.getUserId());
        cloudAlarmConfigDo.setUpdateTime(new Date());
        cloudAlarmConfigService.updateById(cloudAlarmConfigDo);
    }


    @Override
    public PageData<PageKcpAlarmLogResp> pageKcpAlarmLog(PagKcpAlarmLogParam kcpAlarmLogParam,
                                                         LoginUserVo loginUserVo) {

        CloudAlarmLogDo queryAlarmLogDo = new CloudAlarmLogDo();
        QueryWrapper queryWrapper = new QueryWrapper(queryAlarmLogDo);
        if (StringUtils.isNotBlank(kcpAlarmLogParam.getStartDate())) {
            queryWrapper.ge("create_time", DateUtils.parse(kcpAlarmLogParam.getStartDate() + DateUtils.DAY_START,
                    DateUtils.DATE_ALL_PATTEN));
        }
        if (StringUtils.isNotBlank(kcpAlarmLogParam.getEndDate())) {
            queryWrapper.le("create_time", DateUtils.parse(kcpAlarmLogParam.getEndDate() + DateUtils.DAY_END,
                    DateUtils.DATE_ALL_PATTEN));
        }

        if (Objects.nonNull(kcpAlarmLogParam.getZoneId()) && kcpAlarmLogParam.getZoneId() > 0) {

            List<CloudVdcDo> cloudVdcDoList = vdcService.visibleVdcListByUserIdAndZoneId(loginUserVo.getUserId(),
                    kcpAlarmLogParam.getZoneId());
            if (cloudVdcDoList.isEmpty()) {
                return new PageData(null);
            }
            queryWrapper.in("alarm_target_id",
                    cloudVdcDoList.stream().map(CloudVdcDo::getId).collect(Collectors.toList()));
        }
        if (Objects.nonNull(kcpAlarmLogParam.getOrgId()) && kcpAlarmLogParam.getOrgId() > 0) {
            CloudVdcDo vdcDo = cloudVdcService.getVdcByOrgId(kcpAlarmLogParam.getOrgId());
            if (Objects.isNull(vdcDo)) {
                return new PageData(null);
            }
            queryWrapper.eq("alarm_target_id", vdcDo.getId());
        }

        if (Objects.equals(kcpAlarmLogParam.getZoneId(), 0) || Objects.equals(kcpAlarmLogParam.getOrgId(), 0)) {
            List<CloudVdcDo> cloudVdcDoList = vdcService.visibleVdcListByUserId(loginUserVo.getUserId());
            if (cloudVdcDoList.isEmpty()) {
                return new PageData(null);
            }
            queryWrapper.in("alarm_target_id",
                    cloudVdcDoList.stream().map(CloudVdcDo::getId).collect(Collectors.toList()));
        }
        queryWrapper.orderByDesc("id");
        PageHelper.startPage(kcpAlarmLogParam.getPageNo(), kcpAlarmLogParam.getPageSize());
        List<CloudAlarmLogDo> alarmLogDoList = cloudAlarmLogService.list(queryWrapper);
        PageInfo<CloudAlarmLogDo> pageInfo = new PageInfo<>(alarmLogDoList);
        List<PageKcpAlarmLogResp> kcpAlarmLogList = new ArrayList<>();
        alarmLogDoList.forEach(item -> {
            PageKcpAlarmLogResp kcpAlarmLog = new PageKcpAlarmLogResp();
            BeanUtils.copyProperties(item, kcpAlarmLog);
            CloudVdcDo vdcDo = cloudVdcService.getById(item.getAlarmTargetId());
            CloudZoneDo zoneDo = cloudZoneService.getById(vdcDo.getZoneId());
            kcpAlarmLog.setZoneName(zoneDo.getName());
            CloudOrganizationDo organizationDo = orgService.getOrgByVdcId(vdcDo.getId());
            kcpAlarmLog.setOrgName("---");
            if (Objects.nonNull(organizationDo)) {
                kcpAlarmLog.setOrgName(organizationDo.getOrganizationName());
            }
            kcpAlarmLog.setAlarmTime(DateUtils.format(item.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
            kcpAlarmLogList.add(kcpAlarmLog);
        });

        PageData pageData = new PageData(pageInfo);
        pageData.setList(kcpAlarmLogList);
        return pageData;
    }
}
