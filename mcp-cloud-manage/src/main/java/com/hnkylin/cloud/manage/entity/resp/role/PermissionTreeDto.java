package com.hnkylin.cloud.manage.entity.resp.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionTreeDto {

    private Integer permissionId;

    private String name;

    private String icon;

    private String routeKey;

    private Integer parentId;


    private List<PermissionTreeDto> children;
}
