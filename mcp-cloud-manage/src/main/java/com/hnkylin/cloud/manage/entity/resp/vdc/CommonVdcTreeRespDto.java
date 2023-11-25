package com.hnkylin.cloud.manage.entity.resp.vdc;


import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

/**
 * 通用vdc-树形结构对象
 */
@Data
public class CommonVdcTreeRespDto {

    private Integer vdcId;

    private String vdcName;

    private Integer parentId;

    private String parentName;

    private String remark;

    private Integer vcpu;

    private Integer mem;

    private MemUnit memUnit;

    private Integer storage;

    private StorageUnit storageUnit;

}
