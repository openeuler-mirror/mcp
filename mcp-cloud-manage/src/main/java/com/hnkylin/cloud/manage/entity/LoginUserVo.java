package com.hnkylin.cloud.manage.entity;

import lombok.Data;

@Data
public class LoginUserVo {

    private String userName;

    private Integer userId;

    private String token;

    private String clientIp;

}
