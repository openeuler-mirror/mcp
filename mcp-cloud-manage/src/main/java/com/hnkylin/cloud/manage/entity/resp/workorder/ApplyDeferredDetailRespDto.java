package com.hnkylin.cloud.manage.entity.resp.workorder;


import lombok.Data;

@Data
public class ApplyDeferredDetailRespDto extends BaseWorkOrderDetailDto {


    //原过期时间
    private String oldDeadlineTime;

    //新过期时间
    private String newDeadlineTime;


}
