package com.hnkylin.cloud.manage.entity.resp.user;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoRespDto {

    private String userName;

    private String realName;

    private String organizationName;

    private Integer organizationId;

    private String mobile;

    private String remark;

    private List<UserPermissionDto> permissions;

    private boolean topUser;

    private Integer userId;

    private boolean superUser;


}
