package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

/**
 * 模板中网络信息
 * Created by kylin-ksvd on 21-7-14.
 */
@Data
public class McNetworkResp {
    private String interfaceType;
    private String portGroup;
    private String virtualSwitch;
    private Integer id;
}
