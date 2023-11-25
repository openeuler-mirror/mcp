package com.hnkylin.cloud.manage.entity.resp.role;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RolePermissionTreeDto {

    private Integer permissionId;

    private String name;

    private String icon;

    private String routeKey;

    private Integer parentId;


    private List<RolePermissionTreeDto> child;

}


