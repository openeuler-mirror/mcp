package com.hnkylin.cloud.selfservice.entity;

import lombok.Data;

@Data
public class LoginUserVo {

    private String userName;

    private Integer userId;

    private String token;
}
