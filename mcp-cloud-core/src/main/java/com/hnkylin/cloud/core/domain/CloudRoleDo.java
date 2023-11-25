package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_role")
public class CloudRoleDo extends BaseDo {


    private String roleName;

    private RoleType roleType;

    private String remark;

    //是否是系统默认内置用户
    private Boolean defaultRole;
}
