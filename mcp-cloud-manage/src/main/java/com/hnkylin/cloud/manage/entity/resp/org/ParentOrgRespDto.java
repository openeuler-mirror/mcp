package com.hnkylin.cloud.manage.entity.resp.org;

import lombok.Data;

import java.util.List;

@Data
public class ParentOrgRespDto extends CommonOrgTreeRespDto {


    //子组织列表
    private List<ParentOrgRespDto> children;
}
