package com.hnkylin.cloud.manage.entity.resp.vdc;


import com.hnkylin.cloud.manage.entity.resp.network.NetworkDetailDto;
import lombok.Data;

import java.util.List;

/**
 * 编辑VDC时-VDC详情
 */
@Data
public class ModifyVdcDetailRespDto {

    private Integer vdcId;

    private String vdcName;

    private Integer parentId;

    private String parentName;


    private Integer zoneId;


    private String zoneName;


    private List<NetworkDetailDto> networkList;

}
