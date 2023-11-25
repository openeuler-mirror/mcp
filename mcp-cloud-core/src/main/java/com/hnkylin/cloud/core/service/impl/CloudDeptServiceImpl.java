package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudDeptDo;
import com.hnkylin.cloud.core.mapper.CloudDeptMapper;
import com.hnkylin.cloud.core.service.CloudDeptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CloudDeptServiceImpl extends ServiceImpl<CloudDeptMapper, CloudDeptDo> implements CloudDeptService {

    @Override
    public List<CloudDeptDo> queryAllDeptList() {
        CloudDeptDo deptDo = new CloudDeptDo();
        deptDo.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudDeptDo> wrapper = new QueryWrapper<>(deptDo);
        return baseMapper.selectList(wrapper);
    }
}
