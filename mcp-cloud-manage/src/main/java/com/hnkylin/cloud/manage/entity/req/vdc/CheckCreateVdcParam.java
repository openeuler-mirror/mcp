package com.hnkylin.cloud.manage.entity.req.vdc;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

/**
 * 创建VDC时检查是否能创建
 */
@Data
public class CheckCreateVdcParam {

    @FieldCheck(notNull = true, notNullMessage = "可用区不能为空")
    private Integer zoneId;

    @FieldCheck(notNull = true, notNullMessage = "上级VDC不能为空")
    private Integer parentVdcId;
}
