package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.McUpdateAlarmIntervalParam;
import com.hnkylin.cloud.manage.entity.mc.req.McUpdateAlarmSettingsParam;
import com.hnkylin.cloud.manage.entity.mc.req.QueryMcAlarmSettingsParam;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.monitor.*;
import com.hnkylin.cloud.manage.entity.resp.monitor.KcpAlarmSettingsResp;
import com.hnkylin.cloud.manage.entity.resp.monitor.PageKcpAlarmLogResp;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
public interface MonitorService {

    /**
     * 物理主机事件类型查询
     *
     * @param clusterParam
     * @param loginUserVo
     * @return
     */
    PhysicalHostEventTypeResp serverEventTypeList(BaseClusterParam clusterParam, LoginUserVo loginUserVo);

    /**
     * 分页查询物理主机事件
     *
     * @param physicalHostEventParam
     * @param loginUserVo
     * @return
     */
    PageData<PhysicalHostEventResp> pagePhysicalHostEvent(PhysicalHostEventParam physicalHostEventParam,
                                                          LoginUserVo loginUserVo);

    /**
     * 告警日志类型查询
     *
     * @param clusterParam
     * @param loginUserVo
     * @return
     */
    AlarmLogTypeResp alarmLogFilterList(BaseClusterParam clusterParam, LoginUserVo loginUserVo);


    /**
     * 分页查询告警日志
     *
     * @param alarmLogParam
     * @param loginUserVo
     * @return
     */
    PageData<AlarmLogResp> pageAlarmLog(AlarmLogParam alarmLogParam, LoginUserVo loginUserVo);


    /**
     * 服务器虚拟化事件类型
     *
     * @param clusterParam
     * @param loginUserVo
     * @return
     */
    ServerVirtualizationEventTypeResp serverVirtualizationEventsFilter(BaseClusterParam clusterParam,
                                                                       LoginUserVo loginUserVo);


    /**
     * 分页查询告警日志
     *
     * @param serverVirtualizationEventParam
     * @param loginUserVo
     * @return
     */
    PageData<ServerVirtualizationEventResp> pageServerVirtualizationEvent(ServerVirtualizationEventParam serverVirtualizationEventParam, LoginUserVo loginUserVo);


    /**
     * 查询告警设置
     *
     * @param queryMcAlarmSettingsParam
     * @param loginUserVo
     * @return
     */
    List<AlarmSettingsResp> listAlarmSettings(QueryMcAlarmSettingsParam queryMcAlarmSettingsParam,
                                              LoginUserVo loginUserVo);

    /**
     * 修改mc告警设置接口
     *
     * @param updateAlarmSettingsParam
     * @param loginUserVo
     * @return
     */
    boolean updateAlarmSettings(McUpdateAlarmSettingsParam updateAlarmSettingsParam, LoginUserVo loginUserVo);

    /**
     * 获取告警持续时间
     *
     * @param clusterParam
     * @param loginUserVo
     * @return
     */
    AlarmIntervalResp getAlarmInterval(BaseClusterParam clusterParam, LoginUserVo loginUserVo);

    /**
     * 修改告警持续时间
     *
     * @param updateAlarmIntervalParam
     * @param loginUserVo
     * @return
     */
    boolean updateAlarmInterval(McUpdateAlarmIntervalParam updateAlarmIntervalParam, LoginUserVo loginUserVo);

    /**
     * 根据登录用户获取告警数量
     *
     * @param loginUserVo
     * @return
     */
    AlarmNotificationsResp alarmNotifications(LoginUserVo loginUserVo);


    /**
     * 查询平台告警日志
     *
     * @param loginUserVo
     * @return
     */
    List<KcpAlarmSettingsResp> listKcpAlarmSettings(LoginUserVo loginUserVo);

    /**
     * 修改平台告警设置
     *
     * @param updateAlarmIntervalParam
     * @param loginUserVo
     */
    void updateKcpAlarmSetting(UpdateKcpAlarmSettingParam updateAlarmIntervalParam, LoginUserVo loginUserVo);


    /**
     * 分页查询kcp平台告警
     *
     * @param kcpAlarmLogParam
     * @param loginUserVo
     * @return
     */
    PageData<PageKcpAlarmLogResp> pageKcpAlarmLog(PagKcpAlarmLogParam kcpAlarmLogParam, LoginUserVo loginUserVo);


}
