package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudPermissionDo;
import com.hnkylin.cloud.manage.entity.resp.user.UserPermissionDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-11-24.
 */
public interface PermissionMapper extends BaseMapper<CloudPermissionDo> {


    List<CloudPermissionDo> listPermissionByRoleId(@Param("roleId") Integer roleId);


    List<UserPermissionDto> rolePermissionByRoleId(@Param("roleId") Integer roleId);
}
