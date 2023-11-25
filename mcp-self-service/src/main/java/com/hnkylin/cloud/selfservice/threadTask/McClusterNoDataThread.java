package com.hnkylin.cloud.selfservice.threadTask;

import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.selfservice.service.McHttpService;

import java.util.Objects;
import java.util.concurrent.Callable;


/**
 * 向mc多集群发送无数据获取请假
 */
public class McClusterNoDataThread implements Callable<MCResponseData<Object>> {


    private McHttpService mcHttpService;


    private String loginUserName;

    private Integer clusterId;

    private String mcInterfaceUrl;

    private Object requestObject;


    public McClusterNoDataThread(McHttpService mcHttpService,
                                 String loginUserName,
                                 Integer clusterId, String mcInterfaceUrl, Object requestObject) {
        //this.latch = latch;
        this.mcHttpService = mcHttpService;
        this.loginUserName = loginUserName;
        this.clusterId = clusterId;
        this.mcInterfaceUrl = mcInterfaceUrl;
        this.requestObject = requestObject;
    }

    @Override
    public MCResponseData<Object> call() {
        MCResponseData<Object> mcResponse = null;
        try {
            mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, requestObject, mcInterfaceUrl, loginUserName
                    , 0);
            if (Objects.nonNull(mcResponse)) {
                return mcResponse;

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return mcResponse;
    }
}
