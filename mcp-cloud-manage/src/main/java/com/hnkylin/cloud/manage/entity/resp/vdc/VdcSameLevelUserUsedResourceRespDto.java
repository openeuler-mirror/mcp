package com.hnkylin.cloud.manage.entity.resp.vdc;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 直接挂在本级VDC(组织)下用户云服务器占用资源
 */
@Data
public class VdcSameLevelUserUsedResourceRespDto {

    //总存储大小
    private Integer usedStorage = 0;

    //各个架构总使用CPU
    private Integer usedCpu = 0;

    //各个架构总使用内存
    private Integer usedMem = 0;


    private List<VdcSameLevelUserUsedArchitectureResourceRespDto> userUsedArchitectureResourceList =
            new ArrayList<>();


}
