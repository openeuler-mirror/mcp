package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.org.BaseOrgParam;
import com.hnkylin.cloud.manage.entity.req.role.*;
import com.hnkylin.cloud.manage.entity.resp.role.*;
import com.hnkylin.cloud.manage.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/role")
@Slf4j
public class RoleCtrl {


    @Resource
    private RoleService roleService;


    @PostMapping("/createRole")
    @ParamCheck
    public BaseResult<String> createRole(@ModelCheck(notNull = true) @RequestBody CreateRoleParam createRoleParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        roleService.createPlatformRole(createRoleParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/deleteRole")
    @ParamCheck
    public BaseResult<String> deleteRole(@ModelCheck(notNull = true) @RequestBody BaseRoleParam baseRoleParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        roleService.deleteRole(baseRoleParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/modifyRole")
    @ParamCheck
    public BaseResult<String> modifyRole(@ModelCheck(notNull = true) @RequestBody ModifyRoleParam modifyRoleParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        roleService.modifyRole(modifyRoleParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/modifyRoleDetail")
    @ParamCheck
    public BaseResult<RoleDetailDto> modifyRoleDetail(@ModelCheck(notNull = true) @RequestBody BaseRoleParam baseRoleParam) {

        return BaseResult.success(roleService.modifyRoleDetail(baseRoleParam));

    }


    @PostMapping("/roleInfo")
    @ParamCheck
    public BaseResult<RoleInfoRespDto> roleInfo(@ModelCheck(notNull = true) @RequestBody BaseRoleParam baseRoleParam) {
        return BaseResult.success(roleService.roleInfo(baseRoleParam));

    }


    @PostMapping("/pageRole")
    @ParamCheck
    public BaseResult<PageData<PageRoleRespDto>> pageRole(@ModelCheck(notNull = true) @RequestBody
                                                                  RolePageParam rolePageParam) {

        return BaseResult.success(roleService.pageRole(rolePageParam));

    }


    @PostMapping("/listRole")
    @ParamCheck
    public BaseResult<List<CommonRoleDto>> listRole(@ModelCheck(notNull = true) @RequestBody
                                                            SearchRoleParam searchRoleParam,
                                                    @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(roleService.listRole(searchRoleParam, loginUserVo));

    }

    @PostMapping("/allocateRolePermission")
    public BaseResult<List<PermissionTreeDto>> allocateRolePermission(@RequestBody AllocateRolePermissionParam
                                                                              allocateRolePermissionParam,
                                                                      @LoginUser LoginUserVo loginUserVo) {
        roleService.allocateRolePermission(allocateRolePermissionParam, loginUserVo);
        return BaseResult.success(null);

    }


}
