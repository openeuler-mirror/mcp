package com.hnkylin.cloud.core.common.servervm;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kylin-ksvd on 21-8-11.
 */
@Data
public class ServerVmMonitorInfoRespDto {

    private List<String> timeList;

    private List<BigDecimal> cpuUsed;

    private List<BigDecimal> memUsed;

    private List<Integer> diskReadSpeed;

    private List<Integer> diskWriteSpeed;

    private List<Integer> netWorkInSpeed;

    private List<Integer> netWorkOutSpeed;
}
