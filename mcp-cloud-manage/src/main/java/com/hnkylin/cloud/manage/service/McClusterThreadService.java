package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.threadTask.McClusterNoDataThread;
import com.hnkylin.cloud.manage.threadTask.McClusterThread;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Service
public class McClusterThreadService {


    @Resource
    private McHttpService mcHttpService;


    @Resource
    private ThreadPoolTaskExecutor mcClusterInfoExecutor;

    /**
     * 多集群向mc发送带数据获取的请求
     *
     * @param clusterIdList
     * @param loginName
     */
    public List<String> threadGetMcResponse(List<Integer> clusterIdList,
                                            String loginName, String mcInterfaceUrl, List<Object> requestObjList) {
        //使用线程池
        List<FutureTask<String>> taskList = new ArrayList<>();

        for (int i = 0; i < clusterIdList.size(); i++) {
            McClusterThread thread = new McClusterThread(mcHttpService, loginName, clusterIdList.get(i), mcInterfaceUrl,
                    requestObjList.isEmpty() ? null : requestObjList.get(i));
            FutureTask<String> futureTask = new FutureTask<>(thread);
            taskList.add(futureTask);
            mcClusterInfoExecutor.submit(futureTask);
        }

        List<String> mcResponseList = new ArrayList<>();
        for (FutureTask<String> task : taskList) {
            try {
                String mcClusterResponse = task.get();
                mcResponseList.add(mcClusterResponse);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return mcResponseList;

    }

    /**
     * 多集群向mc发送无数据获取的请求
     *
     * @param clusterIdList
     * @param loginName
     */
    public List<MCResponseData<Object>> threadSendToMc(List<Integer> clusterIdList,
                                                       String loginName, String mcInterfaceUrl,
                                                       List<Object> requestObjList) {
        //使用线程池
        List<FutureTask<MCResponseData<Object>>> taskList = new ArrayList<>();

        for (int i = 0; i < clusterIdList.size(); i++) {
            McClusterNoDataThread thread = new McClusterNoDataThread(mcHttpService,
                    loginName, clusterIdList.get(i), mcInterfaceUrl, requestObjList.isEmpty() ? null :
                    requestObjList.get(i));
            FutureTask<MCResponseData<Object>> futureTask = new FutureTask<>(thread);
            taskList.add(futureTask);
            mcClusterInfoExecutor.submit(futureTask);
        }

        List<MCResponseData<Object>> mcResponseList = new ArrayList<>();
        for (FutureTask<MCResponseData<Object>> task : taskList) {
            try {
                MCResponseData<Object> responseData = task.get();
                mcResponseList.add(responseData);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return mcResponseList;

    }


}
