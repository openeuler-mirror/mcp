package com.hnkylin.cloud.selfservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.core.common.MCServerVmConstants;
import com.hnkylin.cloud.core.common.McUtils;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.config.exception.KylinMcRequestException;
import com.hnkylin.cloud.selfservice.config.MCConfigProperties;
import jdk.nashorn.api.scripting.JSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Service
@Slf4j
public class McHttpService {


    @Resource
    private RestTemplate httpsRestTemplate;


    @Resource
    private McNodeService mcNodeService;

    @Resource
    private MCConfigProperties mcConfigProperties;


    /*
     * 不需要响应data的通用和mc请求
     */
    public boolean noDataCommonMcRequest(Integer clusterId, Object requestObj, String httpMcUrl, String userName,
                                         Integer retryIndex) {
        List<String> mcNodeList = mcNodeService.getMcNodeListFromCache(clusterId);
        int mcNodeListSize = mcNodeList.size();
        try {
            if (retryIndex < mcNodeListSize) {
                String mcLeaderUrl = getMcLeaderUrl(clusterId, retryIndex);
                HttpHeaders httpHeaders = McUtils.createMcHeaders(userName);
                log.info("noDataCommonMcRequest-headers:{}", JSON.toJSONString(httpHeaders));

                requestObj = Objects.isNull(requestObj) ? new Object() : requestObj;
                JSONObject reqBodyObj = JSONObject.parseObject(JSON.toJSONString(requestObj));
                String requestUrl = mcLeaderUrl + mcConfigProperties.getCommonPrefix() + httpMcUrl;
                log.info("noDataCommonMcRequest-url:{},body:{}", requestUrl, reqBodyObj.toJSONString());


                HttpEntity<JSObject> httpEntity = new HttpEntity(reqBodyObj, httpHeaders);

                MCResponseData<Object> result =
                        httpsRestTemplate.postForObject(requestUrl, httpEntity, MCResponseData.class);

                log.info("noDataCommonMcRequest-response:{}", JSON.toJSONString(result));
                if (Objects.isNull(result) && retryIndex < mcNodeListSize - 1) {
                    return noDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
                }

                if (Objects.equals(MCServerVmConstants.SUCCESS, result.getStatus())) {
                    mcNodeService.setMcLeaderUrlToCache(mcLeaderUrl, clusterId);
                }
                return Objects.equals(MCServerVmConstants.SUCCESS, result.getStatus());

            }
        } catch (Exception exception) {
            exception.printStackTrace();
            log.debug(exception.getMessage());
            if (retryIndex < mcNodeListSize - 1) {
                return noDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
            }
        }
        throw new KylinException(MCServerVmConstants.MC_NODE_NOT_VISIT);
    }

    /*
     * 带data响应的的通用和mc请求
     */
    public MCResponseData<Object> hasDataCommonMcRequest(Integer clusterId, Object requestObj, String httpMcUrl,
                                                         String userName,
                                                         Integer retryIndex) {
        List<String> mcNodeList = mcNodeService.getMcNodeListFromCache(clusterId);
        int mcNodeListSize = mcNodeList.size();
        try {

            if (retryIndex < mcNodeListSize) {
                String mcLeaderUrl = getMcLeaderUrl(clusterId, retryIndex);
                HttpHeaders httpHeaders = McUtils.createMcHeaders(userName);
                log.info("hasDataCommonMcRequest-headers:{}", JSON.toJSONString(httpHeaders));

                requestObj = Objects.isNull(requestObj) ? new Object() : requestObj;

                JSONObject reqBodyObj = JSONObject.parseObject(JSON.toJSONString(requestObj));

                String requestUrl = mcLeaderUrl + mcConfigProperties.getCommonPrefix() + httpMcUrl;
                log.info("hasDataCommonMcRequest-url:{},body:{},retryIndex:{}",
                        requestUrl,
                        reqBodyObj.toJSONString(), retryIndex);


                HttpEntity<JSObject> httpEntity = new HttpEntity(reqBodyObj, httpHeaders);


                MCResponseData<Object> result =
                        httpsRestTemplate.postForObject(requestUrl, httpEntity, MCResponseData.class);
                log.info("hasDataCommonMcRequest-retryIndex:{},response:{}", retryIndex, JSON.toJSONString(result));
                if (Objects.isNull(result) && retryIndex < mcNodeListSize - 1) {
                    return hasDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
                }
                if (Objects.nonNull(result) && Objects.equals(result.getStatus(), "success")) {
                    mcNodeService.setMcLeaderUrlToCache(mcLeaderUrl, clusterId);
                }
                return result;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            log.debug(exception.getMessage());
            if (retryIndex < mcNodeListSize - 1) {
                return hasDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
            }

        }
        throw new KylinException(MCServerVmConstants.MC_NODE_NOT_VISIT);
    }


    /**
     * 获取mc主节点Url
     */
    private String getMcLeaderUrl(Integer clusterId, Integer retryIndex) {

        String mcLeaderUrl = null;
        if (Objects.equals(retryIndex, 0)) {
            mcLeaderUrl = mcNodeService.getMcLeaderUrlFromCache(clusterId);
            if (Objects.nonNull(mcLeaderUrl)) {
                return mcLeaderUrl;
            }
        }
        List<String> mcNodeList = mcNodeService.getMcNodeListFromCache(clusterId);

        return mcNodeList.isEmpty() ? null : mcNodeList.get(retryIndex);
    }

}
