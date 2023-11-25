package com.hnkylin.cloud.manage.entity.req.vdc;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.List;

/**
 * 创建VDC时检查是否能创建
 */
@Data
public class ModifyVdcParam {


    private Integer vdcId;


    @FieldCheck(notNull = true, notNullMessage = "上级VDC不能为空")
    private String vdcName;

    private String remark;


    private List<ModifyVdcNetworkParam> networkList;


}
