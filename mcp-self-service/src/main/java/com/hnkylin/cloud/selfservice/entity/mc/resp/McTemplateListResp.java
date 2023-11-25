package com.hnkylin.cloud.selfservice.entity.mc.resp;

import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;

import java.util.List;

/**
 * 模板列表
 * Created by kylin-ksvd on 21-7-14.
 */
@Data
public class McTemplateListResp {

    private Integer id;

    private String name;

    private String operatingSystem;

    private String architecture;

    private String systemType;

    //描述
    private String description;

    //cpu
    private Integer cpu;

    //内存
    private Integer mem;

    private MemUnit memUnit;

    private List<McNetworkResp> interfacesList;

    private List<McDiskResp> disks;


}
