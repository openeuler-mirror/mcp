package com.hnkylin.cloud.selfservice.entity.mc.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

/**
 * 创建快照
 * Created by kylin-ksvd on 21-7-17.
 */
@Data
public class McServerVmCreateSnapshotReq {


    @FieldCheck(notNull = true, notNullMessage = "用户服务器uuid不能为空")
    private String uuid;


    private String description;

    @FieldCheck(notNull = true, notNullMessage = "快照名不能为空")
    private String snapName;
}
