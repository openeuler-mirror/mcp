package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-7-17.
 */
@Data
public class McServerVmSnapshotResp {

    private Integer id;

    private String alias;

    private String description;

    private String dateCreated;

    private String createUserName;

    private Integer status;

    //private Integer lose;

    private Integer snapType;


}
