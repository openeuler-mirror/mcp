package com.hnkylin.cloud.manage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.core.common.MCServerVmConstants;
import com.hnkylin.cloud.core.common.McUtils;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
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
        try {
            List<String> mcNodeList = mcNodeService.getMcNodeListFromCache(clusterId);
            int mcNodeListSize = mcNodeList.size();
            if (retryIndex < mcNodeListSize) {

                String mcLeaderUrl = getMcLeaderUrl(clusterId, retryIndex);

                String requestUrl = mcLeaderUrl + mcConfigProperties.getCommonPrefix() + httpMcUrl;

                MCResponseData<Object> result = commonSendToMc(userName, requestObj, requestUrl);
                log.info("noDataCommonMcRequest-retryIndex:{},response:{}", retryIndex, JSON.toJSONString(result));
                if (Objects.isNull(result) && retryIndex < mcNodeListSize - 1) {
                    return noDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
                }

                if (Objects.equals(MCServerVmConstants.SUCCESS, result.getStatus())) {
                    mcNodeService.setMcLeaderUrlToCache(mcLeaderUrl, clusterId);
                    return true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return noDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
        }
        throw new KylinException(MCServerVmConstants.MC_NODE_NOT_VISIT);
    }

    /**
     * 无缓存带有数据响应
     *
     * @param requestObj
     * @param mcNodeList
     * @param interfaceUrl
     * @param userName
     * @param retryIndex
     * @return
     */
    public MCResponseData<Object> noCacheHasDataMcRequest(Object requestObj, List<String> mcNodeList,
                                                          String interfaceUrl,
                                                          String userName,
                                                          Integer retryIndex) {
        try {
            int mcNodeListSize = mcNodeList.size();
            if (retryIndex < mcNodeListSize) {

                String mcLeaderUrl = mcNodeList.get(retryIndex);

                String requestUrl = mcLeaderUrl + mcConfigProperties.getCommonPrefix() + interfaceUrl;
                MCResponseData<Object> result = commonSendToMc(userName, requestObj, requestUrl);
                log.info("noDataCommonMcRequest-retryIndex:{},response:{}", retryIndex, JSON.toJSONString(result));
                if (Objects.isNull(result) && retryIndex < mcNodeListSize - 1) {
                    return noCacheHasDataMcRequest(requestObj, mcNodeList, interfaceUrl, userName, retryIndex + 1);
                }
                return result;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return noCacheHasDataMcRequest(requestObj, mcNodeList, interfaceUrl, userName, retryIndex + 1);
        }
        throw new KylinException(MCServerVmConstants.MC_NODE_NOT_VISIT);
    }


    /**
     * 向MC发送
     *
     * @param userName
     * @param requestObj
     * @param requestUrl
     * @return
     */
    public MCResponseData<Object> commonSendToMc(String userName, Object requestObj, String requestUrl) {
        MCResponseData<Object> result = null;
        try {
            HttpHeaders httpHeaders = McUtils.createMcHeaders(userName);
            log.info("sendToMc-headers:{}", JSON.toJSONString(httpHeaders));
            requestObj = Objects.isNull(requestObj) ? new Object() : requestObj;
            JSONObject reqBodyObj = JSONObject.parseObject(JSON.toJSONString(requestObj));
            log.info("sendToMc-url:{},body:{}", requestUrl, reqBodyObj.toJSONString());
            HttpEntity<JSObject> httpEntity = new HttpEntity(reqBodyObj, httpHeaders);
            result = httpsRestTemplate.postForObject(requestUrl, httpEntity, MCResponseData.class);
            log.info("sendToMc-response:{}", JSON.toJSONString(result));
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
        return result;
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

                String requestUrl = mcLeaderUrl + mcConfigProperties.getCommonPrefix() + httpMcUrl;

                MCResponseData<Object> result = commonSendToMc(userName, requestObj, requestUrl);

                log.info("hasDataCommonMcRequest-retryIndex:{},response:{}", retryIndex, JSON.toJSONString(result));
                if (Objects.isNull(result) && retryIndex < mcNodeListSize - 1) {
                    return hasDataCommonMcRequest(clusterId, requestObj, httpMcUrl, userName, retryIndex + 1);
                }
                if (Objects.nonNull(result) && Objects.equals(result.getStatus(), MCServerVmConstants.SUCCESS)) {
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
