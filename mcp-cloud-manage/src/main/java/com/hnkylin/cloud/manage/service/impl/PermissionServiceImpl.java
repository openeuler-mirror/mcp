package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.domain.CloudPermissionDo;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.core.domain.CloudRolePermissionDo;
import com.hnkylin.cloud.core.service.CloudPermissionService;
import com.hnkylin.cloud.core.service.CloudRolePermissionService;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.entity.resp.role.PermissionTreeDto;
import com.hnkylin.cloud.manage.mapper.PermissionMapper;
import com.hnkylin.cloud.manage.service.PermissionService;
import com.hnkylin.cloud.manage.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private CloudPermissionService cloudPermissionService;


    @Override
    public List<CloudPermissionDo> listPermissionByRoleId(Integer roleId) {
        return permissionMapper.listPermissionByRoleId(roleId);
    }


    @Override
    public List<PermissionTreeDto> customPlatformRoleMaxPermission() {

        //查询登录用户拥有的权限列表
        List<CloudPermissionDo> customPlatformRoleMaxPermissionList =
                cloudPermissionService.customPlatformRoleMaxPermission();


        List<PermissionTreeDto> permissionTreeDtoList = new ArrayList<>();
        for (CloudPermissionDo permissionDo : customPlatformRoleMaxPermissionList) {
            if (Objects.equals(permissionDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
                PermissionTreeDto permissionTreeDto = createCustomRolePermissionTreeDto(permissionDo,
                        customPlatformRoleMaxPermissionList);
                if (Objects.nonNull(permissionTreeDto)) {
                    permissionTreeDtoList.add(permissionTreeDto);
                }

            }
        }
        return permissionTreeDtoList;
    }

    /**
     * 创建PermissionTree对象
     */
    private PermissionTreeDto createCustomRolePermissionTreeDto(CloudPermissionDo permissionDo, List<CloudPermissionDo>
            customPlatformRoleMaxPermissionList) {
        PermissionTreeDto permissionTreeDto = PermissionTreeDto.builder()
                .permissionId(permissionDo.getId()).name(permissionDo.getName())
                .parentId(permissionDo.getParentId()).icon(permissionDo.getIcon())
                .routeKey(permissionDo.getRouteKey()).build();


        permissionTreeDto.setChildren(getChild(permissionDo.getId(), customPlatformRoleMaxPermissionList));
        return permissionTreeDto;
    }

    /**
     * 递归创建子权限
     */
    private List<PermissionTreeDto> getChild(Integer parentId,
                                             List<CloudPermissionDo> customPlatformRoleMaxPermissionList) {
        List<PermissionTreeDto> childList = new ArrayList<>();
        for (CloudPermissionDo permissionDo : customPlatformRoleMaxPermissionList) {
            if (Objects.equals(parentId, permissionDo.getParentId())) {
                PermissionTreeDto permissionTreeDto = createCustomRolePermissionTreeDto(permissionDo,
                        customPlatformRoleMaxPermissionList);
                if (Objects.nonNull(permissionTreeDto)) {
                    childList.add(permissionTreeDto);
                }
            }
        }
        return childList;
    }

    @Override
    public CloudPermissionDo getPermissionByRouteKey(String routeKey) {
        CloudPermissionDo cloudPermissionDo = new CloudPermissionDo();
        cloudPermissionDo.setRouteKey(routeKey);
        cloudPermissionDo.setDeleteFlag(false);
        QueryWrapper wrapper = new QueryWrapper(cloudPermissionDo);
        return cloudPermissionService.getOne(wrapper);
    }


}
