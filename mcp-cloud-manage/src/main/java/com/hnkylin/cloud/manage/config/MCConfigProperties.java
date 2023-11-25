package com.hnkylin.cloud.manage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties("mc-config")
@Data
public class MCConfigProperties {


    private String mcPrefix;


    private String openPrefix;

    //mc请求统一地址
    private String commonPrefix;

    //创建云服务器
    private String createServerVmUrl;

    //基于ISO创建云服务器
    private String createServerVmByIsoUrl;

    //变更
    private String modifyServerVmUrl;

    //获取网络配置接口地址
    private String networkConfigUrl;

    //根据虚拟机交换机，获取端口组名称
    private String listPortGroupByNameUrl;

    //获取存储位置
    private String storageLocationsListUrl;

    //获取计算资源
    private String getRealServiceListUrl;

    //通过云服务器ID获取云服务器详情
    private String getServervmDetailByServevmIdUrl;


    //通过云服务器ID获取云服务器详情
    private String getServervmDetailByUuidUrl;


    //获取存储资源详情
    private String getDataStoreInfoByNameUrl;

    private String isoListUrl;

    private String checkVmServerNameUrl;


    //分页查询存储资源
    private String pageStorageUrl;

    //分页获取物理主机
    private String pagePhysicalHostUrl;

    //快捷登录
    private String mcQuickLogin;


    //获取集群基础资源
    private String mcClusterBaseResourceUrl;


    //获取模板列表
    private String serverVmList;

    //开机
    private String startServerVmUrl;
    //关闭云服务器
    private String shutdownServerVmUrl;
    //重启云服务器
    private String restartServerVmUrl;

    //强制关闭云服务器
    private String forcedShutdownServerVmUrl;
    //强制重启云服务器
    private String forcedRestartServerVmUrl;

    //批量开机
    private String batchStartServerVmUrl;
    //批量关机
    private String batchShutdownServerVmUrl;

    //批量重启
    private String batchRebootServerVmUrl;

    //批量把云服务器放入回收站中
    private String batchRemoveMachineToRecycleUrl;

    private String mcLogoLocalFilePath;

    private String logoPath;

    //vncUrl
    private String serverVmVncUrl;

    private String serverVmInfoUrl;
    private String serverVmSummaryUrl;
    private String serverVmAlarmEventUrl;
    private String serverVmNetworkUrl;
    private String serverVmDiskUrl;
    private String serverVmMonitorInfoUrl;
    private String serverVmOperateLogUrl;

    //物理主机事件类型查询
    private String serverEventsFilterUrl;
    //分页查询物理主机事件
    private String pagePhysicalHostEventUrl;

    //告警日志类型查询
    private String alarmLogFilterUrl;
    //分页查询告警日志
    private String pageAlarmLogUrl;

    //服务器虚拟化事件类型
    private String serverVirtualizationEventsFilterUrl;
    //分页查询服务器虚拟化事件
    private String pageServerVirtualizationEventUrl;

    //查询告警设置
    private String listAlarmSettingsUrl;

    //修改告警设置
    private String updateAlarmSettingsUrl;

    //获取告警持续时间
    private String getAlarmIntervalUrl;

    //修改告警持续时间
    private String updateAlarmIntervalUrl;


    //获取告警数量接口
    private String alarmNotificationsUrl;


    //校验账号密码
    private String checkClusterUserNameAndPasswordUrl;


    //获取计算资源绑定列表
    private String getClusterBindResourceListUrl;

    //获取云服务器占用已使用资源
    private String serverVmsUsedResourceUrl;

    //云服务器运行情况
    private String serverVmRunningInfoUrl;

    //已读告警
    private String ignoreNotificationsUrl;

}
