package com.hnkylin.cloud.selfservice.entity.req;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class UpdateServerVmDescriptionParam {


    @FieldCheck(notNull = true, notNullMessage = "云服务器UUID不能为空")
    private String uuid;

    @FieldCheck(notNull = true, notNullMessage = "描述不能为空")
    private String description;


}
