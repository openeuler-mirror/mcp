package com.hnkylin.cloud.selfservice.entity.resp;

import lombok.Data;

import java.util.List;

@Data
public class DeptRespDto {

    private Integer deptId;

    private String deptName;

    private Integer parentId;

    private List<DeptRespDto> childDepts;
}
