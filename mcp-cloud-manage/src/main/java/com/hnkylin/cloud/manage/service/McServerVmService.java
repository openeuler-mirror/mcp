package com.hnkylin.cloud.manage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.core.common.MCServerVmConstants;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.enums.ApplyServerVmType;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseOrderConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.*;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class McServerVmService {


    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private McHttpService mcHttpService;


    /**
     * 获取mc网络资源
     */
    public NetworkConfigResp mcClusterNetworkConfig(Integer clusterId, LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getNetworkConfigUrl();

        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, null,
                httpUrl, loginUserVo.getUserName(), 0);
        NetworkConfigResp networkConfigResp = new NetworkConfigResp();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String mcNetworkConfigStr = JSON.toJSONString(mcResponse.getData());
            networkConfigResp = JSON.parseObject(mcNetworkConfigStr, NetworkConfigResp.class);

        }
        return networkConfigResp;
    }


    /**
     * 网络资源-根据虚拟交换机，获取端口组信息
     */
    public List<NetworkPortGroupResp> mcClusterPortGroupListByVirtualSwitch(Integer clusterId,
                                                                            ListPortGroupParam listPortGroupParam,
                                                                            LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getListPortGroupByNameUrl();

        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, listPortGroupParam, httpUrl,
                loginUserVo.getUserName(), 0);
        List<NetworkPortGroupResp> portGroupRespList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String portGroupListStr = JSONArray.toJSONString(mcResponse.getData());
            portGroupRespList = JSON.parseArray(portGroupListStr, NetworkPortGroupResp.class);
        }
        return portGroupRespList;
    }

    /**
     * 根据云服务器ID(UUID)获取云服务器(模板详情)
     */
    public McServerVmDetailResp getMcServerVmDetailByServerVmId(Integer clusterId,
                                                                QueryMcServerDetailParamReq queryMcServerDetailParamReq,
                                                                LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getGetServervmDetailByServevmIdUrl();
        return getMcServerVmDetail(clusterId, queryMcServerDetailParamReq, loginUserVo, httpUrl);
    }

    public McServerVmDetailResp getMcServerVmDetailByServerVmUuid(Integer clusterId,
                                                                  QueryMcServerDetailParamReq queryMcServerDetailParamReq,
                                                                  LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getGetServervmDetailByUuidUrl();
        return getMcServerVmDetail(clusterId, queryMcServerDetailParamReq, loginUserVo, httpUrl);

    }


    /**
     * 根据云服务器ID(UUID)获取云服务器(模板详情)
     */
    private McServerVmDetailResp getMcServerVmDetail(Integer clusterId,
                                                     QueryMcServerDetailParamReq queryMcServerDetailParamReq,
                                                     LoginUserVo loginUserVo, String httpUrl) {
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId,
                queryMcServerDetailParamReq, httpUrl,
                loginUserVo.getUserName(), 0);
        McServerVmDetailResp mcServerVmDetailResp = new McServerVmDetailResp();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String mcServerVmDetailStr = JSON.toJSONString(mcResponse.getData());
            mcServerVmDetailResp = JSON.parseObject(mcServerVmDetailStr, McServerVmDetailResp.class);
        }
        return mcServerVmDetailResp;
    }


    /**
     * 获取mc中计算资源列表
     *
     * @param clusterId
     * @param queryMcClusterParamReq
     * @param loginUserVo
     * @return
     */
    public List<McClusterResp> getMcClusterList(Integer clusterId, QueryMcClusterParamReq queryMcClusterParamReq,
                                                LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getGetRealServiceListUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, queryMcClusterParamReq,
                httpUrl,
                loginUserVo.getUserName(), 0);
        List<McClusterResp> clusterRespList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String clusterListStr = JSONArray.toJSONString(mcResponse.getData());
            clusterRespList = JSON.parseArray(clusterListStr, McClusterResp.class);
        }
        return clusterRespList;
    }

    /**
     * 获取mc中主机绑定资源
     *
     * @param clusterId
     * @param loginUserVo
     * @return
     */
    public List<McClusterBindResource> getMcClusterBindResourceList(Integer clusterId, LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getGetClusterBindResourceListUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, null,
                httpUrl, loginUserVo.getUserName(), 0);
        List<McClusterBindResource> clusterBindResourceList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            clusterBindResourceList = JSON.parseArray(JSONArray.toJSONString(mcResponse.getData()),
                    McClusterBindResource.class);
        }
        return clusterBindResourceList;
    }


    /**
     * 获取MC的存储位置列表
     */
    public List<McStorageLocationResp> getMcStorageLocationList(Integer clusterId, LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getStorageLocationsListUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, null, httpUrl,
                loginUserVo.getUserName(), 0);
        List<McStorageLocationResp> storageLocationRespList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String storageLocationListStr = JSONArray.toJSONString(mcResponse.getData());
            storageLocationRespList = JSON.parseArray(storageLocationListStr, McStorageLocationResp.class);
        }
        return storageLocationRespList;
    }

    /**
     * 获取mc所以iso光驱列表
     *
     * @param loginUserVo
     * @return
     */
    public List<String> mcAllIsoList(Integer clusterId, LoginUserVo loginUserVo) {
        List<String> isoList = new ArrayList<>();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, null,
                mcConfigProperties.getIsoListUrl(), loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String templateListStr = JSON.toJSONString(mcResponse.getData());
            isoList = (List<String>) mcResponse.getData();
        }
        return isoList;

    }

    /**
     * 创建云服务器
     */
    public List<String> createMcServerVm(Integer clusterId, McCreateServerVmParamReq mcCreateServerVmParamReq,
                                         LoginUserVo loginUserVo,
                                         ApplyServerVmType applyServerVmType) {
        try {

            String createUrl = Objects.equals(applyServerVmType, ApplyServerVmType.TEMPLATE) ?
                    mcConfigProperties.getCreateServerVmUrl()
                    : mcConfigProperties.getCreateServerVmByIsoUrl();
            MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId,
                    mcCreateServerVmParamReq,
                    createUrl,
                    loginUserVo.getUserName(), 0);
            List<String> uuidList = new ArrayList<>();
            if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                String uusListStr = JSONArray.toJSONString(mcResponse.getData());
                uuidList = JSON.parseArray(uusListStr, String.class);
            }
            return uuidList;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new KylinException(KylinHttpResponseOrderConstants.CREATE_SERVERVM_ERR);
        }
    }

    public void checkServerNameExist(Integer clusterId, CheckServerNameParamReq checkServerNameParamReq,
                                     LoginUserVo loginUserVo) {

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, checkServerNameParamReq,
                mcConfigProperties.getCheckVmServerNameUrl(),
                loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String existStr = JSONArray.toJSONString(mcResponse.getData());
            CheckServerNameResp checkServerNameResp = JSON.parseObject(existStr, CheckServerNameResp.class);
            if (Objects.nonNull(checkServerNameResp) && checkServerNameResp.getExist()) {
                throw new KylinException(KylinHttpResponseConstants.SERVER_VM_NAME_EXIST);
            }
        }

    }


    /**
     * 创建云服务器
     */
    public boolean modifyMcServerVm(Integer clusterId, McModifyServerVmParamReq mcModifyServerVmParamReq,
                                    LoginUserVo loginUserVo) {
        try {

            String httpUrl = mcConfigProperties.getModifyServerVmUrl();
            MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId,
                    mcModifyServerVmParamReq, httpUrl,
                    loginUserVo.getUserName(), 0);
            if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new KylinException(KylinHttpResponseOrderConstants.MODIFY_SERVERVM_ERR);
        }
        return false;
    }
}
