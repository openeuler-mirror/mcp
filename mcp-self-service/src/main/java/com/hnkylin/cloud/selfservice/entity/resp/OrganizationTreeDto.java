package com.hnkylin.cloud.selfservice.entity.resp;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationTreeDto {

    private Integer organizationId;

    private String organizationName;

    private Integer parentId;

    List<OrganizationTreeDto> children;

}
