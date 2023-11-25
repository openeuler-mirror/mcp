package com.hnkylin.cloud.manage.entity.req.network;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import lombok.Data;

@Data
public class CreateNetworkParam {

    @FieldCheck(notNull = true, notNullMessage = "网络名称不能为空")
    private String networkName;

    private String interfaceType;


    @FieldCheck(notNull = true, notNullMessage = "地址池不能为空")
    private String addressPool;


    private Integer addressPoolId;

    @FieldCheck(notNull = true, notNullMessage = "虚拟交换机不能为空")
    private String virtualSwitch;

    @FieldCheck(notNull = true, notNullMessage = "网卡类型不能为空")
    private String modelType;


    @FieldCheck(notNull = true, notNullMessage = "端口组不能为空")
    private String portGroup;

    private String portGroupUuid;

    private String securityGroup;

    private String securityGroupUuid;

}
