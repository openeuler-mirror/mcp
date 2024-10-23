package com.hnkylin.cloud.selfservice.entity.mc.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

/**
 * 编辑快照
 * Created by kylin-ksvd on 21-7-17.
 */
@Data
public class McServerVmUpdateSnapshotReq {

    @FieldCheck(notNull = true, notNullMessage = "uuid不能为空")
    private String uuid;

    @FieldCheck(notNull = true, notNullMessage = "快照ID不能为空")
    private Integer snapId;

    private String description;

    @FieldCheck(notNull = true, notNullMessage = "快照名称不能为空")
    private String snapName;
}
