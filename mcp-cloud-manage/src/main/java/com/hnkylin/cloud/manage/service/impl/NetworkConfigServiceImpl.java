package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.CloudClusterDo;
import com.hnkylin.cloud.core.domain.CloudNetworkConfigDo;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.core.service.CloudClusterService;
import com.hnkylin.cloud.core.service.CloudNetworkConfigService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.ListPortGroupParam;
import com.hnkylin.cloud.manage.entity.mc.resp.NetworkConfigResp;
import com.hnkylin.cloud.manage.entity.mc.resp.NetworkPortGroupResp;
import com.hnkylin.cloud.manage.entity.req.network.CreateNetworkParam;
import com.hnkylin.cloud.manage.entity.req.network.ModifyNetworkParam;
import com.hnkylin.cloud.manage.entity.req.vdc.BaseVdcParam;
import com.hnkylin.cloud.manage.entity.req.workorder.QueryPortGroupParam;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkConfigRespDto;
import com.hnkylin.cloud.manage.service.ClusterService;
import com.hnkylin.cloud.manage.service.McServerVmService;
import com.hnkylin.cloud.manage.service.NetworkConfigService;
import com.hnkylin.cloud.manage.service.VdcService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@Service
public class NetworkConfigServiceImpl implements NetworkConfigService {

    @Resource
    private CloudNetworkConfigService cloudNetworkConfigService;

    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private VdcService vdcService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Override
    public List<NetworkConfigRespDto> listNetworkListByVdcId(Integer vdcId) {
        CloudNetworkConfigDo networkConfigDo = new CloudNetworkConfigDo();
        networkConfigDo.setDeleteFlag(Boolean.FALSE);
        networkConfigDo.setVdcId(vdcId);
        QueryWrapper<CloudNetworkConfigDo> wrapper = new QueryWrapper<>(networkConfigDo);
        wrapper.orderByDesc("id");
        List<CloudNetworkConfigDo> networkConfigDoList = cloudNetworkConfigService.list(wrapper);

        List<CloudVdcDo> vdcDoList = new ArrayList<>();
        vdcDoList.add(cloudVdcService.getById(vdcId));
        List<NetworkConfigRespDto> networkList = formatNetworkConfigRespList(networkConfigDoList, vdcDoList);
        return networkList;
    }

    @Override
    public List<NetworkConfigRespDto> listNetworkListByVdcIdAndClusterId(Integer vdcId, Integer clusterId) {
        CloudNetworkConfigDo networkConfigDo = new CloudNetworkConfigDo();
        networkConfigDo.setDeleteFlag(Boolean.FALSE);
        networkConfigDo.setVdcId(vdcId);
        networkConfigDo.setClusterId(clusterId);
        QueryWrapper<CloudNetworkConfigDo> wrapper = new QueryWrapper<>(networkConfigDo);
        wrapper.orderByDesc("id");
        List<CloudNetworkConfigDo> networkConfigDoList = cloudNetworkConfigService.list(wrapper);

        List<CloudVdcDo> vdcDoList = new ArrayList<>();
        vdcDoList.add(cloudVdcService.getById(vdcId));
        List<NetworkConfigRespDto> networkList = formatNetworkConfigRespList(networkConfigDoList, vdcDoList);
        return networkList;
    }

    /**
     * 封装网络信息
     */
    private List<NetworkConfigRespDto> formatNetworkConfigRespList(List<CloudNetworkConfigDo> networkConfigDoList,
                                                                   List<CloudVdcDo> vdcList) {
        List<NetworkConfigRespDto> networkList = new ArrayList<>(networkConfigDoList.size());
        networkConfigDoList.forEach(networkConfig -> {
            CloudVdcDo vdcDo =
                    vdcList.stream().filter(item -> Objects.equals(item.getId(), networkConfig.getVdcId()))
                            .findFirst().orElse(null);
            NetworkConfigRespDto networkConfigRespDto = new NetworkConfigRespDto();
            BeanUtils.copyProperties(networkConfig, networkConfigRespDto);
            networkConfigRespDto.setCreateTime(DateUtils.format(networkConfig.getCreateTime(),
                    DateUtils.DATE_ALL_PATTEN));
            networkConfigRespDto.setNetworkId(networkConfig.getId());
            networkConfigRespDto.setVdcName(Objects.nonNull(vdcDo) ? vdcDo.getVdcName() : "-");
            networkConfigRespDto.setClusterId(networkConfig.getClusterId());
            networkConfigRespDto.setClusterName(cloudClusterService.getById(networkConfig.getClusterId()).getName());
            networkList.add(networkConfigRespDto);
        });
        return networkList;
    }


    @Override
    public PageData<NetworkConfigRespDto> pageNetworkConfig(BasePageParam basePageParam, LoginUserVo loginUserVo) {

        List<CloudVdcDo> vdcList = vdcService.visibleVdcListByUserId(loginUserVo.getUserId());
        if (vdcList.isEmpty()) {
            return new PageData(null);
        }
        CloudNetworkConfigDo networkConfigDo = new CloudNetworkConfigDo();
        networkConfigDo.setDeleteFlag(Boolean.FALSE);

        QueryWrapper<CloudNetworkConfigDo> wrapper = new QueryWrapper<>(networkConfigDo);
        wrapper.in("vdc_id", vdcList.stream().map(CloudVdcDo::getId).collect(Collectors.toList()));
        wrapper.orderByDesc("id");

        PageHelper.startPage(basePageParam.getPageNo(), basePageParam.getPageSize());
        List<CloudNetworkConfigDo> networkConfigDoList = cloudNetworkConfigService.list(wrapper);

        PageInfo<CloudNetworkConfigDo> pageInfo = new PageInfo<>(networkConfigDoList);
        List<NetworkConfigRespDto> networkList = formatNetworkConfigRespList(networkConfigDoList, vdcList);
        PageData pageData = new PageData(pageInfo);
        pageData.setList(networkList);
        return pageData;
    }

