package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudWorkOrderVdcCpuMemDo;
import com.hnkylin.cloud.core.domain.CloudWorkOrderVdcDo;
import com.hnkylin.cloud.core.mapper.CloudWorkOrderVdcCpuMemMapper;
import com.hnkylin.cloud.core.mapper.CloudWorkOrderVdcMapper;
import com.hnkylin.cloud.core.service.CloudWorkOrderVdcCpuMemService;
import com.hnkylin.cloud.core.service.CloudWorkOrderVdcService;
import org.springframework.stereotype.Service;

@Service
public class CloudWorkOrderVdcCpuMemServiceImpl extends ServiceImpl<CloudWorkOrderVdcCpuMemMapper,
        CloudWorkOrderVdcCpuMemDo>
        implements CloudWorkOrderVdcCpuMemService {


}
