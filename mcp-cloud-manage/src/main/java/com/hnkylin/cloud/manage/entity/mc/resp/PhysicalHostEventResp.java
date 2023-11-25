package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

@Data
public class PhysicalHostEventResp {


    private String severity;

    private String hostname;

    private String info;

    private String mutableTags;


    private String timestamp;

    private String type;
}
