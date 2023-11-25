package com.hnkylin.cloud.manage.entity.resp.workorder;


import com.hnkylin.cloud.manage.entity.resp.workorder.BaseWorkOrderDetailDto;
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

    //部门名称
    private String deptName;

    //手机号
    private String mobile;


}
