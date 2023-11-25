package com.hnkylin.cloud.manage.entity.resp.serverVm;

import com.hnkylin.cloud.manage.enums.ZoneOrgUserType;
import lombok.Data;

import java.util.List;
import java.util.Objects;


/**
 * 服务器模块-可用区组织数
 */
@Data
public class ServerVmZoneOrgTreeRespDto {

    private Integer uniqueId;

    private String name;

    private Integer serverVmCount;

    private ZoneOrgUserType type;

    private String treeUniqueId;

    private List<ServerVmZoneOrgTreeRespDto> child;


    public void countServerVmCount() {
        if (Objects.nonNull(child) && !child.isEmpty()) {
            int total = child.stream().mapToInt(ServerVmZoneOrgTreeRespDto::getServerVmCount).sum();
            this.serverVmCount = total;
        } else {
            this.serverVmCount = 0;
        }

    }
}
