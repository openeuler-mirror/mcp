package com.hnkylin.cloud.manage.entity.mc.resp;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 集群管理-资源使用情况
 */
@Data
public class McClusterServerResourceResp {


    private String serverIp;
    private String serverUuid;

    private Integer vcpus;

    private BigDecimal memory;

    private ArchitectureType architecture;

}
