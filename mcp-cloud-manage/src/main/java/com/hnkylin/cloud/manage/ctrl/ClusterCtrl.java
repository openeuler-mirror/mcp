package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.CloudClusterDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterPhysicalHostResp;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterStorageResp;
import com.hnkylin.cloud.manage.entity.mc.resp.NetworkConfigResp;
import com.hnkylin.cloud.manage.entity.mc.resp.NetworkPortGroupResp;
import com.hnkylin.cloud.manage.entity.req.cluster.*;
import com.hnkylin.cloud.manage.entity.req.workorder.QueryPortGroupParam;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.resp.cluster.*;
import com.hnkylin.cloud.manage.service.ClusterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cluster")
public class ClusterCtrl {


    @Resource
    private ClusterService clusterService;


    @PostMapping("/checkClusterUserNameAndPassword")
    @ParamCheck
    public BaseResult<String> checkClusterUserNameAndPassword(@ModelCheck(notNull = true) @RequestBody
                                                                      CheckClusterNameAndPasswordParam checkClusterNameAndPasswordParam,
                                                              @LoginUser LoginUserVo loginUserVo) {
        return clusterService.checkClusterUserNameAndPassword(checkClusterNameAndPasswordParam, loginUserVo);

    }

    @PostMapping("/pageStorage")
    @ParamCheck
    public BaseResult<PageData<McClusterStorageResp>> pageStorage(@ModelCheck(notNull = true) @RequestBody
                                                                          ClusterStoragePageParam clusterStoragePageParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.pageStorage(clusterStoragePageParam, loginUserVo));

    }

    @PostMapping("/creatCluster")
    @ParamCheck
    public BaseResult<String> creatCluster(@ModelCheck(notNull = true) @RequestBody CreateClusterParam createClusterParam,
                                           @LoginUser LoginUserVo loginUserVo) {

        clusterService.creatCluster(createClusterParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/pageCluster")
    @ParamCheck
    public BaseResult<PageData<PageClusterDetailDto>> pageCluster(@ModelCheck(notNull = true) @RequestBody
                                                                          ClusterPageParam clusterPageParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.pageCluster(clusterPageParam, loginUserVo));

    }

    @PostMapping("/getQuickLoginUrl")
    @ParamCheck
    public BaseResult<LoginClusterRespDto> getQuickLoginUrl(@ModelCheck(notNull = true) @RequestBody
                                                                    BaseClusterParam baseClusterParam,
                                                            @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.getQuickLoginUrl(baseClusterParam, loginUserVo));

    }


    @PostMapping("/clusterInfo")
    @ParamCheck
    public BaseResult<ClusterInfoDto> clusterInfo(@ModelCheck(notNull = true) @RequestBody
                                                          BaseClusterParam baseClusterParam,
                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.clusterInfo(baseClusterParam, loginUserVo));

    }

    @PostMapping("/pagePhysicalHost")
    @ParamCheck
    public BaseResult<PageData<McClusterPhysicalHostResp>> pagePhysicalHost(@ModelCheck(notNull = true) @RequestBody
                                                                                    ClusterPhysicalHostPageParam clusterPhysicalHostPageParam,
                                                                            @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.pagePhysicalHost(clusterPhysicalHostPageParam, loginUserVo));

    }

    @PostMapping("/clusterDetail")
    @ParamCheck
    public BaseResult<ClusterDetailDto> clusterDetail(@ModelCheck(notNull = true) @RequestBody
                                                              BaseClusterParam baseClusterParam,
                                                      @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.clusterDetail(baseClusterParam, loginUserVo));

    }


    @PostMapping("/modifyCluster")
    @ParamCheck
    public BaseResult<String> modifyCluster(@ModelCheck(notNull = true) @RequestBody ModifyClusterParam modifyClusterParam,
                                            @LoginUser LoginUserVo loginUserVo) {

        clusterService.modifyCluster(modifyClusterParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/deleteCluster")
    @ParamCheck
    public BaseResult<String> deleteCluster(@ModelCheck(notNull = true) @RequestBody BaseClusterParam baseClusterParam,
                                            @LoginUser LoginUserVo loginUserVo) {

        clusterService.deleteCluster(baseClusterParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/clusterNetworkConfig")
    @ParamCheck
    public BaseResult<NetworkConfigResp> mcNetworkConfig(@ModelCheck(notNull = true) @RequestBody
                                                                 BaseClusterParam baseClusterParam,
                                                         @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.mcClusterNetWorkConfig(baseClusterParam, loginUserVo));

    }

    @PostMapping("/mcPortGroupListByVirtualSwitch")
    @ParamCheck
    public BaseResult<List<NetworkPortGroupResp>> mcPortGroupListByVirtualSwitch(@RequestBody QueryPortGroupParam queryPortGroupParam,
                                                                                 @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.mcPortGroupListByVirtualSwitch(queryPortGroupParam,
                loginUserVo));

    }


    @PostMapping("/zoneClusterList")
    @ParamCheck
    public BaseResult<List<ClusterDto>> zoneClusterList(@ModelCheck(notNull = true) @RequestBody
                                                                BaseZoneParam baseZoneParam) {
        return BaseResult.success(clusterService.zoneClusterList(baseZoneParam.getZoneId()));

    }

    @PostMapping("/getLoginUserCluster")
    public BaseResult<List<ClusterDto>> getLoginUserCluster(@LoginUser LoginUserVo loginUserVo) {
        List<CloudClusterDo> userVisibleClusterList = clusterService.visibleClusterByUserId(loginUserVo.getUserId());
        List<ClusterDto> clusterDtoList = new ArrayList<>();
        userVisibleClusterList.forEach(clusterDo -> {
            ClusterDto clusterDto = new ClusterDto();
            clusterDto.setClusterId(clusterDo.getId());
            clusterDto.setClusterName(clusterDo.getName());
            clusterDtoList.add(clusterDto);
        });
        return BaseResult.success(clusterDtoList);

    }


}
