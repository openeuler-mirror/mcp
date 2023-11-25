package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.common.servervm.*;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.McStartVmErrorCode;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseServerVmConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.ServerVmOperateLogReq;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmOperateLogPageParam;
import com.hnkylin.cloud.manage.entity.req.servervm.ServerVmPageParam;
import com.hnkylin.cloud.manage.entity.resp.serverVm.PageServerVmRespDto;
import com.hnkylin.cloud.manage.entity.resp.serverVm.ServerVmZoneOrgTreeRespDto;
import com.hnkylin.cloud.manage.entity.resp.zone.ZoneInfoDto;
import com.hnkylin.cloud.manage.enums.ZoneOrgUserType;
import com.hnkylin.cloud.manage.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServerVmServiceImpl implements ServerVmService {


    @Resource
    private UserService userService;

    @Resource
    private ZoneService zoneService;

    @Resource
    private VdcService vdcService;

    @Resource
    private OrgService orgService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;

    @Resource
    private CloudUserMachineService cloudUserMachineService;

    @Resource
    private CloudOrgVdcService cloudOrgVdcService;


    @Resource
    private McHttpService mcHttpService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private McClusterThreadService mcClusterThreadService;

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private UserMachineService userMachineService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private RoleService roleService;

    @Override
    public List<ServerVmZoneOrgTreeRespDto> serverVmZoneTree(Integer clusterId, LoginUserVo loginUserVo) {

        List<ZoneInfoDto> loginUserZoneList = zoneService.zoneList(loginUserVo);
        if (loginUserZoneList.isEmpty()) {
            return new ArrayList<>();
        }
        //获取登录用户角色
        CloudRoleDo loginUserRole = roleService.getUserRole(loginUserVo.getUserId());
        boolean platformUser = Objects.equals(loginUserRole.getRoleType(), RoleType.PLATFORM);
        List<ServerVmZoneOrgTreeRespDto> zoneOrgTreeList = new ArrayList<>();
        loginUserZoneList.forEach(zone -> {

            List<CloudVdcDo> vdcDoList = new ArrayList<>();
            if (platformUser) {
                vdcDoList = cloudVdcService.getFirstVdcListByZone(zone.getZoneId());
            } else {
                CloudVdcDo userVdc = vdcService.getUserOrgBindVdc(loginUserVo.getUserId());
                if (Objects.nonNull(userVdc)) {
                    vdcDoList.add(userVdc);
                }
            }
            ServerVmZoneOrgTreeRespDto zoneOrgDto = new ServerVmZoneOrgTreeRespDto();
            zoneOrgDto.setUniqueId(zone.getZoneId());
            zoneOrgDto.setTreeUniqueId(ZoneOrgUserType.ZONE.name() + "_" + zone.getZoneId());
            zoneOrgDto.setName(zone.getName());
            zoneOrgDto.setType(ZoneOrgUserType.ZONE);
            zoneOrgDto.setServerVmCount(0);
            List<ServerVmZoneOrgTreeRespDto> child = new ArrayList<>();
            vdcDoList.forEach(vdc -> {
                CloudOrganizationDo organizationDo = orgService.getOrgByVdcId(vdc.getId());
                if (Objects.nonNull(organizationDo)) {
                    //获取vdc对应的组织
                    List<CloudOrganizationDo> orgDoList = cloudOrganizationService.queryAllOrgList();
                    List<ServerVmZoneOrgTreeRespDto> childOrgUser = new ArrayList<>();
                    for (CloudOrganizationDo orgDo : orgDoList) {
                        if (Objects.equals(orgDo.getId(), organizationDo.getId())) {
                            childOrgUser.add(createZoneOrgObject(clusterId, orgDo, orgDoList, loginUserVo));
                        }
                    }
                    child.addAll(childOrgUser);

                }
            });
            zoneOrgDto.setChild(child);
            zoneOrgDto.countServerVmCount();

            zoneOrgTreeList.add(zoneOrgDto);
        });
        return zoneOrgTreeList;
    }


    /**
     * 封装可用区组织数实体
     */
    private ServerVmZoneOrgTreeRespDto createZoneOrgObject(Integer clusterId, CloudOrganizationDo cloudOrganizationDo,
                                                           List<CloudOrganizationDo> allOrgList,
                                                           LoginUserVo loginUserVo) {
        ServerVmZoneOrgTreeRespDto serverVmZoneOrgRespDto = new ServerVmZoneOrgTreeRespDto();

        serverVmZoneOrgRespDto.setUniqueId(cloudOrganizationDo.getId());
        serverVmZoneOrgRespDto.setName(cloudOrganizationDo.getOrganizationName());
        serverVmZoneOrgRespDto.setTreeUniqueId(ZoneOrgUserType.ORG.name() + "_" + cloudOrganizationDo.getId());
        serverVmZoneOrgRespDto.setType(ZoneOrgUserType.ORG);

        List<ServerVmZoneOrgTreeRespDto> childList = new ArrayList<>();
        List<CloudUserDo> userDoList = userService.listUserByOrgId(cloudOrganizationDo.getId());

        //判断登录用户是否是组织管理员，是则可以显示该组织全部用户，不是则只能返回自己
        boolean userHasOrgPermission = orgService.userHasOrgPermission(loginUserVo.getUserId(),
                cloudOrganizationDo.getId());
        userDoList.forEach(item -> {
            ServerVmZoneOrgTreeRespDto zoneUserRespDto = new ServerVmZoneOrgTreeRespDto();
            zoneUserRespDto.setUniqueId(item.getId());
            zoneUserRespDto.setName(item.getRealName());
            if (StringUtils.isBlank(item.getRealName())) {
                zoneUserRespDto.setName(item.getUserName());
            }
            zoneUserRespDto.setType(ZoneOrgUserType.USER);
            zoneUserRespDto.setTreeUniqueId(ZoneOrgUserType.USER.name() + "_" + item.getId());
            zoneUserRespDto.setServerVmCount(countUserMachineNum(clusterId, item.getId(), loginUserVo));
            if (userHasOrgPermission) {
                childList.add(zoneUserRespDto);
            } else {
                if (Objects.equals(item.getId(), loginUserVo.getUserId())) {
                    childList.add(zoneUserRespDto);
                }
            }

        });
        List<ServerVmZoneOrgTreeRespDto> orgChild = getParentChildZoneOrg(cloudOrganizationDo.getId(), allOrgList,
                loginUserVo, clusterId);
        childList.addAll(orgChild);
        serverVmZoneOrgRespDto.setChild(childList);
        serverVmZoneOrgRespDto.countServerVmCount();
        return serverVmZoneOrgRespDto;
    }

    /**
     * 获取云服务器数量
     *
     * @param userId
     * @param loginUserVo
     * @return
     */
    private Integer countUserMachineNum(Integer clusterId, Integer userId, LoginUserVo loginUserVo) {
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);

//        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
//        cloudUserMachineDo.setDeleteFlag(false);
//        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
////        if (userIdList.isEmpty()) {
////            return new ArrayList<>();
////        }
//        queryWrapper.in("user_id", userIdList);

        List<McServerVmPageDetailResp> mcServerVmList =
                userMachineService.listUserMachineByUserIdListAndClusterId(clusterId, userIdList,
                        loginUserVo.getUserName());
        return mcServerVmList.size();
    }

    /**
     * 获取可用区-组织下级组织
     */
    private List<ServerVmZoneOrgTreeRespDto> getParentChildZoneOrg(Integer parentId,
                                                                   List<CloudOrganizationDo> allOrgList,
                                                                   LoginUserVo loginUserVo, Integer clusterId) {
        List<ServerVmZoneOrgTreeRespDto> childList = new ArrayList<>();
        for (CloudOrganizationDo organizationDo : allOrgList) {
            if (Objects.equals(parentId, organizationDo.getParentId())) {
                ServerVmZoneOrgTreeRespDto zoneOrgObject = createZoneOrgObject(clusterId, organizationDo, allOrgList,
                        loginUserVo);
                childList.add(zoneOrgObject);
            }
        }
        return childList;
    }


    @Override
    public PageData<PageServerVmRespDto> serverVmList(ServerVmPageParam serverVmPageParam, LoginUserVo loginUserVo) {
        List<CloudUserMachineDo> userMachineList = getUserMachineList(serverVmPageParam, loginUserVo);
        if (userMachineList.isEmpty()) {
            return new PageData(null);
        }

        //将用户云服务器安装集群进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap =
                userMachineList.stream().collect(Collectors.groupingBy(CloudUserMachineDo::getClusterId));

        if (Objects.equals(clusterUserMachineMap.size(), 1)) {
            //单集群
            return singleClusterUserMachine(userMachineList, serverVmPageParam, loginUserVo,
                    userMachineList.get(0).getClusterId());
        } else {
            //多集群
            return manyClusterUserMachine(clusterUserMachineMap, serverVmPageParam, loginUserVo,
                    userMachineList.size());
        }
    }

    /**
     * 单个集群获取云服务器列表
     *
     * @param userMachineList
     * @param serverVmPageParam
     * @param loginUserVo
     * @return
     */
    private PageData<PageServerVmRespDto> singleClusterUserMachine(List<CloudUserMachineDo> userMachineList,
                                                                   ServerVmPageParam serverVmPageParam,
                                                                   LoginUserVo loginUserVo, Integer clusterId) {

        List<String> uuidList =
                userMachineList.stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
        ServerVmListReq serverVmListReq = formatSearchMcServerVmParam(serverVmPageParam, uuidList);

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, serverVmListReq,
                mcConfigProperties.getServerVmList(), loginUserVo.getUserName(), 0);
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String serverListTxt = JSON.toJSONString(mcResponse.getData());
            McPageResp<McServerVmPageDetailResp> mcServerVmPage = JSONObject.parseObject(serverListTxt, new
                    TypeReference<McPageResp<McServerVmPageDetailResp>>() {
                    });
            List<PageServerVmRespDto> pageServerVmRespDtoList = new ArrayList<>();
            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(mcServerVmPage.getPager());
            mcPageInfo.setPageSize(mcServerVmPage.getRows().size());
            mcPageInfo.setRecords(mcServerVmPage.getRecords());
            mcPageInfo.setTotal(mcServerVmPage.getTotal());

            mcServerVmPage.getRows().forEach(mcServerVm -> {
                PageServerVmRespDto pageServerVmRespDto = createMcServerVmPageDetail(clusterId, mcServerVm,
                        userMachineList, loginUserVo);
                pageServerVmRespDtoList.add(pageServerVmRespDto);
            });
            return new PageData(mcPageInfo, pageServerVmRespDtoList);
        }
        return new PageData(null);
    }


    /**
     * 多集群云云服务器列表
     *
     * @param clusterUserMachineMap
     * @param serverVmPageParam
     * @param loginUserVo
     * @return
     */
    private PageData<PageServerVmRespDto> manyClusterUserMachine(Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap,
                                                                 ServerVmPageParam serverVmPageParam,
                                                                 LoginUserVo loginUserVo, Integer maxServerVmSize) {
        List<Integer> clusterIdList = new ArrayList<>();
        clusterIdList.addAll(clusterUserMachineMap.keySet());
        List<Object> mcRequestObjectList = new ArrayList<>();
        clusterIdList.forEach(clusterId -> {
            List<String> uuidList =
                    clusterUserMachineMap.get(clusterId).stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
            ServerVmListReq serverVmListReq = formatSearchMcServerVmParam(serverVmPageParam, uuidList);
            serverVmListReq.setPage(KylinCommonConstants.FIRST_PAGE);
            serverVmListReq.setRows(maxServerVmSize);
            mcRequestObjectList.add(serverVmListReq);
        });

        List<String> mcServerVmList = mcClusterThreadService.threadGetMcResponse(clusterIdList,
                loginUserVo.getUserName(),
                mcConfigProperties.getServerVmList(), mcRequestObjectList);

        //多个集群中满足条件的所有的云服务器
        List<PageServerVmRespDto> totalServerVmRespDtoList = new ArrayList<>();
        McPageInfo mcPageInfo = new McPageInfo();
        mcPageInfo.setPager(serverVmPageParam.getPageNo());
        for (int i = 0; i < clusterIdList.size(); i++) {
            Integer clusterId = clusterIdList.get(i);
            String serverVmList = mcServerVmList.get(i);
            if (Objects.nonNull(serverVmList)) {
                McPageResp<McServerVmPageDetailResp> mcServerVmPage = JSONObject.parseObject(serverVmList, new
                        TypeReference<McPageResp<McServerVmPageDetailResp>>() {
                        });
                mcServerVmPage.getRows().forEach(mcServerVm -> {
                    PageServerVmRespDto pageServerVmRespDto = createMcServerVmPageDetail(clusterId,
                            mcServerVm, clusterUserMachineMap.get(clusterId), loginUserVo);
                    totalServerVmRespDtoList.add(pageServerVmRespDto);
                });
            }
        }
        mcPageInfo.setTotal(mcPageInfo.getRecords());
        mcPageInfo.setRecords(totalServerVmRespDtoList.size());
        //总页数
        int totalPage = (mcPageInfo.getRecords() - 1) / serverVmPageParam.getPageSize() + 1;
        mcPageInfo.setTotal(totalPage);

        //云服务器安装状态排序
        Collections.sort(totalServerVmRespDtoList);
        //从所有云服务器列表中，截取页面查询的对应页码的数量
        int totalSize = totalServerVmRespDtoList.size();
        int subListStart = (serverVmPageParam.getPageNo() - 1) * serverVmPageParam.getPageSize();
        int subListEnd = serverVmPageParam.getPageNo() * serverVmPageParam.getPageSize();
        if (totalSize < subListStart) {
            return new PageData(null);
        } else if (totalSize >= subListStart && totalSize < subListEnd) {
            mcPageInfo.setPageSize(totalSize - subListStart);
            return new PageData(mcPageInfo, totalServerVmRespDtoList.subList(subListStart, totalSize));
        } else {
            mcPageInfo.setPageSize(serverVmPageParam.getPageSize());
            return new PageData(mcPageInfo, totalServerVmRespDtoList.subList(subListStart, subListEnd));
        }

    }

    /**
     * 创建云服务器列表数据
     *
     * @param clusterId
     * @param mcServerVm
     * @return
     */
    private PageServerVmRespDto createMcServerVmPageDetail(Integer clusterId, McServerVmPageDetailResp mcServerVm,
                                                           List<CloudUserMachineDo> userMachineList,
                                                           LoginUserVo loginUserVo) {
        //根据uuid 过滤，保存起来的用户云服务器。
        CloudUserMachineDo userMachineDo = userMachineList.stream().filter(userMachine -> Objects.equals
                (mcServerVm.getUuid(), userMachine.getMachineUuid())).findFirst().orElse(null);
        PageServerVmRespDto serverVmRespDto = new PageServerVmRespDto();
        //过滤,防止MC中删除了该云服务器，但是自服务中还保留该用户和云服务器的关联关系
        if (Objects.nonNull(userMachineDo)) {
            BeanUtils.copyProperties(mcServerVm, serverVmRespDto);
            serverVmRespDto.setServerVmId(mcServerVm.getId());
            serverVmRespDto.setServerVmUuid(mcServerVm.getUuid());
            //设置到期时间  到期时间-当前时间=剩余天数
            serverVmRespDto.setDeadlineTime(DateUtils.differentDaysByMillisecond(new Date(),
                    userMachineDo.getDeadlineTime()));
            serverVmRespDto.setDeadlineFlag(userMachineDo.getDeadlineFlag());
            serverVmRespDto.setCreateDate(DateUtils.format(userMachineDo.getCreateTime(),
                    DateUtils.DATE_YYYY_MM_DD));


            if (userMachineDo.getDeadlineFlag()) {
                serverVmRespDto.setStatus(McServerVmStatus.OVERDUE);
            }

            serverVmRespDto.setArchitecture(KcpCommonUtil.changeToKcpArchitectureType(mcServerVm.getArchitecture()).getDesc());
            //利用率
            JSONObject usage = new JSONObject();
            if (mcServerVm.getStatus().equals(McServerVmStatus.AVAILABLE) ||
                    mcServerVm.getStatus().equals(McServerVmStatus.SUSPEND) ||
                    mcServerVm.getStatus().equals(McServerVmStatus.INSTALLING) ||
                    mcServerVm.getStatus().equals(McServerVmStatus.CONNECTED)) {
                usage.put("cpu", "0");
                usage.put("mem", "0");
            }
            if (StringUtils.isNotBlank(mcServerVm.getCpuRate())) {
                usage.put("cpu", mcServerVm.getCpuRate().replaceAll("%", ""));
            }
            if (StringUtils.isNotBlank(mcServerVm.getMemoryRate())) {
                usage.put("mem", mcServerVm.getMemoryRate().replaceAll("%", ""));
            }
            serverVmRespDto.setUsage(usage.toJSONString());

            CloudClusterDo clusterDo = cloudClusterService.getById(clusterId);
            //可用区名称
            CloudZoneDo cloudZoneDo = zoneService.getZoneByClusterId(clusterDo.getId());
            serverVmRespDto.setZoneName(Objects.nonNull(cloudZoneDo) ? cloudZoneDo.getName() : "未知集群");
            //集群名称
            serverVmRespDto.setClusterName(clusterDo.getName());
            //组织名称
            CloudOrganizationDo orgDo = orgService.getByUserId(userMachineDo.getUserId());
            serverVmRespDto.setOrgName(orgDo.getOrganizationName());
            //vdc名称
            CloudVdcDo vdcDo = vdcService.getVdcByOrgId(orgDo.getId());
            serverVmRespDto.setVdcName(Objects.nonNull(vdcDo) ? vdcDo.getVdcName() : "未知VDC");
            //用户名称
            serverVmRespDto.setUserName(loginUserVo.getUserName());
        }
        return serverVmRespDto;
    }

    /**
     * 封装请求mc云服务器列表的参数
     *
     * @param serverVmPageParam
     * @param uuids
     * @return
     */
    private ServerVmListReq formatSearchMcServerVmParam(ServerVmPageParam serverVmPageParam, List<String> uuids) {
        ServerVmListReq serverVmListReq = new ServerVmListReq();
        serverVmListReq.setPage(serverVmPageParam.getPageNo());
        serverVmListReq.setRows(serverVmPageParam.getPageSize());
        serverVmListReq.setVmName(serverVmPageParam.getSearchKey());
        if (Objects.nonNull(serverVmPageParam.getVmStatus()) && !Objects.equals(McServerVmStatus.OVERDUE,
                serverVmPageParam.getVmStatus()) && !Objects.equals(McServerVmStatus.ALL,
                serverVmPageParam.getVmStatus())) {
            serverVmListReq.setVmStatus(serverVmPageParam.getVmStatus());
        }
        serverVmListReq.setUuidList(uuids);
        return serverVmListReq;
    }

    /**
     * 获取用户管理云服务器关系记录
     *
     * @param serverVmPageParam
     * @param loginUserVo
     * @return
     */
    private List<CloudUserMachineDo> getUserMachineList(ServerVmPageParam serverVmPageParam, LoginUserVo loginUserVo) {
        //查询用户拥有的云服务器uuid
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setDeleteFlag(false);
        //是否过期过滤
        if (Objects.nonNull(serverVmPageParam.getVmStatus()) && !Objects.equals(McServerVmStatus.ALL,
                serverVmPageParam.getVmStatus())) {
            cloudUserMachineDo.setDeadlineFlag(Objects.equals(McServerVmStatus.OVERDUE, serverVmPageParam.getVmStatus
                    ()));
        }
        if (Objects.nonNull(serverVmPageParam.getClusterId()) && serverVmPageParam.getClusterId() > 0) {
            cloudUserMachineDo.setClusterId(serverVmPageParam.getClusterId());
        }

        List<Integer> userIdList = new ArrayList<>();
        if (Objects.equals(serverVmPageParam.getType(), ZoneOrgUserType.ZONE)) {
            //查询的是可用区，则查询登录在可用区里可见的用户
            //根据可用区获取可用区下组织ID列表
            List<Integer> orgIdList = cloudOrgVdcService.orgIdListByZoneId(serverVmPageParam.getUniqueId());
            if (!orgIdList.isEmpty()) {
                Set<Integer> userIdSet = new HashSet<>();
                orgIdList.forEach(orgId -> {
                    boolean userHasOrgPermission = orgService.userHasOrgPermission(loginUserVo.getUserId(), orgId);
                    if (userHasOrgPermission) {
                        List<CloudUserDo> userDoList = userService.listUserByOrgId(orgId);
                        userIdSet.addAll(userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toSet()));
                    } else {
                        userIdSet.add(loginUserVo.getUserId());
                    }
                });
                userIdList.addAll(userIdSet);
            }
        } else if (Objects.equals(serverVmPageParam.getType(), ZoneOrgUserType.ORG)) {
            //判断登录用户是否是组织管理员，是则可以显示该组织全部用户，不是则只能返回自己
            boolean userHasOrgPermission = orgService.userHasOrgPermission(loginUserVo.getUserId(),
                    serverVmPageParam.getUniqueId());
            if (userHasOrgPermission) {
                List<Integer> orgIdList = orgService.getOrgChildIdList(serverVmPageParam.getUniqueId());
                List<CloudUserDo> userDoList = userService.listUserByOrgList(orgIdList, null);
                userIdList = userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList());
            } else {
                userIdList.add(loginUserVo.getUserId());
            }

        }
        if (Objects.equals(serverVmPageParam.getType(), ZoneOrgUserType.USER)) {
            userIdList.add(serverVmPageParam.getUniqueId());
        }
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        if (userIdList.isEmpty()) {
            return new ArrayList<>();
        }
        queryWrapper.in("user_id", userIdList);

        return cloudUserMachineService.list(queryWrapper);
    }


    /**
     * 检查云服务器是否过期
     */
    private void checkUserMachineIfOverdue(String serverVmUuid) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setMachineUuid(serverVmUuid);
        QueryWrapper<CloudUserMachineDo> wrapper = new QueryWrapper<>(cloudUserMachineDo);

        CloudUserMachineDo queryDo = cloudUserMachineService.getOne(wrapper);
        if (Objects.isNull(queryDo)) {
            throw new KylinException(KylinHttpResponseServerVmConstants.OPERATE_ERR);
        }
        if (queryDo.getDeadlineFlag()) {
            throw new KylinException(KylinHttpResponseServerVmConstants.SERVERVM_OVERDUE_NOT_OPERATE);
        }
    }

    /**
     * 根据云服务器uuid获取用户云服务器关联关系
     *
     * @param uuid
     * @return
     */
    private CloudUserMachineDo getUserMachineByUuid(String uuid) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setMachineUuid(uuid);
        QueryWrapper<CloudUserMachineDo> wrapper = new QueryWrapper<>(cloudUserMachineDo);
        CloudUserMachineDo queryDo = cloudUserMachineService.getOne(wrapper);
        return queryDo;
    }


    @Override
    public McStartVmErrorCode startServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        checkUserMachineIfOverdue(serverVmBaseParam.getServerVmUuid());
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());

        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, mcConfigProperties.getStartServerVmUrl(), loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            return McStartVmErrorCode.SUCCESS;
        }
        String error = JSON.parseObject(JSON.toJSONString(mcResponse.getData())).getString("errorCode");
        McStartVmErrorCode errorCode = McStartVmErrorCode.valueOf(error);
        return errorCode;
    }

    /**
     * 单台云服务器操作
     *
     * @param serverVmBaseParam
     * @param loginUserVo
     * @param httpUrl
     * @return
     */
    private boolean commonSingleOperate(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo, String httpUrl) {
        checkUserMachineIfOverdue(serverVmBaseParam.getServerVmUuid());
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());

        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        return mcHttpService.noDataCommonMcRequest(userMachineDo.getClusterId(), serverVmBaseReq,
                httpUrl, loginUserVo.getUserName(), 0);
    }

    @Override
    public boolean shutdownServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getShutdownServerVmUrl());
    }

    @Override
    public boolean forcedShutdownServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getForcedShutdownServerVmUrl());
    }

    @Override
    public boolean restartServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getRestartServerVmUrl());
    }

    @Override
    public boolean forcedRestartServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getForcedRestartServerVmUrl());
    }


    /**
     * 云服务器操作，批量开机/批量关机/批量重启/
     */
    private BaseResult<String> serverVmBatchOperateToMc(ServerVmBatchOperateParam serverVmBatchOperateParam, LoginUserVo
            loginUserVo, String httpUrl) {

        //将用户云服务器安装集群进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap = groupByClusterId(serverVmBatchOperateParam);
        int clusterMachineMapSize = clusterUserMachineMap.size();
        //选择的云服务器是通一个集群中
        if (Objects.equals(clusterMachineMapSize, 1)) {
            CloudUserMachineDo userMachineDo =
                    getUserMachineByUuid(serverVmBatchOperateParam.getServerVmUuids().get(0));
            boolean singleClusterBatchOperate = singleClusterBatchOperate(serverVmBatchOperateParam, loginUserVo,
                    httpUrl
                    , userMachineDo.getClusterId());
            if (singleClusterBatchOperate) {
                return BaseResult.success(null);
            }
            return BaseResult.error(KylinHttpResponseServerVmConstants.OPERATE_ERR);
        } else {
            //选择的云服务器是不同的集群
            List<Integer> failedClusterList = manyClusterBatchOperate(clusterUserMachineMap, loginUserVo, httpUrl);
            if (failedClusterList.isEmpty()) {
                return BaseResult.success(null);
            }
            StringBuilder manyClusterFailedMsg = new StringBuilder();
            failedClusterList.forEach(clusterId -> {
                CloudClusterDo failedCluster = cloudClusterService.getById(clusterId);
                manyClusterFailedMsg.append(failedCluster.getName()).append(",")
                        .append(KylinHttpResponseServerVmConstants.BATCH_OPERATE_ERR).append(";");
            });
            return BaseResult.error(manyClusterFailedMsg.toString());
        }
    }

    /**
     * 针对单个云服务器单个操作。
     *
     * @param serverVmBatchOperateParam
     * @param loginUserVo
     * @param httpUrl
     * @param clusterId
     * @return
     */
    private boolean singleClusterBatchOperate(ServerVmBatchOperateParam serverVmBatchOperateParam, LoginUserVo
            loginUserVo, String httpUrl, Integer clusterId) {
        ServerVmBatchReq serverVmBatchReq = new ServerVmBatchReq();
        serverVmBatchReq.setUuid(String.join(",", serverVmBatchOperateParam.getServerVmUuids()));

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, serverVmBatchReq, httpUrl
                , loginUserVo.getUserName(), 0);
        return Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus());

    }


    /**
     * 针对多个集群中的批量操作
     *
     * @param clusterUserMachineMap
     * @param loginUserVo
     * @param httpUrl
     * @return
     */
    private List<Integer> manyClusterBatchOperate(Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap,
                                                  LoginUserVo
                                                          loginUserVo, String httpUrl) {
        List<Integer> clusterIdList = new ArrayList<>();
        clusterIdList.addAll(clusterUserMachineMap.keySet());
        List<Object> mcRequestObjectList = new ArrayList<>();
        clusterIdList.forEach(clusterId -> {
            List<String> uuidList =
                    clusterUserMachineMap.get(clusterId).stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
            ServerVmBatchReq serverVmBatchReq = new ServerVmBatchReq();
            serverVmBatchReq.setUuid(String.join(",", uuidList));
            mcRequestObjectList.add(serverVmBatchReq);
        });
        List<MCResponseData<Object>> manyClusterBatchOperateResponse =
                mcClusterThreadService.threadSendToMc(clusterIdList,
                        loginUserVo.getUserName(),
                        httpUrl, mcRequestObjectList);

        //操作失败的集群
        List<Integer> failedClusterList = new ArrayList<>();
        for (int i = 0; i < clusterIdList.size(); i++) {
            MCResponseData<Object> mcResponse = manyClusterBatchOperateResponse.get(i);
            if (Objects.isNull(mcResponse) || Objects.equals(mcResponse.getStatus(), MCServerVmConstants.ERROR)) {
                failedClusterList.add(clusterIdList.get(i));
            }
        }
        return failedClusterList;
    }

    @Override
    public BaseResult<String> batchStartServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                 LoginUserVo loginUserVo) {
        return serverVmBatchOperateToMc(serverVmBatchOperateParam, loginUserVo,
                mcConfigProperties.getBatchStartServerVmUrl());
    }


    @Override
    public BaseResult<String> batchShutdownServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                    LoginUserVo loginUserVo) {
        return serverVmBatchOperateToMc(serverVmBatchOperateParam, loginUserVo,
                mcConfigProperties.getBatchShutdownServerVmUrl());
    }

    @Override
    public BaseResult<String> batchRebootServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  LoginUserVo loginUserVo) {
        return serverVmBatchOperateToMc(serverVmBatchOperateParam, loginUserVo,
                mcConfigProperties.getBatchRebootServerVmUrl());
    }

    /**
     * 根据集群ID，将批量操作进行分组
     *
     * @param serverVmBatchOperateParam
     * @return
     */
    private Map<Integer, List<CloudUserMachineDo>> groupByClusterId(ServerVmBatchOperateParam serverVmBatchOperateParam) {
        //根据云服务器列表，进行分类
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        QueryWrapper<CloudUserMachineDo> wrapper = new QueryWrapper<>(cloudUserMachineDo);
        wrapper.in("machine_uuid", serverVmBatchOperateParam.getServerVmUuids());
        List<CloudUserMachineDo> machineList = cloudUserMachineService.list(wrapper);

        //将用户云服务器安装集群进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap =
                machineList.stream().collect(Collectors.groupingBy(CloudUserMachineDo::getClusterId));
        return clusterUserMachineMap;
    }

    /**
     * 批量删除用户云服务器关联关系
     *
     * @param loginUserVo
     * @param userMachineUuidList
     */
    private void deleteUserMachine(LoginUserVo loginUserVo, List<String> userMachineUuidList) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        QueryWrapper<CloudUserMachineDo> wrapper = new QueryWrapper<>(cloudUserMachineDo);
        wrapper.in("machine_uuid", userMachineUuidList);
        List<CloudUserMachineDo> machineList = cloudUserMachineService.list(wrapper);
        if (!machineList.isEmpty()) {
            Date deleteTime = new Date();
            machineList.forEach(userMachine -> {
                userMachine.setDeleteFlag(true);
                userMachine.setDeleteBy(loginUserVo.getUserId());
                userMachine.setDeleteTime(deleteTime);
            });
            cloudUserMachineService.updateBatchById(machineList);
        }
    }

    @Override
    public BaseResult<String> deleteServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        List<String> serverVmUuids = new ArrayList();
        serverVmUuids.add(serverVmBaseParam.getServerVmUuid());
        ServerVmBatchOperateParam serverVmBatchOperateParam = new ServerVmBatchOperateParam();
        serverVmBatchOperateParam.setServerVmUuids(serverVmUuids);

        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());

        boolean deleteFlag = singleClusterBatchOperate(serverVmBatchOperateParam, loginUserVo,
                mcConfigProperties.getBatchRemoveMachineToRecycleUrl(), userMachineDo.getClusterId());
        //mc中成功将云服务器放入回收站后，将用户用户拥有的云服务器逻辑删除
        if (deleteFlag) {
            deleteUserMachine(loginUserVo, serverVmUuids);
            return BaseResult.success(null);
        }
        return BaseResult.error(KylinHttpResponseServerVmConstants.OPERATE_ERR);
    }


    @Override
    @Transactional
    public BaseResult<String> batchDeleteServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  LoginUserVo loginUserVo) {

        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap = groupByClusterId(serverVmBatchOperateParam);
        if (Objects.equals(clusterUserMachineMap.size(), 1)) {
            CloudUserMachineDo userMachineDo =
                    getUserMachineByUuid(serverVmBatchOperateParam.getServerVmUuids().get(0));
            boolean deleteFlag = singleClusterBatchOperate(serverVmBatchOperateParam, loginUserVo,
                    mcConfigProperties.getBatchRemoveMachineToRecycleUrl(), userMachineDo.getClusterId());
            //mc中成功将云服务器放入回收站后，将用户用户拥有的云服务器逻辑删除
            if (deleteFlag) {
                deleteUserMachine(loginUserVo, serverVmBatchOperateParam.getServerVmUuids());
                return BaseResult.success(null);
            }
            return BaseResult.error(KylinHttpResponseServerVmConstants.OPERATE_ERR);
        } else {
            //选择的云服务器是不同的集群
            List<Integer> failedClusterList = manyClusterBatchOperate(clusterUserMachineMap, loginUserVo,
                    mcConfigProperties.getBatchRemoveMachineToRecycleUrl());
            if (failedClusterList.isEmpty()) {
                deleteUserMachine(loginUserVo, serverVmBatchOperateParam.getServerVmUuids());
                return BaseResult.success(null);
            }
            //将操作成功的集群中的，用户云服务器管理关系删除
            List<String> deleteSuccessUuid = new ArrayList<>();
            clusterUserMachineMap.forEach((clusterId, value) -> {
                if (!failedClusterList.contains(clusterId)) {
                    List<String> deleteSuccessClusterUuid =
                            value.stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
                    deleteSuccessUuid.addAll(deleteSuccessClusterUuid);
                }
            });
            if (!deleteSuccessUuid.isEmpty()) {
                deleteUserMachine(loginUserVo, deleteSuccessUuid);
            }
            StringBuilder manyClusterFailedMsg = new StringBuilder();
            failedClusterList.forEach(clusterId -> {
                CloudClusterDo failedCluster = cloudClusterService.getById(clusterId);
                manyClusterFailedMsg.append(failedCluster.getName()).append(",")
                        .append(KylinHttpResponseServerVmConstants.BATCH_OPERATE_ERR).append(";");
            });
        }
        return BaseResult.error(KylinHttpResponseServerVmConstants.OPERATE_ERR);
    }

    @Override
    public VncUrlDto getVncUrl(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        VncUrlDto vncUrlDto = new VncUrlDto();
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());
        String httpUrl = mcConfigProperties.getServerVmVncUrl();
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq,
                httpUrl, loginUserVo.getUserName(), 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            vncUrlDto = JSON.parseObject(JSON.toJSONString(mcResponse.getData()), VncUrlDto.class);

        }
        return vncUrlDto;
    }


    @Override
    public KcpServerVmDetailResp serverVmInfo(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq,
                mcConfigProperties.getServerVmInfoUrl(), loginUserVo.getUserName(), 0);
        KcpServerVmDetailResp kcpServerVmDetailResp = new KcpServerVmDetailResp();
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            kcpServerVmDetailResp = JSON.parseObject(JSON.toJSONString(mcResponse.getData()),
                    KcpServerVmDetailResp.class);
            CloudUserDo userDo = cloudUserService.getById(userMachineDo.getUserId());
            kcpServerVmDetailResp.setUserName(userDo.getRealName());
            CloudOrganizationDo organizationDo = cloudOrganizationService.getById(userDo.getOrganizationId());
            kcpServerVmDetailResp.setOrgName(organizationDo.getOrganizationName());

        }
        return kcpServerVmDetailResp;
    }


    @Override
    public KcpServerVmSummaryResp serverVmSummary(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, mcConfigProperties.getServerVmSummaryUrl(), loginUserVo.getUserName(), 0);
        KcpServerVmSummaryResp summary = new KcpServerVmSummaryResp();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            summary = JSON.parseObject(JSON.toJSONString(mcResponse.getData()), KcpServerVmSummaryResp.class);
            summary.setMemoryTotal(summary.getMemoryTotal().divide(new BigDecimal(1024)).setScale(0,
                    BigDecimal.ROUND_HALF_UP));
            summary.setMemoryUsed(summary.getMemoryUsed().divide(new BigDecimal(1024)).setScale(0,
                    BigDecimal.ROUND_HALF_UP));
            summary.setMemorySurplus(summary.getMemoryTotal().subtract(summary.getMemoryUsed()));

            summary.setDiskSurplus(summary.getDiskTotal().subtract(summary.getDiskTotalUsed()));

        }
        return summary;
    }

    @Override
    public List<KcpServerVmAlarmEventResp> serverVmAlarmEvent(ServerVmBaseParam serverVmBaseParam,
                                                              LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq,
                mcConfigProperties.getServerVmAlarmEventUrl(), loginUserVo.getUserName(), 0);
        List<KcpServerVmAlarmEventResp> alarmEventList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            alarmEventList = JSON.parseArray(JSONArray.toJSONString(mcResponse.getData()),
                    KcpServerVmAlarmEventResp.class);
        }
        return alarmEventList;
    }

    @Override
    public List<KcpServerVmNetworkResp> serverVmNetwork(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, mcConfigProperties.getServerVmNetworkUrl(), loginUserVo.getUserName(), 0);

        List<KcpServerVmNetworkResp> networkList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            networkList = JSON.parseArray(JSONArray.toJSONString(mcResponse.getData()), KcpServerVmNetworkResp.class);
        }
        return networkList;
    }

    @Override
    public List<KcpServerVmDiskResp> serverVmDisk(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, mcConfigProperties.getServerVmDiskUrl(), loginUserVo.getUserName(), 0);

        List<KcpServerVmDiskResp> diskList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            diskList = JSON.parseArray(JSONArray.toJSONString(mcResponse.getData()), KcpServerVmDiskResp.class);
        }
        return diskList;
    }

    @Override
    public ServerVmMonitorInfoRespDto serverVmMonitor(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmBaseParam.getServerVmUuid());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, mcConfigProperties.getServerVmMonitorInfoUrl(), loginUserVo.getUserName(), 0);

        ServerVmMonitorInfoRespDto serverVmMonitorInfoRespDto = new ServerVmMonitorInfoRespDto();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            List<McServerVmMonitorDetailResp> mcServerVmMonitorDetailList = new ArrayList<>();
            String serverVmMonitorTxt = JSON.toJSONString(mcResponse.getData());
            mcServerVmMonitorDetailList = JSON.parseArray(serverVmMonitorTxt, McServerVmMonitorDetailResp.class);
            List<String> timeList = new ArrayList<>();
            List<BigDecimal> cpuUsed = new ArrayList<>();
            List<BigDecimal> memUsed = new ArrayList();
            List<Integer> diskReadSpeed = new ArrayList();
            List<Integer> diskWriteSpeed = new ArrayList();
            List<Integer> netWorkInSpeed = new ArrayList();
            List<Integer> netWorkOutSpeed = new ArrayList();

            if (!mcServerVmMonitorDetailList.isEmpty()) {

                mcServerVmMonitorDetailList.forEach(monitor -> {
                    timeList.add(monitor.getDateShow());
                    cpuUsed.add(monitor.getCpuUtil());
                    memUsed.add(monitor.getMemUtil());
                    diskReadSpeed.add(monitor.getDiskReadSpeed());
                    diskWriteSpeed.add(monitor.getDiskWriteSpeed());
                    netWorkInSpeed.add(monitor.getNetWorkInSpeed());
                    netWorkOutSpeed.add(monitor.getNetWorkOutSpeed());
                });
                serverVmMonitorInfoRespDto.setTimeList(timeList);
                serverVmMonitorInfoRespDto.setCpuUsed(cpuUsed);
                serverVmMonitorInfoRespDto.setMemUsed(memUsed);
                serverVmMonitorInfoRespDto.setDiskReadSpeed(diskReadSpeed);
                serverVmMonitorInfoRespDto.setDiskWriteSpeed(diskWriteSpeed);
                serverVmMonitorInfoRespDto.setNetWorkInSpeed(netWorkInSpeed);
                serverVmMonitorInfoRespDto.setNetWorkOutSpeed(netWorkOutSpeed);
            }
        }

        return serverVmMonitorInfoRespDto;

    }

    @Override
    public PageData<McServerVmLogResp> serverVmOperateLog(ServerVmOperateLogPageParam serverVmOperateLogPageParam,
                                                          LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmOperateLogPageParam.getServerVmUuid());
        PageData<McServerVmLogResp> pageData = new PageData(null);


        ServerVmOperateLogReq serverVmOperateLogReq = new ServerVmOperateLogReq();
        serverVmOperateLogReq.setUuid(serverVmOperateLogPageParam.getServerVmUuid());
        serverVmOperateLogReq.setPage(serverVmOperateLogPageParam.getPageNo());
        serverVmOperateLogReq.setRows(serverVmOperateLogPageParam.getPageSize());


        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmOperateLogReq, mcConfigProperties.getServerVmOperateLogUrl(), loginUserVo.getUserName(), 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String serverListTxt = JSON.toJSONString(mcResponse.getData());
            McPageResp<McServerVmLogResp> mcServerLogPageResp = JSONObject.parseObject(serverListTxt, new
                    TypeReference<McPageResp<McServerVmLogResp>>() {
                    });

            if (Objects.nonNull(mcServerLogPageResp)) {
                McPageInfo mcPageInfo = new McPageInfo();
                mcPageInfo.setPager(mcServerLogPageResp.getPager());
                mcPageInfo.setPageSize(mcServerLogPageResp.getRows().size());
                mcPageInfo.setRecords(mcServerLogPageResp.getRecords());
                mcPageInfo.setTotal(mcServerLogPageResp.getTotal());
                List<McServerVmLogResp> mcServerVmLogList = new ArrayList<>();
                mcServerVmLogList = mcServerLogPageResp.getRows();
                pageData = new PageData(mcPageInfo, mcServerVmLogList);

            }
        }

        return pageData;
    }

    @Override
    public ResponseEntity<byte[]> downLoadServerVmLog(String mcServerVmLogoPath, String mcServerVmLogoName,
                                                      String serverVmUuid) {

        try {
            String localFilePath = mcConfigProperties.getMcLogoLocalFilePath() + mcServerVmLogoName;
            File file = new File(localFilePath);
            if (!file.exists()) {
                String encodeName = URLEncoder.encode(mcServerVmLogoName, "UTF-8");
                CloudUserMachineDo userMachineDo = getUserMachineByUuid(serverVmUuid);
                List<String> mcNodeList = cloudClusterService.formatClusterNodeList(userMachineDo.getClusterId());

                String mcDownLoadUrl = mcNodeList.get(0) + mcConfigProperties.getMcPrefix() +
                        mcConfigProperties.getLogoPath() + encodeName;
                file = HttpUtil.downLoadMcServerVmLogo(mcDownLoadUrl, localFilePath);
            }
            byte[] imageContent;
            imageContent = fileToByte(file);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] fileToByte(File img) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bi;
            bi = ImageIO.read(img);
            ImageIO.write(bi, "png", baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return bytes;
    }

}
