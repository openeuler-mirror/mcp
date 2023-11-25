package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.cluster.CanBindClusterParam;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.CreateZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.ModifyZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.PageZoneParam;
import com.hnkylin.cloud.manage.entity.resp.cluster.PageClusterDetailDto;
import com.hnkylin.cloud.manage.entity.resp.zone.PageZoneRespDto;
import com.hnkylin.cloud.manage.entity.resp.zone.ZoneDetailDto;
import com.hnkylin.cloud.manage.entity.resp.zone.ZoneInfoDto;
import com.hnkylin.cloud.manage.service.ClusterService;
import com.hnkylin.cloud.manage.service.ZoneService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/zone")
public class ZoneCtrl {


    @Resource
    private ZoneService zoneService;

    @Resource
    private ClusterService clusterService;

    @PostMapping("/createZone")
    @ParamCheck
    public BaseResult<String> createZone(@ModelCheck(notNull = true) @RequestBody CreateZoneParam zoneParam,
                                         @LoginUser LoginUserVo loginUserVo) {

        zoneService.createZone(zoneParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/modifyZone")
    @ParamCheck
    public BaseResult<String> modifyZone(@ModelCheck(notNull = true) @RequestBody ModifyZoneParam modifyZoneParam,
                                         @LoginUser LoginUserVo loginUserVo) {

        zoneService.modifyZone(modifyZoneParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/pageZone")
    @ParamCheck
    public BaseResult<PageData<PageZoneRespDto>> pageZone(@ModelCheck(notNull = true) @RequestBody
                                                                  PageZoneParam pageZoneParam,
                                                          @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(zoneService.pageZone(pageZoneParam, loginUserVo));

    }


    @PostMapping("/modifyZoneDetail")
    @ParamCheck
    public BaseResult<ZoneInfoDto> modifyZoneDetail(@ModelCheck(notNull = true) @RequestBody BaseZoneParam baseZoneParam,
                                                    @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(zoneService.modifyZoneDetail(baseZoneParam, loginUserVo));

    }


    @PostMapping("/zoneDetail")
    @ParamCheck
    public BaseResult<ZoneDetailDto> zoneDetail(@ModelCheck(notNull = true) @RequestBody BaseZoneParam baseZoneParam,
                                                @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(zoneService.zoneDetail(baseZoneParam, loginUserVo));

    }


    /**
     * 可用区获取可以绑定的物理集群
     *
     * @param canBindClusterParam
     * @param loginUserVo
     * @return
     */
    @PostMapping("/canBindCluster")
    @ParamCheck
    public BaseResult<List<PageClusterDetailDto>> canBindCluster(@ModelCheck(notNull = true) @RequestBody
                                                                         CanBindClusterParam canBindClusterParam,
                                                                 @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.canBindCluster(canBindClusterParam, loginUserVo));

    }


    /**
     * 可用区已绑定的物理集群
     *
     * @param baseZoneParam
     * @param loginUserVo
     * @return
     */
    @PostMapping("/clusterListByZone")
    @ParamCheck
    public BaseResult<List<PageClusterDetailDto>> clusterListByZone(@ModelCheck(notNull = true) @RequestBody
                                                                            BaseZoneParam baseZoneParam,
                                                                    @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(clusterService.clusterListByZone(baseZoneParam, loginUserVo));

    }


    @PostMapping("/zoneList")
    public BaseResult<List<ZoneInfoDto>> zoneList(@LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(zoneService.zoneList(loginUserVo));
    }


    @PostMapping("/deleteZone")
    @ParamCheck
    public BaseResult<String> deleteZone(@ModelCheck(notNull = true) @RequestBody BaseZoneParam BaseZoneParam,
                                         @LoginUser LoginUserVo loginUserVo) {

        zoneService.deleteZone(BaseZoneParam, loginUserVo);
        return BaseResult.success(null);

    }

}
