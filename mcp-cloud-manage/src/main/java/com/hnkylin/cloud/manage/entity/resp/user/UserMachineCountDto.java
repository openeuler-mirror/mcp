package com.hnkylin.cloud.manage.entity.resp.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMachineCountDto {

    private Integer userId;

    private Integer machineCount;

    private String realName;


}
