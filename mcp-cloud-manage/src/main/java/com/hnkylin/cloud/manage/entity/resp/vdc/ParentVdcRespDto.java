package com.hnkylin.cloud.manage.entity.resp.vdc;

import lombok.Data;

import java.util.List;

@Data
public class ParentVdcRespDto extends CommonVdcTreeRespDto {


    //子组织列表
    private List<ParentVdcRespDto> children;
}
