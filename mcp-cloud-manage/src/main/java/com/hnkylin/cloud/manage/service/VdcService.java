package com.hnkylin.cloud.manage.service;


import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterBaseResource;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.org.BaseOrgParam;
import com.hnkylin.cloud.manage.entity.req.vdc.*;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.resp.vdc.*;

import java.util.List;

/**
 * 虚拟数据中心Vdc管理
 */
public interface VdcService {

    /**
     * 根据父组织ID，获取父组织对应VDC的子VDC列表(未绑定的)
     */
    List<VdcDetailRespDto> queryNotBindVdcByParentOrgId(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo);


    McClusterBaseResource queryClusterBaseResource(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo);


    /**
     * 获取可用区下可以绑定的VDC列表
     *
     * @param baseZoneParam
     * @return
     */
    List<ParentVdcRespDto> vdcTreeByZone(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo);


    /**
     * 检查可用区下上级VDC能否被绑定
     * 能绑定：返回上级VDC的资源情况
     *
     * @param checkCreateVdcParam
     * @param loginUserVo
     * @return
     */
    VdcResourceRespDto checkCreateVdc(CheckCreateVdcParam checkCreateVdcParam, LoginUserVo loginUserVo);


    /**
     * 创建VDC
     *
     * @param createVdcParam
     * @param loginUserVo
     */
    void createVdc(CreateVdcParam createVdcParam, LoginUserVo loginUserVo);


    /**
     * 获取vdc树形结构
     *
     * @param loginUserVo
     * @return
     */
    List<VdcTreeRespDto> vdcTree(LoginUserVo loginUserVo);


    /**
     * vdc详情
     *
     * @param baseVdcParam
     * @return
     */
    VdcInfoRespDto vdcDetail(BaseVdcParam baseVdcParam, LoginUserVo loginUserVo);


    /**
     * 编辑vdc时-获取vdc详情
     *
     * @param baseVdcParam
     * @param loginUserVo
     * @return
     */
    ModifyVdcDetailRespDto modifyVdcDetail(BaseVdcParam baseVdcParam, LoginUserVo loginUserVo);


    /**
     * 编辑vdc
     *
     * @param modifyVdcParam
     * @param loginUserVo
     */
    void modifyVdc(ModifyVdcParam modifyVdcParam, LoginUserVo loginUserVo);


    /**
     * 删除VDC
     *
     * @param baseVdcParam
     * @param loginUserVo
     */
    void deleteVdc(BaseVdcParam baseVdcParam, LoginUserVo loginUserVo);


    /**
     * 根据用户ID获取Vdc
     *
     * @param userId
     * @return
     */
    CloudVdcDo getUserOrgBindVdc(Integer userId);


    /**
     * 根据组织获取组织绑定的VDC
     *
     * @param orgId
     * @return
     */
    CloudVdcDo getVdcByOrgId(Integer orgId);


    /**
     * 根据用户ID查询用户可见vdc列表
     *
     * @param userId
     * @return
     */
    List<CloudVdcDo> visibleVdcListByUserId(Integer userId);

    /**
     * 根据用户ID查询用户可见vdc列表
     *
     * @param userId
     * @return
     */
    List<CloudVdcDo> visibleVdcListByUserIdAndZoneId(Integer userId, Integer zoneId);


    /**
     * 变更VDC资源-资源详情
     *
     * @param vdcId
     * @param loginUserVo
     * @return
     */
    VdcModifyResourceRespDto modifyVdcResourceDetail(Integer vdcId, LoginUserVo loginUserVo);


    /**
     * 申请变更VDC资源
     *
     * @param applyModifyVdcResourceParam
     * @param loginUserVo
     */
    BaseResult<String> applyModifyVdcResource(ApplyModifyVdcResourceParam applyModifyVdcResourceParam,
                                              LoginUserVo loginUserVo);


    /**
     * 获取VDC资源情况
     *
     * @return
     */
    VdcUsedResourceDto getVdcResourceInfo(Integer vdcId, LoginUserVo loginUserVo);


}
