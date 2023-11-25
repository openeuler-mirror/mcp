package com.hnkylin.cloud.manage.entity.mc.req;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McCreateServerVmDiskParam {

    //网络类型
    private String id;

    //磁盘大小
    private Integer diskCapacity;
    //变更类型
    private String lastUpdateType;


}
