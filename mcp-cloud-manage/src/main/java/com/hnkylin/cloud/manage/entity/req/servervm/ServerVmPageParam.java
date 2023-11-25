package com.hnkylin.cloud.manage.entity.req.servervm;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.manage.enums.ZoneOrgUserType;
import lombok.Data;

@Data
public class ServerVmPageParam extends BasePageParam {

    @FieldCheck(notNull = true, notNullMessage = "角色名称不能为空")
    private ZoneOrgUserType type;


    private String searchKey;

    private McServerVmStatus vmStatus;


    private Integer uniqueId;

    private Integer clusterId = 0;


}
