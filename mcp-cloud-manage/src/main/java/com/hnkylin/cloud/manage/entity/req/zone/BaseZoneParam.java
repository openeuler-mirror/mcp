package com.hnkylin.cloud.manage.entity.req.zone;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BaseZoneParam {

    @FieldCheck(notNull = true, notNullMessage = "可用区ID不能为空")
    Integer zoneId;


}
