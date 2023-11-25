package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.org.BaseOrgParam;
import com.hnkylin.cloud.manage.entity.req.vdc.*;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.resp.vdc.*;
import com.hnkylin.cloud.manage.service.VdcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/vdc")
@Slf4j
public class VdcCtrl {


    @Resource
    private VdcService vdcService;

    @PostMapping("/queryNotBindVdcByParentOrgId")
    public BaseResult<List<VdcDetailRespDto>> queryNotBindVdcByParentOrgId(@ModelCheck(notNull = true) @RequestBody BaseOrgParam baseOrgParam
            , @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.queryNotBindVdcByParentOrgId(baseOrgParam, loginUserVo));

    }


    @PostMapping("/queryClusterBaseResource")
    public BaseResult<String> queryClusterBaseResource(@ModelCheck(notNull = true) @RequestBody BaseClusterParam baseClusterParam
            , @LoginUser LoginUserVo loginUserVo) {
        vdcService.queryClusterBaseResource(baseClusterParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/vdcTreeByZone")
    @ParamCheck
    public BaseResult<List<ParentVdcRespDto>> vdcTreeByZone(@ModelCheck(notNull = true) @RequestBody BaseZoneParam baseZoneParam, @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.vdcTreeByZone(baseZoneParam, loginUserVo));

    }


    @PostMapping("/checkCreateVdc")
    @ParamCheck
    public BaseResult<VdcResourceRespDto> checkCreateVdc(@ModelCheck(notNull = true) @RequestBody CheckCreateVdcParam checkCreateVdcParam
            , @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.checkCreateVdc(checkCreateVdcParam, loginUserVo));

    }


    @PostMapping("/createVdc")
    @ParamCheck
    public BaseResult<String> createVdc(@ModelCheck(notNull = true) @RequestBody CreateVdcParam createVdcParam
            , @LoginUser LoginUserVo loginUserVo) {
        vdcService.createVdc(createVdcParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/vdcTree")
    public BaseResult<List<VdcTreeRespDto>> vdcTree(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.vdcTree(loginUserVo));

    }

    @PostMapping("/vdcDetail")
    @ParamCheck
    public BaseResult<VdcInfoRespDto> vdcDetail(@ModelCheck(notNull = true) @RequestBody BaseVdcParam baseVdcParam,
                                                @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.vdcDetail(baseVdcParam, loginUserVo));

    }


    @PostMapping("/deleteVdc")
    @ParamCheck
    public BaseResult<String> deleteVdc(@ModelCheck(notNull = true) @RequestBody BaseVdcParam baseVdcParam,
                                        @LoginUser LoginUserVo loginUserVo) {
        vdcService.deleteVdc(baseVdcParam, loginUserVo);
        return BaseResult.success(null);

    }


    @PostMapping("/modifyVdcDetail")
    @ParamCheck
    public BaseResult<ModifyVdcDetailRespDto> modifyVdcDetail(@ModelCheck(notNull = true) @RequestBody BaseVdcParam baseVdcParam,
                                                              @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.modifyVdcDetail(baseVdcParam, loginUserVo));

    }


    @PostMapping("/modifyVdc")
    @ParamCheck
    public BaseResult<String> modifyVdc(@ModelCheck(notNull = true) @RequestBody ModifyVdcParam modifyVdcParam
            , @LoginUser LoginUserVo loginUserVo) {
        vdcService.modifyVdc(modifyVdcParam, loginUserVo);
        return BaseResult.success(null);
    }


    @PostMapping("/modifyVdcResourceDetail")
    @ParamCheck
    public BaseResult<VdcModifyResourceRespDto> modifyVdcResourceDetail(@ModelCheck(notNull = true) @RequestBody BaseVdcParam baseVdcParam,
                                                                        @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(vdcService.modifyVdcResourceDetail(baseVdcParam.getVdcId(), loginUserVo));

    }

    @PostMapping("/applyModifyVdcResource")
    @ParamCheck
    public BaseResult<String> applyModifyVdcResource(@ModelCheck(notNull = true) @RequestBody ApplyModifyVdcResourceParam applyModifyVdcResourceParam,
                                                     @LoginUser LoginUserVo loginUserVo) {
        return vdcService.applyModifyVdcResource(applyModifyVdcResourceParam, loginUserVo);
    }
}
