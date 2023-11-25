package com.hnkylin.cloud.manage.ctrl;


import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.McUpdateAlarmIntervalParam;
import com.hnkylin.cloud.manage.entity.mc.req.McUpdateAlarmSettingsParam;
import com.hnkylin.cloud.manage.entity.mc.req.QueryMcAlarmSettingsParam;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.cluster.ClusterStoragePageParam;
import com.hnkylin.cloud.manage.entity.req.monitor.*;
import com.hnkylin.cloud.manage.entity.resp.monitor.KcpAlarmSettingsResp;
import com.hnkylin.cloud.manage.entity.resp.monitor.PageKcpAlarmLogResp;
import com.hnkylin.cloud.manage.service.MonitorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api/monitoring")
public class MonitoringCtrl {

    @Resource
    private MonitorService monitorService;

    @PostMapping("/serverEventTypeList")
    @ParamCheck
    public BaseResult<PhysicalHostEventTypeResp> serverEventTypeList(@ModelCheck(notNull = true) @RequestBody BaseClusterParam clusterParam,
                                                                     @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.serverEventTypeList(clusterParam, loginUserVo));

    }

    @PostMapping("/pagePhysicalHostEvent")
    @ParamCheck
    public BaseResult<PageData<PhysicalHostEventResp>> pagePhysicalHostEvent(@ModelCheck(notNull = true) @RequestBody PhysicalHostEventParam physicalHostEventParam,
                                                                             @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.pagePhysicalHostEvent(physicalHostEventParam, loginUserVo));

    }

    @PostMapping("/alarmLogFilterList")
    @ParamCheck
    public BaseResult<AlarmLogTypeResp> alarmLogFilterList(@ModelCheck(notNull = true) @RequestBody BaseClusterParam clusterParam,
                                                           @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.alarmLogFilterList(clusterParam, loginUserVo));

    }

    @PostMapping("/pageAlarmLog")
    @ParamCheck
    public BaseResult<PageData<AlarmLogResp>> pageAlarmLog(@ModelCheck(notNull = true) @RequestBody AlarmLogParam alarmLogParam,
                                                           @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.pageAlarmLog(alarmLogParam, loginUserVo));

    }

    @PostMapping("/serverVirtualizationEventsFilter")
    @ParamCheck
    public BaseResult<ServerVirtualizationEventTypeResp> serverVirtualizationEventsFilter(@ModelCheck(notNull = true) @RequestBody BaseClusterParam clusterParam,
                                                                                          @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.serverVirtualizationEventsFilter(clusterParam, loginUserVo));

    }

    @PostMapping("/pageServerVirtualizationEvent")
    @ParamCheck
    public BaseResult<PageData<ServerVirtualizationEventResp>> pageServerVirtualizationEvent(@ModelCheck(notNull =
            true) @RequestBody ServerVirtualizationEventParam serverVirtualizationEventParam,
                                                                                             @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.pageServerVirtualizationEvent(serverVirtualizationEventParam,
                loginUserVo));

    }


    @PostMapping("/listAlarmSettings")
    @ParamCheck
    public BaseResult<List<AlarmSettingsResp>> listAlarmSettings(@ModelCheck(notNull = true) @RequestBody QueryMcAlarmSettingsParam queryMcAlarmSettingsParam,
                                                                 @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.listAlarmSettings(queryMcAlarmSettingsParam, loginUserVo));

    }

    @PostMapping("/updateAlarmSettings")
    @ParamCheck
    public BaseResult<String> updateAlarmSettings(@ModelCheck(notNull = true) @RequestBody McUpdateAlarmSettingsParam updateAlarmSettingsParam,
                                                  @LoginUser LoginUserVo loginUserVo) {

        boolean result = monitorService.updateAlarmSettings(updateAlarmSettingsParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);
        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);

    }

    @PostMapping("/getAlarmInterval")
    @ParamCheck
    public BaseResult<AlarmIntervalResp> getAlarmInterval(@ModelCheck(notNull = true) @RequestBody BaseClusterParam clusterParam,
                                                          @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.getAlarmInterval(clusterParam, loginUserVo));

    }

    @PostMapping("/updateAlarmInterval")
    @ParamCheck
    public BaseResult<String> updateAlarmInterval(@ModelCheck(notNull = true) @RequestBody McUpdateAlarmIntervalParam updateAlarmIntervalParam,
                                                  @LoginUser LoginUserVo loginUserVo) {

        boolean result = monitorService.updateAlarmInterval(updateAlarmIntervalParam, loginUserVo);
        if (result) {
            return BaseResult.success(null);
        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);

    }

    @PostMapping("/alarmNotifications")
    public BaseResult<AlarmNotificationsResp> alarmNotifications(@LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(monitorService.alarmNotifications(loginUserVo));

    }


    @PostMapping("/listKcpAlarmSettings")
    @ParamCheck
    public BaseResult<List<KcpAlarmSettingsResp>> listKcpAlarmSettings(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.listKcpAlarmSettings(loginUserVo));

    }

    @PostMapping("/updateKcpAlarmSetting")
    @ParamCheck
    public BaseResult<String> updateKcpAlarmSetting(@ModelCheck(notNull = true) @RequestBody UpdateKcpAlarmSettingParam updateAlarmIntervalParam,
                                                    @LoginUser LoginUserVo loginUserVo) {

        monitorService.updateKcpAlarmSetting(updateAlarmIntervalParam, loginUserVo);

        return BaseResult.success(null);

    }

    @PostMapping("/pageKcpAlarmLog")
    @ParamCheck
    public BaseResult<PageData<PageKcpAlarmLogResp>> pageKcpAlarmLog(@ModelCheck(notNull = true) @RequestBody
                                                                             PagKcpAlarmLogParam kcpAlarmLogParam,
                                                                     @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(monitorService.pageKcpAlarmLog(kcpAlarmLogParam, loginUserVo));

    }

}
