package com.hnkylin.cloud.selfservice.schedule;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.domain.CloudUserMachineDo;
import com.hnkylin.cloud.core.service.CloudUserMachineService;
import com.hnkylin.cloud.core.service.CloudUserService;
import com.hnkylin.cloud.selfservice.config.MCConfigProperties;
import com.hnkylin.cloud.selfservice.service.McHttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 云服务器到期处理，定时任务
 */
@Configuration
@EnableScheduling
@Slf4j
public class UserServerVmExpireTask {

    @Resource
    private CloudUserMachineService cloudUserMachineService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private McHttpService mcHttpService;

    @Resource
    private CloudUserService cloudUserService;


    private final int maxThreadSize = 5;

    /**
     * 每天凌晨1点进行定时任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void runServerVmExpireTask() {
        log.info("runServerVmExpireTask-start");
        //查询出今天之前过期的云服务器
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setDeleteFlag(false);
        cloudUserMachineDo.setDeadlineFlag(false);
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        queryWrapper.le("deadline_time", new Date());
        List<CloudUserMachineDo> userMachineDoList =
                cloudUserMachineService.list(queryWrapper);
        log.info("runServerVmExpireTask-到期云服务器数量：" + userMachineDoList.size());

        if (userMachineDoList.size() > 0) {
            //线程池核心数控制
            int threadCoreSize = userMachineDoList.size() > maxThreadSize ? maxThreadSize : userMachineDoList.size();

            //定义线程池
            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(userMachineDoList.size());
            ThreadFactory threadFactory = new ServerVmExpireThreadFactory();

            ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCoreSize, threadCoreSize, 5, TimeUnit.MINUTES,
                    workQueue, threadFactory, new ThreadPoolExecutor.AbortPolicy());

            userMachineDoList.forEach(userMachine -> {
                ServerVmExpireThread thread = new ServerVmExpireThread(cloudUserMachineService, mcConfigProperties,
                        mcHttpService, cloudUserService, userMachine);
                executor.execute(thread);
            });
        }
    }


}
