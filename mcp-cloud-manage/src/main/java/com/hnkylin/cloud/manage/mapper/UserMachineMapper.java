package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.manage.entity.resp.user.UserMachineCountDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-11-27.
 */
public interface UserMachineMapper extends BaseMapper<CloudVdcDo> {


    List<UserMachineCountDto> userMachineCountGroupByUserId(@Param("userIds") List<Integer> userIds);

}
