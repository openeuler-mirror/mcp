package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudDeptDo;

import java.util.List;

public interface CloudDeptService extends IService<CloudDeptDo> {

    List<CloudDeptDo> queryAllDeptList();

}
