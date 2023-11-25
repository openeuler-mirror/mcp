package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudAlarmConfigDo;
import com.hnkylin.cloud.core.mapper.CloudAlarmConfigMapper;
import com.hnkylin.cloud.core.service.CloudAlarmConfigService;
import org.springframework.stereotype.Service;

@Service
public class CloudAlarmConfigServiceImpl extends ServiceImpl<CloudAlarmConfigMapper, CloudAlarmConfigDo>
        implements CloudAlarmConfigService {


}
