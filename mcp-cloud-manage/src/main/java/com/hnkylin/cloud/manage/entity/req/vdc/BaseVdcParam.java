package com.hnkylin.cloud.manage.entity.req.vdc;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BaseVdcParam {

    @FieldCheck(notNull = true, notNullMessage = "vdcId不能为空")
    Integer vdcId;


}
