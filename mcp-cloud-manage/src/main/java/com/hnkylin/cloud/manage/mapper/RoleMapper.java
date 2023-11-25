package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.manage.entity.resp.role.PageRoleRespDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-11-23.
 */
public interface RoleMapper extends BaseMapper<CloudRoleDo> {


    /**
     * 根据父VDCID获取未绑定的vdc列表
     *
     * @param
     * @return
     */
    List<PageRoleRespDto> pageRole();


    Integer getOrgUserIdList(@Param("orgId") Integer orgId);

}
