package com.hnkylin.cloud.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties("kcpha")
@Data
public class KcpHaProperties {


    private String role;

    //获取备kcp状态
    private String slaveKcpInfo;

    private String checkPassword;

    private String resetSlave;

    //配置文件路径
    private String configPath;

    private String initSlave;

    //初始化从节点脚本
    private String initSlaveShell;

    //主节点变成从节点脚本
    private String masterToSlaveShell;

    //从节点变成主节点脚本
    private String slaveToMasterShell;

    //重置备节点脚本
    private String resetSlaveShell;

}
