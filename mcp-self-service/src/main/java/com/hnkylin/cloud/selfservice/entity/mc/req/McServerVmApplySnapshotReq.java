package com.hnkylin.cloud.selfservice.entity.mc.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

/**
 * 应用快照
 * Created by kylin-ksvd on 21-7-17.
 */
@Data
public class McServerVmApplySnapshotReq {


    @FieldCheck(notNull = true, notNullMessage = "用户服务器uuid不能为空")
    private String uuid;

    @FieldCheck(notNull = true, notNullMessage = "快照ID不能为空")
    private Integer snapId;

}
