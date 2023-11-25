package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudPermissionDo;
import com.hnkylin.cloud.core.domain.CloudRolePermissionDo;
import com.hnkylin.cloud.core.mapper.CloudRolePermissionMapper;
import com.hnkylin.cloud.core.service.CloudRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class CloudRolePermissionServiceImpl extends ServiceImpl<CloudRolePermissionMapper, CloudRolePermissionDo>
        implements CloudRolePermissionService {


    @Override
    public List<CloudRolePermissionDo> listRolePermissionByRoleId(Integer roleId) {
        CloudRolePermissionDo rolePermissionDo = new CloudRolePermissionDo();
        rolePermissionDo.setDeleteFlag(Boolean.FALSE);
        rolePermissionDo.setRoleId(roleId);
        Wrapper<CloudRolePermissionDo> wrapper = new QueryWrapper<>(rolePermissionDo);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void deleteRolePermissionByRole(Integer roleId, Integer deleteBy, Date deleteTime) {
        //删除角色拥有的权限
        CloudRolePermissionDo rolePermissionDo = new CloudRolePermissionDo();
        rolePermissionDo.setRoleId(roleId);
        rolePermissionDo.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudRolePermissionDo> rolePermissionWrapper = new QueryWrapper<>(rolePermissionDo);
        List<CloudRolePermissionDo> rolePermissionList = baseMapper.selectList(rolePermissionWrapper);
        if (!rolePermissionList.isEmpty()) {
            rolePermissionList.forEach(item -> {
                item.setDeleteFlag(Boolean.TRUE);
                item.setDeleteBy(deleteBy);
                item.setDeleteTime(deleteTime);
            });
            updateBatchById(rolePermissionList);
        }
    }

    @Override
    @Transactional
    public void insertRolePermission(Integer roleId, List<Integer> permissionIds, Integer createBy, Date createTime) {
        if (Objects.nonNull(permissionIds) && !permissionIds.isEmpty()) {
            List<CloudRolePermissionDo> rolePermissionList = new ArrayList<>(permissionIds.size());
            permissionIds.forEach(item -> {
                CloudRolePermissionDo cloudRolePermissionDo = CloudRolePermissionDo.builder()
                        .roleId(roleId).permissionId(item).build();
                cloudRolePermissionDo.setCreateBy(createBy);
                cloudRolePermissionDo.setCreateTime(createTime);
                rolePermissionList.add(cloudRolePermissionDo);

            });
            saveBatch(rolePermissionList);
        }
    }
}
