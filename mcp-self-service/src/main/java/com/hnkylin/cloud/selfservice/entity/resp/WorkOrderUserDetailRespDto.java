package com.hnkylin.cloud.selfservice.entity.resp;


import lombok.Data;

@Data
public class WorkOrderUserDetailRespDto extends BaseWorkOrderDetailDto {


    //账号
    private String userName;

    //真实姓名
    private String realName;

    //新真实姓名
    private String newRealName;

    //组织名
    private String organizationName;


    //手机号
    private String mobile;


}
