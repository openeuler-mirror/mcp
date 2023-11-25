package com.hnkylin.cloud.manage.entity.resp.role;

import com.hnkylin.cloud.manage.entity.resp.org.CommonOrgTreeRespDto;
import lombok.Data;

import java.util.List;

@Data
public class OrgStatisticTreeDto extends CommonOrgTreeRespDto {

    //统计数量
    private Integer statisticNum;

    //子组织列表
    private List<OrgStatisticTreeDto> children;
}
