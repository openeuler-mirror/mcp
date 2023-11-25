package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudWorkOrderDo;
import com.hnkylin.cloud.core.mapper.CloudWorkOrderMapper;
import com.hnkylin.cloud.core.service.CloudWorkOrderService;
import org.springframework.stereotype.Service;

@Service
public class CloudWorkOrderServiceImpl extends ServiceImpl<CloudWorkOrderMapper, CloudWorkOrderDo> implements
        CloudWorkOrderService {


}
