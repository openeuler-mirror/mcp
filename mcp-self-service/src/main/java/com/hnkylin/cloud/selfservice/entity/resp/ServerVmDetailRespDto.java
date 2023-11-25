package com.hnkylin.cloud.selfservice.entity.resp;

import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.Data;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-8-17.
 */
@Data
public class ServerVmDetailRespDto {


    private String aliasName;

    private Integer cpu;

    private Integer mem;

    private MemUnit memUnit;

    private String OsMachine;

    private String architecture;

    private String systemType;

    //磁盘信息
    private List<ServerVmDiskDto> disks;

    private List<ServerVmNetworkDto> networks;

    //到期处理策略
    private ServerVmDeadlineType deadlineType;

    private String deadlineTime;

    //云服务器状态
    private McServerVmStatus status;


}
