package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.domain.CloudUserRoleDo;
import com.hnkylin.cloud.core.service.CloudUserRoleService;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.role.BaseRoleParam;
import com.hnkylin.cloud.manage.entity.resp.role.PermissionTreeDto;
import com.hnkylin.cloud.manage.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
@Slf4j
public class PermissionCtrl {

    @Resource
    private PermissionService permissionService;


    /**
     * 获取自定义平台管理最大可分配权限树
     *
     * @return
     */
    @PostMapping("/customPlatformRoleMaxPermission")
    public BaseResult<List<PermissionTreeDto>> customPlatformRoleMaxPermission() {
        List<PermissionTreeDto> permissionTree =
                permissionService.customPlatformRoleMaxPermission();
        return BaseResult.success(permissionTree);

    }


}
