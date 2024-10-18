package com.hnkylin.cloud.manage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.config.KcpHaProperties;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.CloudOperateLogDo;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.KcpHaNodeDo;
import com.hnkylin.cloud.core.entity.req.kcpha.AddSlaveKcpParam;
import com.hnkylin.cloud.core.entity.req.kcpha.ChangeKcpRoleParam;
import com.hnkylin.cloud.core.entity.resp.kcpha.AddSlaveResp;
import com.hnkylin.cloud.core.entity.resp.kcpha.KcpHaResp;
import com.hnkylin.cloud.core.entity.resp.kcpha.KcpNodeResp;
import com.hnkylin.cloud.core.enums.*;
import com.hnkylin.cloud.core.service.CloudHaNodeService;
import com.hnkylin.cloud.core.service.CloudOperateLogService;
import com.hnkylin.cloud.core.service.CloudUserService;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseHAConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import jdk.nashorn.api.scripting.JSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

@Service
@Slf4j
public class KcpHaService {


    @Resource
    private RestTemplate httpsRestTemplate;

    @Resource
    private KcpHaProperties kcpHaProperties;

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private CloudHaNodeService cloudHaNodeService;

    @Resource
    private CloudOperateLogService cloudOperateLogService;


    public KcpHaResp getSlaveKcpInfo() {
        KcpHaResp kcpHaResp = new KcpHaResp();
        kcpHaResp.setNodeRole(getHaConfigInfo("role"));
        if (Objects.equals(kcpHaProperties.getRole(), KcpHaNodeRole.master.name())) {
            KcpHaNodeDo slaveNode = cloudHaNodeService.getKcpNodeByNodeType(KcpHaNodeRole.slave);
            if (Objects.nonNull(slaveNode)) {
                kcpHaResp.setSlaveIp(slaveNode.getIpAddress());
            }
            kcpHaResp.setStatus(KcpHaNodeStatus.ONLINE.name());
        } else {
            try {
                KcpHaNodeDo masterNode = cloudHaNodeService.getKcpNodeByNodeType(KcpHaNodeRole.master);
                if (Objects.nonNull(masterNode)) {
                    kcpHaResp.setMasterIp(masterNode.getIpAddress());
                    kcpHaResp.setStatus(KcpHaNodeStatus.ONLINE.name());
                } else {
                    kcpHaResp.setMasterIp(getHaConfigInfo("master"));
                    kcpHaResp.setStatus(KcpHaNodeStatus.INIT.name());
                }
            } catch (Exception e) {
                log.info("getSlaveKcpInfo-error", e);
                kcpHaResp.setMasterIp(getHaConfigInfo("master"));
                kcpHaResp.setStatus(KcpHaNodeStatus.INIT.name());
            }
        }

        return kcpHaResp;
    }


    /**
     * 获取主备配置文件中值
     *
     * @param key
     * @return
     */
    public String getHaConfigInfo(String key) {
        return KcpCommonUtil.getHaConfigInfo(kcpHaProperties.getConfigPath(), key);
    }

    /**
     * 调用另一个kcp节点,获取节点状态
     *
     * @param kcpNodeIp
     * @return
     */
    public KcpHaResp getOtherKcpInfo(String kcpNodeIp) {

        KcpHaResp kcpHaResp = null;
        KcpResponseData<Object> result = sendOtherKcp(kcpNodeIp, null,
                kcpHaProperties.getSlaveKcpInfo());
        if (Objects.nonNull(result) && Objects.equals(result.getCode(), HttpCode.SUCCESS)) {
            kcpHaResp = JSON.parseObject(JSON.toJSONString(result.getData()),
                    KcpHaResp.class);
        }
        return kcpHaResp;
    }


