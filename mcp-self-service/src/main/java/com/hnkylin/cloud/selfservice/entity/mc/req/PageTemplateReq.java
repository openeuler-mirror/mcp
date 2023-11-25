package com.hnkylin.cloud.selfservice.entity.mc.req;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
@Data
public class PageTemplateReq {


    private Integer page;

    private Integer rows;

    private String searchKey;


}
