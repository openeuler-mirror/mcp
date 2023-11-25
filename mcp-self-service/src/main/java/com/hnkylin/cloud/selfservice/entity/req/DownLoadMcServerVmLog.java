package com.hnkylin.cloud.selfservice.entity.req;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class DownLoadMcServerVmLog {

    @FieldCheck(notNull = true, notNullMessage = "图片不能为空")
    private String mcServerVmLogoPath;
    @FieldCheck(notNull = true, notNullMessage = "图片不能为空")
    private String mcServerVmLogoName;


}
