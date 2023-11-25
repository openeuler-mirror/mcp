package com.hnkylin.cloud.manage.entity.mc.resp;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class McServerVmRunningInfoDto {

    private Integer onlineMachine = 0;

    private Integer offlineMachine = 0;

    private Integer totalMachine = 0;

    private List<McServerVmUseRatioData> cpuTopList = new ArrayList<>();

    private List<McServerVmUseRatioData> memTopList = new ArrayList<>();


}
