package com.hnkylin.cloud.selfservice.entity.resp;

import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.McServerVmTaskStatus;
import lombok.Data;

import java.util.Objects;

@Data
public class PageServerVmRespDto implements Comparable<PageServerVmRespDto> {

    //云服务器id
    private Integer serverVmId;

    //云服务器uuid
    private String serverVmUuid;

    //云服务器名称
    private String aliasName;

    //云服务器状态
    private McServerVmStatus status;

    //云服务器任务状态
    private McServerVmTaskStatus taskStatus;

    //云服务器ip
    private String ip;

    //操作系统
    private String os;

    private String architecture;

    //cpu数
    private Integer cpus;

    //内存大小
    private Integer memory;

    //磁盘大小
    private String disks;


    private String usage;

    //创建时间
    private String createDate;

    //内存单位
    private String memoryUnit;

    //过期时间
    private Integer deadlineTime;

    //是否已经过期
    private Boolean deadlineFlag;

    //描述
    private String description;


    @Override
    public int compareTo(PageServerVmRespDto obj) {
        if (Objects.equals(obj.getStatus(), this.getStatus())) {
            return 0;
        } else {
            if (Objects.equals(obj.getStatus(), McServerVmStatus.AVAILABLE) || Objects.equals(obj.getStatus(),
                    McServerVmStatus.INSTALLING) || Objects.equals(obj.getStatus(), McServerVmStatus.SUSPEND) || Objects.equals(obj.getStatus(),
                    McServerVmStatus.CONNECTED)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
