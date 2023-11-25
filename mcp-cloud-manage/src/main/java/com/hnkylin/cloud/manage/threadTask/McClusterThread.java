package com.hnkylin.cloud.manage.threadTask;

import com.alibaba.fastjson.JSON;
import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.core.common.MCServerVmConstants;
import com.hnkylin.cloud.manage.service.McHttpService;

import java.util.Objects;
import java.util.concurrent.Callable;


/**
 * 通用从mc集群中获取数据线程
 */
public class McClusterThread implements Callable<String> {


    private McHttpService mcHttpService;


    private String loginUserName;

    private Integer clusterId;

    private String mcInterfaceUrl;

    private Object requestObject;


    public McClusterThread(McHttpService mcHttpService, String loginUserName,
                           Integer clusterId, String mcInterfaceUrl, Object requestObject) {
        //this.latch = latch;
        this.mcHttpService = mcHttpService;
        this.loginUserName = loginUserName;
        this.clusterId = clusterId;
        this.mcInterfaceUrl = mcInterfaceUrl;
        this.requestObject = requestObject;
    }

    @Override
    public String call() {

        String mcClusterResponse = null;
        try {
            MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, requestObject,
                    mcInterfaceUrl, loginUserName, 0);
            if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                mcClusterResponse = JSON.toJSONString(mcResponse.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
        return mcClusterResponse;
    }
}
