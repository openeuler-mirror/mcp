package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.manage.entity.resp.user.PageUserRespDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-11-23.
 */
public interface UserMapper extends BaseMapper<CloudUserDo> {


    /**
     * 分页查询用户
     *
     * @param orgIdList
     * @return
     */
    List<PageUserRespDto> pageUser(@Param("orgIdList") List<Integer> orgIdList, String searchKey);

    /**
     * 根据校色类型获取用户数量
     *
     * @param roleType
     * @return
     */
    Integer getUserCountByRoleType(@Param("orgIdList") List<Integer> orgIdList, @Param("roleType") Integer roleType);


}
