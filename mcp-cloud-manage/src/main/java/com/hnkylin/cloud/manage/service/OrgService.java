package com.hnkylin.cloud.manage.service;


import com.hnkylin.cloud.core.domain.CloudOrganizationDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.org.*;
import com.hnkylin.cloud.manage.entity.resp.org.OrgDetailRespDto;
import com.hnkylin.cloud.manage.entity.resp.org.OrgSummaryRespDto;
import com.hnkylin.cloud.manage.entity.resp.org.OrganizationRespDto;
import com.hnkylin.cloud.manage.entity.resp.org.ParentOrgRespDto;
import com.hnkylin.cloud.manage.entity.resp.role.OrgStatisticTreeDto;
import com.hnkylin.cloud.manage.enums.OrgStatisticTreeType;

import java.util.List;

/**
 * 组织管理
 */
public interface OrgService {

    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构
     */
    List<ParentOrgRespDto> queryParentOrg(QueryParentOrgParam queryParentOrgParam, LoginUserVo loginUserVo);

    /**
     * 创建组织
     */
    void createOrg(CreateOrgParam createOrgParam, LoginUserVo loginUserVo);


    /**
     * 组织管理-主列表
     */
    List<OrganizationRespDto> queryOrgList(LoginUserVo loginUserVo);


    /**
     * 删除组织
     */
    void deleteOrg(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo);


    /**
     * 编辑组织时-获取组织详情
     */
    OrgDetailRespDto orgDetail(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo);


    /**
     * 编辑组织
     */
    void modifyOrg(ModifyOrgParam modifyOrgParam, LoginUserVo loginUserVo);

    /**
     * 修改顶层组织名称
     */
    void modifyTopOrgName(ModifyTopOrgNameParam modifyOrgParam, LoginUserVo loginUserVo);


    /**
     * 通用组织统计数
     *
     * @param loginUserVo
     * @param orgTreeType
     * @return
     */
    List<OrgStatisticTreeDto> orgStatisticTree(LoginUserVo loginUserVo, OrgStatisticTreeType orgTreeType);


    /**
     * 获取组织的子组织ID集合
     *
     * @param parentOrgId
     * @return
     */
    List<Integer> getOrgChildIdList(Integer parentOrgId);


    /**
     * 根据vdcId获取对应的组织
     *
     * @param vdcId
     * @return
     */
    CloudOrganizationDo getOrgByVdcId(Integer vdcId);


    /**
     * 用户是否拥有该组织的操作权限
     *
     * @param userId
     * @param orgId
     * @return
     */
    boolean userHasOrgPermission(Integer userId, Integer orgId);


    /**
     * 根据用户ID获取组织
     */
    CloudOrganizationDo getByUserId(Integer userId);


    /**
     * 组织概要
     *
     * @param baseOrgParam
     * @param loginUserVo
     * @return
     */
    OrgSummaryRespDto orgSummary(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo);

    /**
     * 云服务器转移可获取的组织列表
     *
     * @param loginUserVo
     * @param zoneId      可用区ID
     * @return
     */
    List<ParentOrgRespDto> transferCanSelectOrg(LoginUserVo loginUserVo, Integer zoneId);


}
