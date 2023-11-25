package com.hnkylin.cloud.manage.entity.mc.resp;

import com.hnkylin.cloud.manage.enums.DataStoreStatus;
import com.hnkylin.cloud.manage.enums.DataStoreType;
import com.hnkylin.cloud.manage.enums.DataStoreUsage;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 集群管理-数据存储
 */
@Data
public class CheckClusterNameAndPasswordResp {


    private Boolean userNameAndPassword;

}
