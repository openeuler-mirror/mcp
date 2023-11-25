package com.hnkylin.cloud.selfservice.entity.req;

import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import lombok.Data;

@Data
public class ServerVmPageParam extends BasePageParam {

    private String searchKey;

    private McServerVmStatus vmStatus;

}
