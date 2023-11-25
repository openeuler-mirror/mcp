package com.hnkylin.cloud.selfservice.entity.mc.req;

import lombok.Data;

/**
 * 删除快照
 * Created by kylin-ksvd on 21-7-17.
 */
@Data
public class McServerVmDeleteSnapshotReq {


    private String uuid;

    private String snapId;
}
