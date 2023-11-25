package com.hnkylin.cloud.manage.runner;

import com.hnkylin.cloud.manage.service.McNodeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KylinCloudManagerRunner implements ApplicationRunner {

    @Resource
    private McNodeService mcNodeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mcNodeService.initMcNodeListToCache();
    }


}
