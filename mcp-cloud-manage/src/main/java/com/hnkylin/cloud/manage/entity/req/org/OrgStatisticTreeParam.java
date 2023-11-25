package com.hnkylin.cloud.manage.entity.req.org;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.manage.enums.OrgStatisticTreeType;
import lombok.Data;

@Data
public class OrgStatisticTreeParam {

    @FieldCheck(notNull = true, notNullMessage = "统计类型不能为空")
    OrgStatisticTreeType statisticType;


}
