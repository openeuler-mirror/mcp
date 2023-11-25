package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudAlarmLogDo;
import com.hnkylin.cloud.core.mapper.CloudAlarmLogMapper;
import com.hnkylin.cloud.core.service.CloudAlarmLogService;
import org.springframework.stereotype.Service;

@Service
public class CloudAlarmLogServiceImpl extends ServiceImpl<CloudAlarmLogMapper, CloudAlarmLogDo>
        implements CloudAlarmLogService {


}
