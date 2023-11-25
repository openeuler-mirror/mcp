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
@TableName("cloud_permission")
public class CloudPermissionDo extends BaseDo {


    private Integer parentId;

    private String name;

    private String icon;

    private String routeKey;

    private Boolean platformRolePermission;

}
