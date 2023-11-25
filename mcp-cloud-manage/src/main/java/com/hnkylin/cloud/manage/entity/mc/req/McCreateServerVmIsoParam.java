package com.hnkylin.cloud.manage.entity.mc.req;

import com.hnkylin.cloud.core.enums.LastUpdateType;
import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McCreateServerVmIsoParam {

    private Integer id = null;

    private String lastUpdateType = LastUpdateType.add.getValue();

    private String isoSelect;

    private Integer index;

    private String deviceName;


}
