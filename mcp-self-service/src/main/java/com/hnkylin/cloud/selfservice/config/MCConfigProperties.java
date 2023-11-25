package com.hnkylin.cloud.selfservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties("mc-config")
@Data
public class MCConfigProperties {


    private String mcLogoLocalFilePath;


    private String mcPrefix;

    //mc请求统一地址
    private String commonPrefix;

    //获取模板列表
    private String templateListUrl;

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


    //虚拟机详情概要接口
    private String serverVmInfoUrl;

    //云服务器监控接口
    private String serverVmMonitorInfoUrl;

    //云服务器操作日志接口
    private String serverVmOperateLogUrl;

    //云服务器-创建快照
    private String serverVmCreateSnapshotUrl;

    //云服务器-更新快照
    private String serverVmUpdateSnapshotUrl;

    //云服务器-删除快照
    private String serverVmDeleteSnapshotUrl;

    //云服务器-应用快照
    private String serverVmApplySnapshotUrl;

    //云服务器-快照列表
    private String serverVmSnapshotListUrl;

    //vncUrl
    private String serverVmVncUrl;

    //获取云服务器状态接口
    private String serverVmStatusUrl;

    //更新云服务器描述
    private String updateMachineDescriptionUrl;


    //重置控制台密码
    private String resetRemotePasswordUrl;

    private String getServervmDetailByServevmIdUrl;

    private String logoPath;

    private String isoListUrl;

    private String allOperatingSystemUrl;

    //修改云服务器名称
    private String updateMachineNameUrl;

    //检查云服务器名称是否重复
    private String checkVmServerNameUrl;

}
