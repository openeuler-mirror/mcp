package com.hnkylin.cloud.core.common.servervm;

import com.hnkylin.cloud.core.enums.McServerVmStatus;
import lombok.Data;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
@Data
public class ServerVmListReq {

    private String vmName;

    private McServerVmStatus vmStatus;

    private Integer page;

    private Integer rows;

    private List<String> uuidList;
}
