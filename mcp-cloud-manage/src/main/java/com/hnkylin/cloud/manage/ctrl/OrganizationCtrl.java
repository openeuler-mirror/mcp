package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.org.*;
import com.hnkylin.cloud.manage.entity.resp.org.OrgDetailRespDto;
import com.hnkylin.cloud.manage.entity.resp.org.OrgSummaryRespDto;
import com.hnkylin.cloud.manage.entity.resp.org.OrganizationRespDto;
import com.hnkylin.cloud.manage.entity.resp.org.ParentOrgRespDto;
import com.hnkylin.cloud.manage.entity.resp.role.OrgStatisticTreeDto;
import com.hnkylin.cloud.manage.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/org")
@Slf4j
public class OrganizationCtrl {

    @Resource
    private OrgService orgService;


    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构
     *
     * @param loginUserVo
     * @return
     */
    @PostMapping("/queryParentOrg")
    public BaseResult<List<ParentOrgRespDto>> queryParentOrg(@ModelCheck(notNull = true) @RequestBody QueryParentOrgParam queryParentOrgParam,
                                                             @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(orgService.queryParentOrg(queryParentOrgParam, loginUserVo));

    }

    /**
     * 组织管理-主列表
     *
     * @param loginUserVo
     * @return
     */
    @PostMapping("/queryOrgList")
    public BaseResult<List<OrganizationRespDto>> queryOrgList(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(orgService.queryOrgList(loginUserVo));

    }


    @PostMapping("/createOrg")
    @ParamCheck
    public BaseResult<String> createOrg(@ModelCheck(notNull = true) @RequestBody CreateOrgParam createOrgParam,
                                        @LoginUser LoginUserVo loginUserVo) {
        orgService.createOrg(createOrgParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/deleteOrg")
    @ParamCheck
    public BaseResult<String> deleteOrg(@ModelCheck(notNull = true) @RequestBody BaseOrgParam baseOrgParam,
                                        @LoginUser LoginUserVo loginUserVo) {
        orgService.deleteOrg(baseOrgParam, loginUserVo);
        return BaseResult.success(null);
    }

    /**
     * 编辑组织时-获取组织详情
     */
    @PostMapping("/orgDetail")
    @ParamCheck
    public BaseResult<OrgDetailRespDto> orgDetail(@ModelCheck(notNull = true) @RequestBody BaseOrgParam baseOrgParam,
                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(orgService.orgDetail(baseOrgParam, loginUserVo));
    }


    /**
     * 编辑组织
     */
    @PostMapping("/modifyOrg")
    @ParamCheck
    public BaseResult<String> modifyOrg(@ModelCheck(notNull = true) @RequestBody ModifyOrgParam modifyOrgParam,
                                        @LoginUser LoginUserVo loginUserVo) {

        orgService.modifyOrg(modifyOrgParam, loginUserVo);
        return BaseResult.success(null);
    }


    /**
     * 修改顶层组织名称
     */
    @PostMapping("/modifyTopOrgName")
    @ParamCheck
    public BaseResult<String> modifyTopOrgName(@ModelCheck(notNull = true) @RequestBody ModifyTopOrgNameParam modifyOrgParam,
                                               @LoginUser LoginUserVo loginUserVo) {

        orgService.modifyTopOrgName(modifyOrgParam, loginUserVo);
        return BaseResult.success(null);
    }

    /**
     * 组织管理-组织统计树
     *
     * @param loginUserVo
     * @return
     */
    @PostMapping("/orgStatisticTree")
    @ParamCheck
    public BaseResult<List<OrgStatisticTreeDto>> orgStatisticTree(@ModelCheck(notNull = true) @RequestBody
                                                                          OrgStatisticTreeParam orgStatisticTreeParam,
                                                                  @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(orgService.orgStatisticTree(loginUserVo, orgStatisticTreeParam.getStatisticType()));

    }


    @PostMapping("/orgSummary")
    @ParamCheck
    public BaseResult<OrgSummaryRespDto> orgSummary(@ModelCheck(notNull = true) @RequestBody
                                                            BaseOrgParam baseOrgParam,
                                                    @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(orgService.orgSummary(baseOrgParam, loginUserVo));

    }


}
