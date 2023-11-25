package com.hnkylin.cloud.manage.entity.resp.workorder;


import com.hnkylin.cloud.core.enums.ApplyServerVmType;
import com.hnkylin.cloud.core.enums.McServerClusterType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkConfigRespDto;
import com.hnkylin.cloud.manage.entity.resp.workorder.ApplyServerVmIsoDetailResp;
import lombok.Data;

import java.util.List;

/**
 * 申请云服务器审核通过时，响应的申请详情，包括
 */
@Data
public class PassApplyServerVmDetailRespDto extends CommonServerVmDetailResp {

    private ApplyServerVmType applyServerVmType;


    //申请个数
    private Integer applyNum;


    //模板名称
    private String templateName;


    //模板ID
    private Integer templateId;


    //光驱文件列表
    private List<ApplyServerVmIsoDetailResp> applyIsoList;

    private List<String> allIsoList;


}
