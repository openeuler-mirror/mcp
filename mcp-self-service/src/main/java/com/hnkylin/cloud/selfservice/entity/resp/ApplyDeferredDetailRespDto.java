package com.hnkylin.cloud.selfservice.entity.resp;


import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.Data;

import java.util.List;

@Data
public class ApplyDeferredDetailRespDto extends BaseWorkOrderDetailDto {


    //原过期时间
    private String oldDeadlineTime;

    //新过期时间
    private String newDeadlineTime;


}
