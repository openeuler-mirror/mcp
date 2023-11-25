package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.CloudZoneDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.CreateZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.ModifyZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.PageZoneParam;
import com.hnkylin.cloud.manage.entity.resp.zone.PageZoneRespDto;
import com.hnkylin.cloud.manage.entity.resp.zone.ZoneDetailDto;
import com.hnkylin.cloud.manage.entity.resp.zone.ZoneInfoDto;

import java.util.List;

public interface ZoneService {

    /**
     * 创建可用区
     *
     * @param createZoneParam
     * @param loginUserVo
     */
    void createZone(CreateZoneParam createZoneParam, LoginUserVo loginUserVo);


    /**
     * 编辑可用区
     *
     * @param modifyZoneParam
     * @param loginUserVo
     */
    void modifyZone(ModifyZoneParam modifyZoneParam, LoginUserVo loginUserVo);


    /**
     * 分页查询可用区
     *
     * @param pageZoneParam
     * @param loginUserVo
     * @return
     */
    PageData<PageZoneRespDto> pageZone(PageZoneParam pageZoneParam, LoginUserVo loginUserVo);

    /**
     * 编辑可用区时获取可用区详情
     *
     * @param baseZoneParam
     * @param loginUserVo
     * @return
     */
    ZoneInfoDto modifyZoneDetail(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo);


    /**
     * 可用区详情
     *
     * @param baseZoneParam
     * @param loginUserVo
     * @return
     */
    ZoneDetailDto zoneDetail(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo);


    /**
     * 获取登录用可以绑定的可用区
     */
    List<ZoneInfoDto> zoneList(LoginUserVo loginUserVo);

    /**
     * 根据集群获取可用区
     *
     * @param clusterId
     * @return
     */
    CloudZoneDo getZoneByClusterId(Integer clusterId);


    /**
     * 查询用户可见可用区
     *
     * @param userId
     * @return
     */
    List<CloudZoneDo> visibleZoneListByUserId(Integer userId);

    /**
     * 删除可用区
     *
     * @param baseZoneParam
     * @param loginUserVo
     */
    void deleteZone(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo);
}
