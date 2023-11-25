package com.hnkylin.cloud.manage.entity.resp.org;


import lombok.Data;

@Data
public class CommonOrgTreeRespDto {

    private Integer organizationId;

    private String organizationName;

    private Integer parentId;

    private String parentName;

    private String remark;

    private Boolean topOrg = false;


}