    /**
     * 通用请求另一个kcp请求
     *
     * @param kcpNodeIp
     * @param requestObj
     * @param httpUrl
     * @return
     */
    private KcpResponseData<Object> sendOtherKcp(String kcpNodeIp, Object requestObj, String httpUrl) {
        KcpResponseData<Object> result = null;
        try {
            HttpHeaders httpHeaders = createKcpHeaders();
            log.info("sendToKcp-headers:{}", JSON.toJSONString(httpHeaders));
            requestObj = Objects.isNull(requestObj) ? new Object() : requestObj;
            JSONObject reqBodyObj = JSONObject.parseObject(JSON.toJSONString(requestObj));

            HttpEntity<JSObject> httpEntity = new HttpEntity(reqBodyObj, httpHeaders);

            String requestUrl =
                    HttpTypes.HTTPS.getValue() + kcpNodeIp + KylinCommonConstants.KCP_PORT + httpUrl;
            log.info("sendOtherKcp-url:{},body:{}", requestUrl, reqBodyObj.toJSONString());
            result = httpsRestTemplate.postForObject(requestUrl, httpEntity,
                    KcpResponseData.class);
            log.info("sendToKcp-response:{}", JSON.toJSONString(result));
        } catch (Exception exception) {
            log.info("sendOtherKcp-" + httpUrl + "-error", exception);
        }
        return result;
    }

    /**
     * 封装主备KCP 请求头
     *
     * @return
     */
    private HttpHeaders createKcpHeaders() {
        Long current = System.currentTimeMillis();
        String authToken = SHAUtil.getSHA256(current + KylinCommonConstants.KCP_OPEN_API_SECRET);
        HttpHeaders headers = new HttpHeaders();
        headers.add(KylinCommonConstants.KCP_OPEN_API_INVOKE_TIME, current.toString());
        headers.add(KylinCommonConstants.KCP_OPEN_API_TOKEN, authToken);
        return headers;
    }


    /**
     * 主节点变成备节点
     *
     * @param kcpHaNodeDo
     */
    public void masterToSlave(KcpHaNodeDo kcpHaNodeDo) {
        try {
            log.info("start-masterToSlave:" + kcpHaNodeDo.getIpAddress());
            ProcessBuilder pb = new ProcessBuilder("sh", kcpHaProperties.getMasterToSlaveShell(),
                    kcpHaNodeDo.getIpAddress());

            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.start();
        } catch (Exception e) {
            log.error("initSlave-error", e);
        }
    }

