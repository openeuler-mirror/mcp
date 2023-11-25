package com.hnkylin.cloud.core.common.servervm;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 云服务器详情-健康信息
 * Created by kylin-ksvd on 21-7-14.
 */
@Data
public class McServerVmMonitorDetailResp {


    private BigDecimal cpuUtil;

    private String dateShow;

    private Integer diskReadSpeed;

    private Integer diskWriteSpeed;

    private BigDecimal memUtil;

    private Integer netWorkInSpeed;

    private Integer netWorkOutSpeed;


}

