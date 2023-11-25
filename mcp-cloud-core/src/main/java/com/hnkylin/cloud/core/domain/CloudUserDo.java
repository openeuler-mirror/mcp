package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.UserType;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("cloud_user")
public class CloudUserDo extends BaseDo {

    private String userName;

    private String realName;

    private String password;

    private String mobile;

    private String remark;

    private CloudUserStatus status;

    private Integer organizationId;

    private UserType userType;

    //是否超级管理员
    private Boolean superUser;

    //是否是系统默认内置用户
    private Boolean defaultUser;
}
