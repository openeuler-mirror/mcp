package com.hnkylin.cloud.selfservice.entity.mc.resp;

import com.hnkylin.cloud.core.enums.McServerVmStatus;
import lombok.Data;

/**
 * 模板中磁盘信息
 * Created by kylin-ksvd on 21-7-14.
 */
@Data
public class McServerVmStatusResp {

    private McServerVmStatus status;
}
