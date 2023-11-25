package com.hnkylin.cloud.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcDetailRespDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-11-23.
 */
public interface VdcMapper extends BaseMapper<CloudVdcDo> {


    /**
     * 根据父VDCID获取未绑定的vdc列表
     *
     * @param parentVdcId
     * @return
     */
    List<VdcDetailRespDto> getNotBindVdcListByParentId(@Param("parentVdcId") Integer parentVdcId);

}