    @Override
    public void createNetwork(CreateNetworkParam createNetworkParam, LoginUserVo loginUserVo) {
        //校验是否重名
        CloudNetworkConfigDo networkConfigDo = new CloudNetworkConfigDo();
        networkConfigDo.setDeleteFlag(Boolean.FALSE);
        networkConfigDo.setNetworkName(createNetworkParam.getNetworkName());
        QueryWrapper<CloudNetworkConfigDo> wrapper = new QueryWrapper<>(networkConfigDo);
        List<CloudNetworkConfigDo> existList = cloudNetworkConfigService.list(wrapper);
        if (!existList.isEmpty()) {
            throw new KylinException(KylinHttpResponseConstants.EXIST_NETWORK_NAME);
        }

        CloudNetworkConfigDo cloudNetworkConfigDo = new CloudNetworkConfigDo();
        cloudNetworkConfigDo.setCreateBy(loginUserVo.getUserId());
        cloudNetworkConfigDo.setCreateTime(new Date());
        BeanUtils.copyProperties(createNetworkParam, cloudNetworkConfigDo);
        cloudNetworkConfigService.save(cloudNetworkConfigDo);
    }
//
//    @Override
//    public NetworkConfigResp mcNetworkConfig(LoginUserVo loginUserVo) {
//
//        return mcServerVmService.mcNetworkConfig(loginUserVo);
//    }

//    @Override
//    public List<NetworkPortGroupResp> mcPortGroupListByVirtualSwitch(QueryPortGroupParam queryPortGroupParam,
//                                                                     LoginUserVo loginUserVo) {
//        ListPortGroupParam listPortGroupParam = new ListPortGroupParam();
//        listPortGroupParam.setVsname(queryPortGroupParam.getVirtualSwitchName());
//        return mcServerVmService.mcPortGroupListByVirtualSwitch(listPortGroupParam, loginUserVo);
//    }

    @Override
    public CloudNetworkConfigDo getById(Integer networkId) {
        return cloudNetworkConfigService.getById(networkId);
    }


    @Override
    public int getNetWorkCountByVdcId(Integer vdcId) {
        CloudNetworkConfigDo networkConfigDo = new CloudNetworkConfigDo();
        networkConfigDo.setDeleteFlag(Boolean.FALSE);
        networkConfigDo.setVdcId(vdcId);
        QueryWrapper<CloudNetworkConfigDo> wrapper = new QueryWrapper<>(networkConfigDo);

        return cloudNetworkConfigService.getBaseMapper().selectCount(wrapper);
    }

    @Override
    public NetworkConfigRespDto networkDetail(Integer networkId) {
        CloudNetworkConfigDo networkConfigDo = cloudNetworkConfigService.getById(networkId);
        NetworkConfigRespDto networkConfigRespDto = new NetworkConfigRespDto();
        BeanUtils.copyProperties(networkConfigDo, networkConfigRespDto);
        networkConfigRespDto.setNetworkId(networkConfigDo.getId());
        CloudVdcDo vdcDo = cloudVdcService.getById(networkConfigDo.getVdcId());
        networkConfigRespDto.setVdcName(Objects.nonNull(vdcDo) ? vdcDo.getVdcName() : "-");
        networkConfigRespDto.setClusterId(networkConfigDo.getClusterId());
        networkConfigRespDto.setClusterName(cloudClusterService.getById(networkConfigDo.getClusterId()).getName());
        return networkConfigRespDto;
    }

    @Override
    public void modifyNetwork(ModifyNetworkParam modifyNetworkParam, LoginUserVo loginUserVo) {
        CloudNetworkConfigDo networkConfigDo = cloudNetworkConfigService.getById(modifyNetworkParam.getNetworkId());
        networkConfigDo.setInterfaceType(modifyNetworkParam.getInterfaceType());
        networkConfigDo.setNetworkName(modifyNetworkParam.getNetworkName());
        networkConfigDo.setModelType(modifyNetworkParam.getModelType());
        networkConfigDo.setAddressPool(modifyNetworkParam.getAddressPool());
        networkConfigDo.setAddressPoolId(modifyNetworkParam.getAddressPoolId());
        networkConfigDo.setPortGroup(modifyNetworkParam.getPortGroup());
        networkConfigDo.setPortGroupUuid(modifyNetworkParam.getPortGroupUuid());
        networkConfigDo.setVirtualSwitch(modifyNetworkParam.getVirtualSwitch());
        networkConfigDo.setSecurityPolicy(modifyNetworkParam.getSecurityPolicy());
        networkConfigDo.setSecurityGroup(modifyNetworkParam.getSecurityGroup());
        networkConfigDo.setSecurityGroupUuid(modifyNetworkParam.getSecurityGroupUuid());
        networkConfigDo.setVirtualFirewallId(modifyNetworkParam.getVirtualFirewallId());
        networkConfigDo.setVirtualFirewallName(modifyNetworkParam.getVirtualFirewallName());
        networkConfigDo.setUpdateBy(loginUserVo.getUserId());
        networkConfigDo.setUpdateTime(new Date());
        cloudNetworkConfigService.updateById(networkConfigDo);
    }
}