    /**
     * 添加备kcp
     *
     * @param addSlaveKcpParam
     * @param loginUserVo
     */
    public void addSlave(AddSlaveKcpParam addSlaveKcpParam, LoginUserVo loginUserVo) {

        //先判断是否已经加入过备节点
        KcpHaNodeDo kcpHaNodeDo = new KcpHaNodeDo();
        kcpHaNodeDo.setDeleteFlag(false);
        Wrapper<KcpHaNodeDo> wrapper = new QueryWrapper<>(kcpHaNodeDo);
        List<KcpHaNodeDo> kcoNodeList = cloudHaNodeService.list(wrapper);

        KcpHaNodeDo slaveNode = kcoNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.slave.name())).findFirst().orElse(null);
        if (Objects.nonNull(slaveNode)) {
            throw new KylinException(KylinHttpResponseHAConstants.EXIST_SLAVE);
        }

        String localIp = getLocalIp();
        if (Objects.equals(localIp, addSlaveKcpParam.getSlaveIp())) {
            throw new KylinException(KylinHttpResponseHAConstants.ADD_SLAVE_ERROR);
        }

        KcpHaNodeDo masterNode = kcoNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.master.name())).findFirst().orElse(null);

        //请求备节点KCP，校验账号密码
        AddSlaveResp addSlaveResp = null;
        KcpResponseData<Object> result = sendOtherKcp(addSlaveKcpParam.getSlaveIp(), addSlaveKcpParam,
                kcpHaProperties.getCheckPassword());
        if (Objects.nonNull(result) && Objects.equals(result.getCode(), HttpCode.SUCCESS)) {
            addSlaveResp = JSON.parseObject(JSON.toJSONString(result.getData()),
                    AddSlaveResp.class);
            if (!addSlaveResp.getAddResult()) {
                throw new KylinException(KylinHttpResponseHAConstants.SLAVE_PASSWORD_ERROR);
            }


            //请求备kcp进行初始化操作
            ChangeKcpRoleParam kcpRoleParam = new ChangeKcpRoleParam();
            kcpRoleParam.setMasterKcpIp(masterNode.getIpAddress());

            KcpResponseData<Object> initSlaveResult = sendOtherKcp(addSlaveKcpParam.getSlaveIp(), kcpRoleParam,
                    kcpHaProperties.getInitSlave());

            if (Objects.nonNull(initSlaveResult) && Objects.equals(initSlaveResult.getCode(), HttpCode.SUCCESS)) {
                slaveNode = new KcpHaNodeDo();
                slaveNode.setIpAddress(addSlaveKcpParam.getSlaveIp());
                slaveNode.setNodeType(KcpHaNodeRole.slave.name());
                slaveNode.setStatus(KcpHaNodeStatus.INIT.name());
                slaveNode.setCreateBy(loginUserVo.getUserId());
                slaveNode.setCreateTime(new Date());
                cloudHaNodeService.save(slaveNode);
                createKcpHaLog(slaveNode, loginUserVo, OperateLogAction.KCP_HA_ADD_SLAVE);
            } else {
                throw new KylinException(KylinHttpResponseHAConstants.ADD_SLAVE_ERROR);
            }
        } else {
            throw new KylinException(KylinHttpResponseHAConstants.ADD_SLAVE_ERROR);
        }

    }


    /**
     * 插入容灾计划站点日志
     */
    private CloudOperateLogDo createKcpHaLog(KcpHaNodeDo kcpHaNodeDo, LoginUserVo loginUserVo,
                                             OperateLogAction action) {
        CloudOperateLogDo operateLogDo = new CloudOperateLogDo();
        operateLogDo.setParentId(0);
        operateLogDo.setType(OperateLogType.KCP_HA.name());
        operateLogDo.setAction(action.name());
        operateLogDo.setStatus(OperateLogStatus.SUCCESS.name());
        operateLogDo.setPercent(KylinCommonConstants.PERCENT_100);
        operateLogDo.setObjId(kcpHaNodeDo.getId());
        operateLogDo.setObjName(kcpHaNodeDo.getIpAddress());
        operateLogDo.setMcTaskId(0L);
        operateLogDo.setDetail(kcpHaNodeDo.getIpAddress());
        operateLogDo.setClusterId(0);
        operateLogDo.setClientIp(loginUserVo.getClientIp());
        operateLogDo.setCreateBy(loginUserVo.getUserId());
        operateLogDo.setCreateTime(new Date());
        operateLogDo.setEndTime(new Date());
        cloudOperateLogService.save(operateLogDo);
        return operateLogDo;
    }

    /**
     * 校验账号密码
     *
     * @param addSlaveKcpParam
     */
    public AddSlaveResp checkNameAndPassword(AddSlaveKcpParam addSlaveKcpParam) {
        CloudUserDo userDo = cloudUserService.queryUserByUserName(addSlaveKcpParam.getSysadmin());
        AddSlaveResp addSlaveResp = new AddSlaveResp();
        addSlaveResp.setAddResult(false);
        if (Objects.equals(addSlaveKcpParam.getPassword(), userDo.getPassword())) {
            addSlaveResp.setAddResult(true);
        }
        return addSlaveResp;
    }

    /**
     * 初始化备KCP
     *
     * @param addSlaveKcpParam
     */
    public void initSlave(ChangeKcpRoleParam addSlaveKcpParam) {
        log.info("start-init-slave,masteIp:" + addSlaveKcpParam.getMasterKcpIp());

        String threadName = "initSlave-thread";
        new Thread(() -> {
            try {

                ProcessBuilder pb = new ProcessBuilder("sh", kcpHaProperties.getInitSlaveShell(),
                        addSlaveKcpParam.getMasterKcpIp());
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
                pb.start();
            } catch (Exception e) {
                log.error("initSlave-error", e);
            }
        }, threadName).start();

    }

    /**
     * 主kcp删除了备KCP后，备KCP需要进行重置，
     */
    public void resetSlave() {
        log.info("resetSlave:");

        String threadName = "resetSlave-thread";
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("sh", kcpHaProperties.getResetSlaveShell());
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
                pb.start();
            } catch (Exception e) {
                log.error("resetSlave-error", e);
            }
        }, threadName).start();
    }

    /**
     * 主备kcp列表
     *
     * @return
     */
    public List<KcpNodeResp> nodeList() {

        List<KcpNodeResp> nodeRespList = new ArrayList<>();

        if (Objects.equals(kcpHaProperties.getRole(), KcpHaNodeRole.master.name())) {
            nodeRespList = masterNodeList();
        } else {
            nodeRespList = slaveNodeList();
        }
        return nodeRespList;
    }

    /**
     * 主节点-获取列表
     *
     * @return
     */
    private List<KcpNodeResp> masterNodeList() {

        KcpHaNodeDo kcpHaNodeDo = new KcpHaNodeDo();
        kcpHaNodeDo.setDeleteFlag(false);
        Wrapper<KcpHaNodeDo> wrapper = new QueryWrapper<>(kcpHaNodeDo);
        List<KcpHaNodeDo> kcpNodeList = cloudHaNodeService.list(wrapper);
        List<KcpNodeResp> nodeRespList = new ArrayList<>();

        //主kcp
        KcpHaNodeDo masterNode = kcpNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.master.name())).findFirst().orElse(null);


        KcpNodeResp masterNodeResp = new KcpNodeResp();
        masterNodeResp.setIpAddress(masterNode.getIpAddress());
        masterNodeResp.setNodeType(masterNode.getNodeType());
        masterNodeResp.setStatus(masterNode.getStatus());
        //备KCP
        KcpHaNodeDo slaveNode = kcpNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.slave.name())).findFirst().orElse(null);
        if (Objects.nonNull(slaveNode)) {
            KcpNodeResp slaveNodeResp = new KcpNodeResp();
            slaveNodeResp.setIpAddress(slaveNode.getIpAddress());
            slaveNodeResp.setNodeType(slaveNode.getNodeType());
            KcpHaResp kcpHaResp = null;
            KcpResponseData<Object> slaveNodeInfo = sendOtherKcp(slaveNode.getIpAddress(), null,
                    kcpHaProperties.getSlaveKcpInfo());
            if (Objects.nonNull(slaveNodeInfo) && Objects.equals(slaveNodeInfo.getCode(), HttpCode.SUCCESS)) {
                kcpHaResp = JSON.parseObject(JSON.toJSONString(slaveNodeInfo.getData()),
                        KcpHaResp.class);
            }

            if (!Objects.equals(slaveNode.getStatus(), KcpHaNodeStatus.INIT.name())) {
                if (Objects.nonNull(kcpHaResp)) {
                    slaveNodeResp.setStatus(KcpHaNodeStatus.ONLINE.name());
                } else {
                    slaveNodeResp.setStatus(KcpHaNodeStatus.OFFLINE.name());
                }
            } else {
                //备KCP正在初始化中
                if (Objects.isNull(kcpHaResp) || Objects.equals(kcpHaResp.getNodeRole(), KcpHaNodeRole.master.name()) ||
                        Objects.equals(kcpHaResp.getStatus(), KcpHaNodeStatus.INIT.name())) {
                    masterNodeResp.setStatus(KcpHaNodeStatus.INIT.name());
                    slaveNodeResp.setStatus(KcpHaNodeStatus.INIT.name());
                } else {
                    slaveNode.setStatus(KcpHaNodeStatus.ONLINE.name());
                    cloudHaNodeService.updateById(slaveNode);

                    masterNodeResp.setStatus(KcpHaNodeStatus.ONLINE.name());
                    slaveNodeResp.setStatus(KcpHaNodeStatus.ONLINE.name());
                }
            }
            nodeRespList.add(masterNodeResp);
            nodeRespList.add(slaveNodeResp);
        } else {
            nodeRespList.add(masterNodeResp);
        }
        return nodeRespList;
    }


    /**
     * 主节点-获取列表
     *
     * @return
     */
    private List<KcpNodeResp> slaveNodeList() {
        List<KcpHaNodeDo> kcpNodeList = new ArrayList<>();
        try {
            KcpHaNodeDo kcpHaNodeDo = new KcpHaNodeDo();
            kcpHaNodeDo.setDeleteFlag(false);
            Wrapper<KcpHaNodeDo> wrapper = new QueryWrapper<>(kcpHaNodeDo);
            kcpNodeList = cloudHaNodeService.list(wrapper);
        } catch (Exception e) {
            kcpNodeList = new ArrayList<>();
        }


        List<KcpNodeResp> nodeRespList = new ArrayList<>();

        //备KCP
        KcpHaNodeDo slaveNode = kcpNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.slave.name())).findFirst().orElse(null);


        KcpNodeResp slaveNodeResp = new KcpNodeResp();
        KcpNodeResp masterNodeResp = new KcpNodeResp();
        if (Objects.nonNull(slaveNode)) {
            slaveNodeResp.setIpAddress(slaveNode.getIpAddress());
            slaveNodeResp.setNodeType(slaveNode.getNodeType());
            slaveNodeResp.setStatus(slaveNode.getStatus());
            //主kcp
            KcpHaNodeDo masterNode = kcpNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                    KcpHaNodeRole.master.name())).findFirst().orElse(null);
            if (Objects.nonNull(masterNode)) {
                masterNodeResp.setIpAddress(masterNode.getIpAddress());
                masterNodeResp.setNodeType(masterNode.getNodeType());
                masterNodeResp.setStatus(masterNode.getStatus());

                if (!Objects.equals(slaveNode.getStatus(), KcpHaNodeStatus.INIT.name())) {
                    //调用主节点-获取主节点状态
                    KcpResponseData<Object> result = sendOtherKcp(masterNode.getIpAddress(), null,
                            kcpHaProperties.getSlaveKcpInfo());
                    KcpHaResp kcpHaResp = null;
                    if (Objects.nonNull(result) && Objects.equals(result.getCode(), HttpCode.SUCCESS)) {
                        kcpHaResp = JSON.parseObject(JSON.toJSONString(result.getData()),
                                KcpHaResp.class);
                    }
                    if (Objects.nonNull(kcpHaResp)) {
                        masterNodeResp.setStatus(KcpHaNodeStatus.ONLINE.name());
                    } else {
                        masterNodeResp.setStatus(KcpHaNodeStatus.OFFLINE.name());
                    }
                } else {
                    masterNodeResp.setStatus(KcpHaNodeStatus.INIT.name());
                }
            } else {
                masterNodeResp.setIpAddress(getHaConfigInfo("master"));
                masterNodeResp.setNodeType(KcpHaNodeRole.master.name());
                masterNodeResp.setStatus(KcpHaNodeStatus.INIT.name());
                slaveNode.setStatus(KcpHaNodeStatus.INIT.name());
            }

        } else {
            slaveNodeResp.setIpAddress(getLocalIp());
            slaveNodeResp.setNodeType(KcpHaNodeRole.slave.name());
            slaveNode.setStatus(KcpHaNodeStatus.INIT.name());

            masterNodeResp.setIpAddress(getHaConfigInfo("master"));
            masterNodeResp.setNodeType(KcpHaNodeRole.master.name());
            masterNodeResp.setStatus(KcpHaNodeStatus.INIT.name());
        }
        nodeRespList.add(masterNodeResp);
        nodeRespList.add(slaveNodeResp);
        return nodeRespList;
    }


    /**
     * 备kcp升级为主kcp
     */
    public void changeToMaster() {
        KcpHaNodeDo kcpHaNodeDo = new KcpHaNodeDo();
        kcpHaNodeDo.setDeleteFlag(false);
        Wrapper<KcpHaNodeDo> wrapper = new QueryWrapper<>(kcpHaNodeDo);
        List<KcpHaNodeDo> kcpNodeList = cloudHaNodeService.list(wrapper);

        KcpHaNodeDo masterNode = kcpNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.master.name())).findFirst().orElse(null);

        KcpHaNodeDo slaveNode = kcpNodeList.stream().filter(item -> Objects.equals(item.getNodeType(),
                KcpHaNodeRole.slave.name())).findFirst().orElse(null);

        if (Objects.nonNull(masterNode) && Objects.nonNull(slaveNode)) {

            //调用主节点-获取主节点状态
            KcpResponseData<Object> result = sendOtherKcp(masterNode.getIpAddress(), null,
                    kcpHaProperties.getSlaveKcpInfo());

            //只有主节点离线才能切主
            if (Objects.nonNull(result)) {
                throw new KylinException(KylinHttpResponseHAConstants.MASTER_ONLINE_NOT_CHANGE);
            }
            executeChangeToMasterCommand(masterNode, slaveNode);

        } else {
            throw new KylinException(KylinHttpResponseHAConstants.CHANGE_MASTER_ERROR);
        }

    }

    private void executeChangeToMasterCommand(KcpHaNodeDo masterNode, KcpHaNodeDo slaveNode) {
        log.info("executeChangeToMasterCommand:" + slaveNode.getIpAddress());

        String threadName = "initSlave-thread";
        new Thread(() -> {
            try {

                ProcessBuilder pb = new ProcessBuilder("sh", kcpHaProperties.getSlaveToMasterShell(),
                        slaveNode.getIpAddress(), masterNode.getIpAddress());
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
                pb.start();
            } catch (Exception e) {
                log.error("initSlave-error", e);
            }
        }, threadName).start();

    }

    /**
     * 删除备KCP
     *
     * @param loginUserVo
     */
    public void deleteSlave(LoginUserVo loginUserVo) {


        KcpHaNodeDo slaveNode = cloudHaNodeService.getKcpNodeByNodeType(KcpHaNodeRole.slave);
        //初始化状态下，不能删除备kcp
        if (Objects.nonNull(slaveNode) && !Objects.equals(slaveNode.getStatus(), KcpHaNodeStatus.INIT.name())) {
            //先判断备节点是否在线
            KcpResponseData<Object> result = sendOtherKcp(slaveNode.getIpAddress(), null,
                    kcpHaProperties.getSlaveKcpInfo());
            if (Objects.nonNull(result)) {
                //备节点在线，则调用备节点接口。通知其进行数据库重置
                sendOtherKcp(slaveNode.getIpAddress(), null, kcpHaProperties.getResetSlave());
            }
            createKcpHaLog(slaveNode, loginUserVo, OperateLogAction.KCP_HA_DELETE_SLAVE);
            slaveNode.setDeleteFlag(true);
            slaveNode.setDeleteBy(loginUserVo.getUserId());
            cloudHaNodeService.updateById(slaveNode);
        } else {
            throw new KylinException(KylinHttpResponseHAConstants.NOT_DELETE_SLAVE);
        }
    }

    /**
     * 获取本级IP
     *
     * @return
     */
    public String getLocalIp() {
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = (NetworkInterface) enumeration.nextElement();
        // 排除虚拟接口
        if (network.getName().startsWith("virbr")
            || // 排除virbr0等虚拟桥接接口
            network.getName().startsWith("docker")
            || // 排除Docker创建的虚拟接口
            // 可以添加更多虚拟接口的判断逻辑
            network.isLoopback()
            || // 排除回环接口
            network.isVirtual()) { // 排除其他虚拟接口
          continue;
        }
                Enumeration addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address != null && (address instanceof Inet4Address)) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("getLocalIp-error", e);
            return null;
        }

        return null;
    }

}
