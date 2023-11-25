package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_servervm_network")
public class CloudWorkOrderServerVmNetworkDo extends BaseDo {

    //申请ID
    private Integer workOrderId;

    //用途
    private String purpose;


    private ApplyMcServerVmType type;

    private Long interfaceId;

    private ModifyType modifyType;

    private Boolean ipBindMac;

    private Boolean manualSetIp;

    private Boolean automaticAcqIp;

    private String ip;
    private String mask;

    private String gw;
    private String dns1;
    private String dns2;


}
