package com.hnkylin.cloud.selfservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.common.servervm.*;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.*;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.selfservice.config.MCConfigProperties;
import com.hnkylin.cloud.selfservice.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.selfservice.constant.KylinSelfConstants;
import com.hnkylin.cloud.core.enums.McStartVmErrorCode;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.mc.req.*;
import com.hnkylin.cloud.selfservice.entity.mc.resp.*;
import com.hnkylin.cloud.selfservice.entity.req.*;
import com.hnkylin.cloud.selfservice.entity.resp.*;
import com.hnkylin.cloud.selfservice.service.*;
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
public class SelfServerVmServiceImpl implements SelfServerVmService {


    @Resource
    private CloudWorkOrderService workOrderService;

    @Resource
    private CloudWorkOrderServerVmService cloudWorkOrderServerVmService;

    @Resource
    private CloudWorkOrderModifyServerVmService cloudWorkOrderModifyServerVmService;

    @Resource
    private CloudWorkOrderServerVmDiskService cloudWorkOrderServerVmDiskService;

    @Resource
    private CloudWorkOrderServerVmNetworkService cloudWorkOrderServerVmNetworkService;


    @Resource
    private SelfWorkOrderService selfWorkOrderService;

    @Resource
    private CloudWorkOrderDeferredMachineService deferredMachineService;

    @Resource
    private CloudUserMachineService cloudUserMachineService;

    @Resource
    private McHttpService mcHttpService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private McNodeService mcNodeService;

    @Resource
    private CloudWorkOrderServerVmIsoService cloudWorkOrderServerVmIsoService;

    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private McClusterThreadService mcClusterThreadService;


    @Override
    public PageData<ServerVmTemplateRespDto> listServerVmTemplate(SearchTemplateParam searchTemplateParam,
                                                                  LoginUserVo loginUserVo) {
        PageTemplateReq pageTemplateReq = new PageTemplateReq();
        pageTemplateReq.setPage(searchTemplateParam.getPageNo());
        pageTemplateReq.setRows(searchTemplateParam.getPageSize());
        pageTemplateReq.setSearchKey(searchTemplateParam.getSearchKey());

        List<CloudClusterDo> clusterDoList = cloudClusterService.clusterListByUserId(loginUserVo.getUserId());
        if (clusterDoList.isEmpty()) {
            return new PageData(null);
        }
        if (Objects.equals(clusterDoList.size(), 1)) {
            return singleClusterMcTemplate(clusterDoList.get(0), loginUserVo, searchTemplateParam, pageTemplateReq);
        }
        return manyClusterMcTemplate(clusterDoList, loginUserVo, searchTemplateParam);
    }


    /**
     * 单集群中获取mc模板列表
     *
     * @param clusterDo
     * @param loginUserVo
     * @param searchTemplateParam
     * @param pageTemplateReq
     * @return
     */
    private PageData<ServerVmTemplateRespDto> singleClusterMcTemplate(CloudClusterDo clusterDo, LoginUserVo loginUserVo,
                                                                      SearchTemplateParam searchTemplateParam,
                                                                      PageTemplateReq pageTemplateReq) {
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterDo.getId(),
                pageTemplateReq, mcConfigProperties.getTemplateListUrl(), loginUserVo.getUserName(), 0);

        List<ServerVmTemplateRespDto> templateList = new ArrayList<>();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String templateListStr = JSON.toJSONString(mcResponse.getData());
            McPageResp<McTemplateListResp> templatePage = JSONObject.parseObject(templateListStr, new
                    TypeReference<McPageResp<McTemplateListResp>>() {
                    });
            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(searchTemplateParam.getPageNo());
            mcPageInfo.setPageSize(templatePage.getRows().size());
            mcPageInfo.setRecords(templatePage.getRecords());
            mcPageInfo.setTotal(templatePage.getTotal());

            templatePage.getRows().forEach(dto -> {

                templateList.add(createTemplateRespDto(dto, clusterDo));
            });
            return new PageData(mcPageInfo, templateList);
        }
        return new PageData(null);
    }

    private PageData<ServerVmTemplateRespDto> manyClusterMcTemplate(List<CloudClusterDo> clusterDoList,
                                                                    LoginUserVo loginUserVo,
                                                                    SearchTemplateParam searchTemplateParam) {

        List<Integer> clusterIdList = clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList());

        List<Object> mcRequestObjectList = new ArrayList<>();
        clusterIdList.forEach(clusterId -> {
            PageTemplateReq pageTemplateReq = new PageTemplateReq();
            pageTemplateReq.setPage(KylinCommonConstants.FIRST_PAGE);
            pageTemplateReq.setRows(KylinCommonConstants.DEFAULT_MAX_SIZE);
            pageTemplateReq.setSearchKey(searchTemplateParam.getSearchKey());
            mcRequestObjectList.add(pageTemplateReq);
        });

        List<String> mcServerVmList = mcClusterThreadService.threadGetMcResponse(clusterIdList,
                loginUserVo.getUserName(),
                mcConfigProperties.getTemplateListUrl(), mcRequestObjectList);

        //满足条件的所有模板
        List<ServerVmTemplateRespDto> totalTemplateList = new ArrayList<>();
        McPageInfo mcPageInfo = new McPageInfo();
        mcPageInfo.setPager(searchTemplateParam.getPageNo());
        for (int i = 0; i < clusterIdList.size(); i++) {
            CloudClusterDo clusterDo = clusterDoList.get(i);
            String templateListStr = mcServerVmList.get(i);
            if (Objects.nonNull(templateListStr)) {
                McPageResp<McTemplateListResp> templatePage = JSONObject.parseObject(templateListStr, new
                        TypeReference<McPageResp<McTemplateListResp>>() {
                        });
                templatePage.getRows().forEach(template -> {
                    totalTemplateList.add(createTemplateRespDto(template, clusterDo));
                });
            }
        }
        mcPageInfo.setTotal(mcPageInfo.getRecords());
        mcPageInfo.setRecords(totalTemplateList.size());
        //总页数
        int totalPage = (mcPageInfo.getRecords() - 1) / searchTemplateParam.getPageSize() + 1;
        mcPageInfo.setTotal(totalPage);

        //从所有模板列表中截取对应数量的模板
        int totalSize = totalTemplateList.size();
        int subListStart = (searchTemplateParam.getPageNo() - 1) * searchTemplateParam.getPageSize();
        int subListEnd = searchTemplateParam.getPageNo() * searchTemplateParam.getPageSize();
        if (totalSize < subListStart) {
            return new PageData(null);
        } else if (totalSize >= subListStart && totalSize < subListEnd) {
            mcPageInfo.setPageSize(totalSize - subListStart);
            return new PageData(mcPageInfo, totalTemplateList.subList(subListStart, totalSize));
        } else {
            mcPageInfo.setPageSize(searchTemplateParam.getPageSize());
            return new PageData(mcPageInfo, totalTemplateList.subList(subListStart, subListEnd));
        }
    }


    /**
     * 创建mc模板实体
     *
     * @param mcTemplate
     * @param clusterDo
     * @return
     */
    private ServerVmTemplateRespDto createTemplateRespDto(McTemplateListResp mcTemplate, CloudClusterDo clusterDo) {
        ServerVmTemplateRespDto serverVmTemplateRespDto =
                ServerVmTemplateRespDto.builder().templateId(mcTemplate.getId()).architecture(mcTemplate
                        .getArchitecture()).systemType(mcTemplate.getSystemType()).description(mcTemplate.getDescription())
                        .cpu(mcTemplate.getCpu()).mem(mcTemplate.getMem())
                        .templateName(mcTemplate.getName()).osMachine(mcTemplate.getOperatingSystem()).build();

        //内存统一转成GB
        Integer mem = mcTemplate.getMem();
        if (Objects.equals(mcTemplate.getMemUnit(), MemUnit.MB)) {
            mem = mcTemplate.getMem() / 1024;
        }
        serverVmTemplateRespDto.setMem(mem);
        List<ServerVmNetworkDto> networks = new ArrayList<>();
        if (Objects.nonNull(mcTemplate.getInterfacesList()) && !mcTemplate.getInterfacesList().isEmpty()) {

            mcTemplate.getInterfacesList().forEach(network -> {
                ServerVmNetworkDto serverVmNetworkDto = new ServerVmNetworkDto();
                serverVmNetworkDto.setPurpose(formatNetworkStr(network.getInterfaceType()
                        , network.getPortGroup(), network.getVirtualSwitch()));
                serverVmNetworkDto.setInterfaceId(network.getId());
                // 拼接成 网络类型(Bridge) 交换机（TPlinksafg）端口组（102）
                networks.add(serverVmNetworkDto);

            });
        }
        serverVmTemplateRespDto.setNetworks(networks);
        List<String> diskSizeList = new ArrayList<>();
        List<ServerVmDiskDto> disks = new ArrayList<ServerVmDiskDto>();
        if (Objects.nonNull(mcTemplate.getDisks()) && !mcTemplate.getDisks().isEmpty()) {
            mcTemplate.getDisks().forEach(diskResp -> {
                ServerVmDiskDto serverVmDiskDto = new ServerVmDiskDto();
                serverVmDiskDto.setDiskSize(diskResp.getSize());
                serverVmDiskDto.setDiskId(diskResp.getId());
                disks.add(serverVmDiskDto);
                diskSizeList.add(diskResp.getSize().toString());
            });
        }
        String diskInfo = String.join(",", diskSizeList);
        serverVmTemplateRespDto.setDiskInfo(diskInfo);
        serverVmTemplateRespDto.setDisks(disks);
        serverVmTemplateRespDto.setClusterId(clusterDo.getId());
        serverVmTemplateRespDto.setClusterName(clusterDo.getName());
        serverVmTemplateRespDto.setClusterRemark(clusterDo.getRemark());
        return serverVmTemplateRespDto;
    }

    @Override
    public List<IsoRespDto> isoList(LoginUserVo loginUserVo) {
        List<IsoRespDto> isoList = new ArrayList<>();


        List<CloudClusterDo> clusterDoList = cloudClusterService.clusterListByUserId(loginUserVo.getUserId());

        if (clusterDoList.isEmpty()) {
            return new ArrayList<>();
        }
        if (Objects.equals(clusterDoList.size(), 1)) {
            return singleClusterIso(clusterDoList.get(0), loginUserVo);
        } else {
            return manyClusterIso(clusterDoList, loginUserVo);
        }
    }

    /**
     * 从但集群中获取ISO
     *
     * @param clusterDo
     * @param loginUserVo
     * @return
     */
    private List<IsoRespDto> singleClusterIso(CloudClusterDo clusterDo, LoginUserVo loginUserVo) {
        List<IsoRespDto> isoList = new ArrayList<>();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterDo.getId(), null,
                mcConfigProperties.getIsoListUrl(), loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            List<String> list = (List<String>) mcResponse.getData();
            list.forEach(iso -> {
                isoList.add(createMcIso(iso, clusterDo));
            });
        }
        return isoList;
    }

    /**
     * 创建mciso实体对象
     *
     * @param isoFile
     * @param clusterDo
     * @return
     */
    private IsoRespDto createMcIso(String isoFile, CloudClusterDo clusterDo) {
        IsoRespDto iso = new IsoRespDto();
        iso.setIsoFile(isoFile);
        iso.setClusterId(clusterDo.getId());
        iso.setClusterName(clusterDo.getName());
        iso.setClusterRemark(clusterDo.getRemark());
        return iso;
    }

    /**
     * 从多集群中获取iso
     *
     * @param clusterDoList
     * @param loginUserVo
     */
    private List<IsoRespDto> manyClusterIso(List<CloudClusterDo> clusterDoList, LoginUserVo loginUserVo) {

        List<Integer> clusterIdList = clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList());
        List<MCResponseData<Object>> mcServerVmList = mcClusterThreadService.threadSendToMc(clusterIdList,
                loginUserVo.getUserName(),
                mcConfigProperties.getIsoListUrl(), new ArrayList<>());

        List<IsoRespDto> isoList = new ArrayList<>();
        for (int i = 0; i < clusterIdList.size(); i++) {
            CloudClusterDo clusterDo = clusterDoList.get(i);
            MCResponseData<Object> responseData = mcServerVmList.get(i);
            if (Objects.nonNull(responseData)) {
                List<String> list = (List<String>) responseData.getData();
                list.forEach(iso -> {
                    isoList.add(createMcIso(iso, clusterDo));
                });
            }
        }
        return isoList;
    }

    @Override
    @Transactional
    public void applyServerVm(ApplyServerVmParam applyServerVmParam, LoginUserVo loginUserVo) {
        Date now = new Date();
        //插入申请工单
        CloudWorkOrderDo cloudWorkOrderDo = new CloudWorkOrderDo();
        cloudWorkOrderDo.setUserId(loginUserVo.getUserId());
        cloudWorkOrderDo.setTarget(applyServerVmParam.getServervmName());
        cloudWorkOrderDo.setStatus(WorkOrderStatus.WAIT_CHECK);
        cloudWorkOrderDo.setType(WorkOrderType.APPLY_SERVERVM);
        cloudWorkOrderDo.setCreateTime(now);
        cloudWorkOrderDo.setApplyReason(applyServerVmParam.getApplyReason());
        cloudWorkOrderDo.setCreateBy(loginUserVo.getUserId());
        workOrderService.save(cloudWorkOrderDo);

        //插入申请虚拟机详情
        CloudWorkOrderServerVmDo cloudWorkOrderServerVmDo = new CloudWorkOrderServerVmDo();
        cloudWorkOrderServerVmDo.setWorkOrderId(cloudWorkOrderDo.getId());
        cloudWorkOrderServerVmDo.setApplyServervmType(applyServerVmParam.getApplyServerVmType());
        cloudWorkOrderServerVmDo.setServervmName(applyServerVmParam.getServervmName());
        cloudWorkOrderServerVmDo.setApplyNum(applyServerVmParam.getApplyNum());
        cloudWorkOrderServerVmDo.setUseMonth(applyServerVmParam.getUseMonth());
        cloudWorkOrderServerVmDo.setDeadlineType(applyServerVmParam.getDeadlineType());
        cloudWorkOrderServerVmDo.setTemplateId(applyServerVmParam.getTemplateId());
        cloudWorkOrderServerVmDo.setOsMachine(applyServerVmParam.getOsMachine());
        cloudWorkOrderServerVmDo.setArchitecture(applyServerVmParam.getArchitecture());
        cloudWorkOrderServerVmDo.setSystemType(applyServerVmParam.getSystemType());
        cloudWorkOrderServerVmDo.setCpu(applyServerVmParam.getCpu());
        cloudWorkOrderServerVmDo.setMem(applyServerVmParam.getMem());
        cloudWorkOrderServerVmDo.setMemUnit(MemUnit.GB);
        cloudWorkOrderServerVmDo.setCreateBy(loginUserVo.getUserId());
        cloudWorkOrderServerVmDo.setCreateTime(now);
        cloudWorkOrderServerVmDo.setDescription(applyServerVmParam.getDescription());
        cloudWorkOrderServerVmDo.setClusterId(applyServerVmParam.getClusterId());
        cloudWorkOrderServerVmService.save(cloudWorkOrderServerVmDo);

        //磁盘和网卡信息入库
        insertDiskAndNetwork(applyServerVmParam, cloudWorkOrderDo.getId(), loginUserVo.getUserId());

        //Iso处理
        List<CloudWorkOrderServerVmIsoDo> isoDoList = new ArrayList<>();
        if (Objects.nonNull(applyServerVmParam.getIsoList()) && !applyServerVmParam.getIsoList().isEmpty()) {
            applyServerVmParam.getIsoList().stream().forEach(iso -> {
                CloudWorkOrderServerVmIsoDo isoDo = new CloudWorkOrderServerVmIsoDo();
                isoDo.setIsoFile(iso);
                isoDo.setWorkOrderId(cloudWorkOrderDo.getId());
                isoDo.setModifyType(ModifyType.NONE);
                isoDo.setCreateBy(loginUserVo.getUserId());
                isoDo.setCreateTime(now);
                isoDo.setOldIsoFile(iso);
                isoDoList.add(isoDo);
            });
            cloudWorkOrderServerVmIsoService.saveBatch(isoDoList);
        }
    }


    /**
     * 磁盘和网卡信息入库处理，
     * diskList 磁盘信息
     * networkList 网卡信息
     * networkList 工单ID
     * loginUserId 登陆者
     */
    private void insertDiskAndNetwork(ApplyServerVmParam applyServerVmParam, Integer workOrderId, Integer loginUserId) {
        Date now = new Date();
        //插入硬盘信息
        List<ServerVmDiskParam> diskList = applyServerVmParam.getDiskList();
        if (Objects.nonNull(diskList) && diskList.size() > 0) {
            List<CloudWorkOrderServerVmDiskDo> diskDoList = new ArrayList<>();
            diskList.forEach(dto -> {
                CloudWorkOrderServerVmDiskDo cloudWorkOrderServerVmDiskDo = new CloudWorkOrderServerVmDiskDo();
                cloudWorkOrderServerVmDiskDo.setWorkOrderId(workOrderId);
                cloudWorkOrderServerVmDiskDo.setDiskSize(dto.getDiskSize());
                cloudWorkOrderServerVmDiskDo.setPurpose(dto.getPurpose());
                cloudWorkOrderServerVmDiskDo.setType(dto.getType());
                cloudWorkOrderServerVmDiskDo.setCreateBy(loginUserId);
                cloudWorkOrderServerVmDiskDo.setCreateTime(now);
                cloudWorkOrderServerVmDiskDo.setDiskId(dto.getDiskId());
                cloudWorkOrderServerVmDiskDo.setModifyType((dto.getDiskId() > 0) ? ModifyType.NONE : ModifyType.ADD);
                cloudWorkOrderServerVmDiskDo.setOldDiskSize(dto.getDiskSize());
                diskDoList.add(cloudWorkOrderServerVmDiskDo);
            });
            cloudWorkOrderServerVmDiskService.saveBatch(diskDoList);
        }
        List<ServerVmNetworkParam> networkList = applyServerVmParam.getNetworkList();
        //网卡信息
        if (Objects.nonNull(networkList) && networkList.size() > 0) {
            List<CloudWorkOrderServerVmNetworkDo> cloudWorkOrderServerVmNetworkDos = new ArrayList<>();
            networkList.forEach(network -> {
                CloudWorkOrderServerVmNetworkDo cloudWorkOrderServerVmNetworkDo = new CloudWorkOrderServerVmNetworkDo();
                cloudWorkOrderServerVmNetworkDo.setWorkOrderId(workOrderId);
                cloudWorkOrderServerVmNetworkDo.setPurpose(network.getPurpose());
                cloudWorkOrderServerVmNetworkDo.setType(network.getType());
                cloudWorkOrderServerVmNetworkDo.setCreateBy(loginUserId);
                cloudWorkOrderServerVmNetworkDo.setCreateTime(now);
                cloudWorkOrderServerVmNetworkDo.setInterfaceId(network.getInterfaceId());
                cloudWorkOrderServerVmNetworkDo.setModifyType((network.getInterfaceId() > 0) ? ModifyType.NONE :
                        ModifyType.ADD);
                cloudWorkOrderServerVmNetworkDos.add(cloudWorkOrderServerVmNetworkDo);
            });
            cloudWorkOrderServerVmNetworkService.saveBatch(cloudWorkOrderServerVmNetworkDos);
        }
    }

    @Override
    @Transactional
    public void modifyServerVm(ModifyServerVmParam modifyServerVmParam, LoginUserVo loginUserVo) {


        //如果只是修改了名称，直接调用mc接口进行修改名称，不用新建一个工单
        if (modifyServerVmParam.getOnlyModifyName()) {
            UpdateServerVmNameParam updateServerVmNameParam = new UpdateServerVmNameParam();
            updateServerVmNameParam.setAliasName(modifyServerVmParam.getServervmName());
            updateServerVmNameParam.setUuid(modifyServerVmParam.getMachineUuid());
            updateMachineName(updateServerVmNameParam, loginUserVo);
            return;
        }

        Date now = new Date();
        //插入申请工单
        CloudWorkOrderDo cloudWorkOrderDo = new CloudWorkOrderDo();
        cloudWorkOrderDo.setUserId(loginUserVo.getUserId());
        cloudWorkOrderDo.setTarget(modifyServerVmParam.getServervmName());
        cloudWorkOrderDo.setStatus(WorkOrderStatus.WAIT_CHECK);
        cloudWorkOrderDo.setType(WorkOrderType.MODIFY_SERVERVM);
        cloudWorkOrderDo.setCreateTime(now);
        cloudWorkOrderDo.setApplyReason(modifyServerVmParam.getApplyReason());
        cloudWorkOrderDo.setCreateBy(loginUserVo.getUserId());
        workOrderService.save(cloudWorkOrderDo);

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(modifyServerVmParam.getMachineUuid(),
                        loginUserVo.getUserId());

        //插入变更云服务器详情
        CloudWorkOrderModifyServerVmDo cloudWorkOrderModifyServerVmDo = new CloudWorkOrderModifyServerVmDo();
        cloudWorkOrderModifyServerVmDo.setMachineUuid(modifyServerVmParam.getMachineUuid());
        cloudWorkOrderModifyServerVmDo.setWorkOrderId(cloudWorkOrderDo.getId());
        cloudWorkOrderModifyServerVmDo.setServervmName(modifyServerVmParam.getServervmName());
        cloudWorkOrderModifyServerVmDo.setDeadlineType(modifyServerVmParam.getDeadlineType());
        cloudWorkOrderModifyServerVmDo.setOsMachine(modifyServerVmParam.getOsMachine());
        cloudWorkOrderModifyServerVmDo.setArchitecture(modifyServerVmParam.getArchitecture());
        cloudWorkOrderModifyServerVmDo.setSystemType(modifyServerVmParam.getSystemType());
        cloudWorkOrderModifyServerVmDo.setCpu(modifyServerVmParam.getCpu());
        cloudWorkOrderModifyServerVmDo.setOriginalCpu(modifyServerVmParam.getOriginalCpu());
        cloudWorkOrderModifyServerVmDo.setMem(modifyServerVmParam.getMem());
        cloudWorkOrderModifyServerVmDo.setOriginalMem(modifyServerVmParam.getOriginalMem());
        cloudWorkOrderModifyServerVmDo.setMemUnit(modifyServerVmParam.getMemUnit());
        cloudWorkOrderModifyServerVmDo.setDeadlineTime(userMachineDo.getDeadlineTime());
        if (modifyServerVmParam.isAddDeadTimeChecked()) {
            //获取新添加过期时间
            Integer addNewDeadTime = Objects.equals(modifyServerVmParam.getAddNewDeadTimeUnit(), "YEAR") ?
                    12 * modifyServerVmParam.getAddNewDeadTime() : modifyServerVmParam.getAddNewDeadTime();
            cloudWorkOrderModifyServerVmDo.setDeadlineTime(DateUtils.getMonthAfter(userMachineDo.getDeadlineTime(),
                    addNewDeadTime));
        }

        cloudWorkOrderModifyServerVmDo.setCreateBy(loginUserVo.getUserId());
        cloudWorkOrderModifyServerVmDo.setCreateTime(now);
        cloudWorkOrderModifyServerVmService.save(cloudWorkOrderModifyServerVmDo);

        modifyDiskAndNetwork(modifyServerVmParam, cloudWorkOrderDo.getId(), loginUserVo.getUserId());

    }

    /**
     * 磁盘和网卡信息入库处理，
     * diskList 磁盘信息
     * networkList 网卡信息
     * networkList 工单ID
     * loginUserId 登陆者
     * addApply 是否是申请云服务器
     */
    private void modifyDiskAndNetwork(ModifyServerVmParam modifyServerVmParam, Integer workOrderId,
                                      Integer loginUserId) {
        Date now = new Date();
        //插入硬盘信息
        List<ServerVmDiskParam> diskList = modifyServerVmParam.getDiskList();
        List<ServerVmDiskParam> oldDiskList = modifyServerVmParam.getOldDiskList();
        List<CloudWorkOrderServerVmDiskDo> diskDoList = new ArrayList<>();
        oldDiskList.forEach(oldDisk -> {
            //用原来的磁盘和新变动的磁盘进行对比
            ServerVmDiskParam diskParam = diskList.stream().filter(disk -> Objects.equals(oldDisk.getDiskId(),
                    disk.getDiskId())).findFirst().orElse(null);
            boolean deleteFlag = Objects.isNull(diskParam);
            CloudWorkOrderServerVmDiskDo cloudWorkOrderServerVmDiskDo = new CloudWorkOrderServerVmDiskDo();
            cloudWorkOrderServerVmDiskDo.setWorkOrderId(workOrderId);
            cloudWorkOrderServerVmDiskDo.setDiskSize(deleteFlag ? oldDisk.getDiskSize() : diskParam.getDiskSize());
            cloudWorkOrderServerVmDiskDo.setPurpose(deleteFlag ? null : diskParam.getPurpose());
            cloudWorkOrderServerVmDiskDo.setType(ApplyMcServerVmType.original);
            cloudWorkOrderServerVmDiskDo.setCreateBy(loginUserId);
            cloudWorkOrderServerVmDiskDo.setCreateTime(now);
            cloudWorkOrderServerVmDiskDo.setDiskId(oldDisk.getDiskId());
            ModifyType modifyType = deleteFlag ? ModifyType.DELETE :
                    Objects.equals(oldDisk.getDiskSize(), diskParam.getDiskSize()) ? ModifyType.NONE :
                            ModifyType.MODIFY;
            cloudWorkOrderServerVmDiskDo.setModifyType(modifyType);
            cloudWorkOrderServerVmDiskDo.setOldDiskSize(oldDisk.getDiskSize());
            diskDoList.add(cloudWorkOrderServerVmDiskDo);
        });
        if (Objects.nonNull(diskList) && diskList.size() > 0) {
            diskList.forEach(dto -> {
                if (Objects.equals(dto.getDiskId(), 0L)) {
                    //新增的磁盘
                    CloudWorkOrderServerVmDiskDo cloudWorkOrderServerVmDiskDo = new CloudWorkOrderServerVmDiskDo();
                    cloudWorkOrderServerVmDiskDo.setWorkOrderId(workOrderId);
                    cloudWorkOrderServerVmDiskDo.setDiskSize(dto.getDiskSize());
                    cloudWorkOrderServerVmDiskDo.setPurpose(dto.getPurpose());
                    cloudWorkOrderServerVmDiskDo.setType(ApplyMcServerVmType.custom);
                    cloudWorkOrderServerVmDiskDo.setCreateBy(loginUserId);
                    cloudWorkOrderServerVmDiskDo.setCreateTime(now);
                    cloudWorkOrderServerVmDiskDo.setDiskId(dto.getDiskId());
                    cloudWorkOrderServerVmDiskDo.setModifyType(ModifyType.ADD);
                    cloudWorkOrderServerVmDiskDo.setOldDiskSize(dto.getDiskSize());
                    cloudWorkOrderServerVmDiskDo.setDiskId(0L);
                    diskDoList.add(cloudWorkOrderServerVmDiskDo);
                }
            });
        }
        if (!diskDoList.isEmpty()) {
            cloudWorkOrderServerVmDiskService.saveBatch(diskDoList);
        }

        //网卡信息
        List<ServerVmNetworkParam> networkList = modifyServerVmParam.getNetworkList();
        List<ServerVmNetworkParam> oldNetworkList = modifyServerVmParam.getOldNetworkList();
        List<CloudWorkOrderServerVmNetworkDo> cloudWorkOrderServerVmNetworkDos = new ArrayList<>();
        oldNetworkList.forEach(oldNetWork -> {
            //用原来的网卡ID和变动后提交的网卡ID进行对比
            ServerVmNetworkParam serverVmNetworkParam =
                    networkList.stream().filter(network -> Objects.equals(oldNetWork.getInterfaceId(),
                            network.getInterfaceId())).findFirst().orElse(null);
            boolean deleteFlag = Objects.isNull(serverVmNetworkParam);

            CloudWorkOrderServerVmNetworkDo cloudWorkOrderServerVmNetworkDo = new CloudWorkOrderServerVmNetworkDo();
            cloudWorkOrderServerVmNetworkDo.setWorkOrderId(workOrderId);
            cloudWorkOrderServerVmNetworkDo.setPurpose(deleteFlag ? oldNetWork.getPurpose() :
                    serverVmNetworkParam.getPurpose());
            cloudWorkOrderServerVmNetworkDo.setType(ApplyMcServerVmType.original);
            cloudWorkOrderServerVmNetworkDo.setCreateBy(loginUserId);
            cloudWorkOrderServerVmNetworkDo.setCreateTime(now);
            cloudWorkOrderServerVmNetworkDo.setInterfaceId(oldNetWork.getInterfaceId());
            cloudWorkOrderServerVmNetworkDo.setModifyType(deleteFlag ? ModifyType.DELETE : ModifyType.NONE);
            cloudWorkOrderServerVmNetworkDos.add(cloudWorkOrderServerVmNetworkDo);
        });
        if (Objects.nonNull(networkList) && networkList.size() > 0) {
            networkList.forEach(network -> {
                if (Objects.equals(network.getInterfaceId(), 0L)) {
                    //新增的磁盘
                    CloudWorkOrderServerVmNetworkDo cloudWorkOrderServerVmNetworkDo =
                            new CloudWorkOrderServerVmNetworkDo();
                    cloudWorkOrderServerVmNetworkDo.setWorkOrderId(workOrderId);
                    cloudWorkOrderServerVmNetworkDo.setPurpose(network.getPurpose());
                    cloudWorkOrderServerVmNetworkDo.setType(ApplyMcServerVmType.custom);
                    cloudWorkOrderServerVmNetworkDo.setCreateBy(loginUserId);
                    cloudWorkOrderServerVmNetworkDo.setCreateTime(now);
                    cloudWorkOrderServerVmNetworkDo.setInterfaceId(network.getInterfaceId());
                    cloudWorkOrderServerVmNetworkDo.setModifyType(ModifyType.ADD);
                    cloudWorkOrderServerVmNetworkDos.add(cloudWorkOrderServerVmNetworkDo);
                }

            });
        }
        if (!cloudWorkOrderServerVmNetworkDos.isEmpty()) {
            cloudWorkOrderServerVmNetworkService.saveBatch(cloudWorkOrderServerVmNetworkDos);
        }

    }

    @Override
    public ServerVmDetailRespDto getServerVmDetail(ServerVmBaseParam serverVmPageParam, LoginUserVo loginUserVo) {

        ServerVmDetailRespDto serverVmDetailRespDto = new ServerVmDetailRespDto();

        McServerVmInfoResp serverVmInfo = serverVmInfo(serverVmPageParam, loginUserVo);


        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmPageParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        if (Objects.nonNull(userMachineDo)) {
            serverVmDetailRespDto.setDeadlineType(userMachineDo.getDeadlineType());
            serverVmDetailRespDto.setDeadlineTime(DateUtils.format(userMachineDo.getDeadlineTime(),
                    DateUtils.DATE_YYYY_MM_DD));
        }

        if (Objects.nonNull(serverVmInfo)) {

            if (Objects.nonNull(serverVmInfo.getMachineInfo())) {
                serverVmDetailRespDto.setStatus(serverVmInfo.getMachineInfo().getStatus());
                serverVmDetailRespDto.setOsMachine(serverVmInfo.getMachineInfo().getOsName());
                serverVmDetailRespDto.setArchitecture(serverVmInfo.getMachineInfo().getArchitecture());
                serverVmDetailRespDto.setSystemType(serverVmInfo.getMachineInfo().getSystemType());
                serverVmDetailRespDto.setAliasName(serverVmInfo.getMachineInfo().getAlisname());
            }
            if (Objects.nonNull(serverVmInfo.getDeviceInfo())) {
                serverVmDetailRespDto.setCpu(serverVmInfo.getDeviceInfo().getCpu().getCpus());
                serverVmDetailRespDto.setMem(serverVmInfo.getDeviceInfo().getMemory().getMemoryTotal());
                serverVmDetailRespDto.setMemUnit(MemUnit.GB);

            }

            if (Objects.nonNull(serverVmInfo.getDeviceInfo().getDisks()) && !serverVmInfo.getDeviceInfo().getDisks().isEmpty()) {
                List<ServerVmDiskDto> disks = new ArrayList<>();
                serverVmInfo.getDeviceInfo().getDisks().forEach(disk -> {
                    ServerVmDiskDto serverVmDiskDto = new ServerVmDiskDto();
                    serverVmDiskDto.setDiskSize(disk.getTotal());
                    serverVmDiskDto.setDiskId(disk.getId());
                    disks.add(serverVmDiskDto);
                });
                serverVmDetailRespDto.setDisks(disks);

            }

            if (Objects.nonNull(serverVmInfo.getDeviceInfo().getInterfaces()) && !serverVmInfo.getDeviceInfo().getInterfaces().isEmpty()) {
                List<ServerVmNetworkDto> networks = new ArrayList<>();
                serverVmInfo.getDeviceInfo().getInterfaces().forEach(network -> {

                    ServerVmNetworkDto serverVmNetworkDto = new ServerVmNetworkDto();
                    serverVmNetworkDto.setPurpose(formatNetworkStr(network.getInterfaceType()
                            , network.getPortGroup(), network.getVirtualSwitch()));
                    serverVmNetworkDto.setInterfaceId(network.getId());
                    networks.add(serverVmNetworkDto);
                });
                serverVmDetailRespDto.setNetworks(networks);

            }

        }
        return serverVmDetailRespDto;
    }

    /*
     *  封装网络信息
     *  如 网络类型(Bridge) 交换机（TPlinksafg）端口组（102）
     */
    private String formatNetworkStr(String interfaceType, String portGroup, String virtualSwitch) {
        StringBuilder purposeSb = new StringBuilder();

        purposeSb.append(KylinSelfConstants.NETWORK_TYPE).append(KylinSelfConstants.LEFT_BRACKET)
                .append(interfaceType).append(KylinSelfConstants.RIGHT_BRACKET)
                .append(KylinSelfConstants.SPACE);

        purposeSb.append(KylinSelfConstants.NETWORK_SWITCH).append(KylinSelfConstants
                .LEFT_BRACKET).append(virtualSwitch)
                .append(KylinSelfConstants.RIGHT_BRACKET).append(KylinSelfConstants.SPACE);

        purposeSb.append(KylinSelfConstants.NETWORK_PORT).append(KylinSelfConstants.LEFT_BRACKET)
                .append(portGroup).append(KylinSelfConstants.RIGHT_BRACKET);

        return purposeSb.toString();
    }

    @Override
    public ApplyServerVmDetailRespDto getApplyServerVmDetailByWorkOrderId(Integer workOrderId,
                                                                          LoginUserVo loginUserVo) {
        ApplyServerVmDetailRespDto applyServerVmDetailRespDto = new ApplyServerVmDetailRespDto();

        selfWorkOrderService.formatBaseWorkOrderDetail(workOrderId, applyServerVmDetailRespDto);

        CloudWorkOrderDo cloudWorkOrderDo = workOrderService.getById(workOrderId);

        //查询服务器申请详情表
        CloudWorkOrderServerVmDo queryServerVmDo = new CloudWorkOrderServerVmDo();
        queryServerVmDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderServerVmDo> wrapper = new QueryWrapper<>(queryServerVmDo);
        CloudWorkOrderServerVmDo serverVmDo = cloudWorkOrderServerVmService.getOne(wrapper);
        if (Objects.nonNull(serverVmDo)) {
            applyServerVmDetailRespDto.setApplyServerVmType(serverVmDo.getApplyServervmType());
            applyServerVmDetailRespDto.setApplyNum(serverVmDo.getApplyNum());
            applyServerVmDetailRespDto.setIfModifyApplyNum(false);
            applyServerVmDetailRespDto.setModifyApplyNum(serverVmDo.getModifyApplyNum());

            applyServerVmDetailRespDto.setUseMonth(serverVmDo.getUseMonth());
            applyServerVmDetailRespDto.setDeadlineType(serverVmDo.getDeadlineType());
            applyServerVmDetailRespDto.setDeadlineTypeDesc(serverVmDo.getDeadlineType().getDesc());
            applyServerVmDetailRespDto.setOsMachine(serverVmDo.getOsMachine());
            applyServerVmDetailRespDto.setArchitecture(serverVmDo.getArchitecture());
            applyServerVmDetailRespDto.setCpu(serverVmDo.getCpu());
            applyServerVmDetailRespDto.setModifyCpu(serverVmDo.getModifyCpu());
            applyServerVmDetailRespDto.setIfModifyCpu(false);
            applyServerVmDetailRespDto.setMem(serverVmDo.getMem());
            applyServerVmDetailRespDto.setModifyMem(serverVmDo.getModifyMem());
            applyServerVmDetailRespDto.setIfModifyMem(false);
            applyServerVmDetailRespDto.setDescription(serverVmDo.getDescription());

            applyServerVmDetailRespDto.setClusterName(cloudClusterService.getById(serverVmDo.getClusterId()).getName());

            if (Objects.equals(cloudWorkOrderDo.getStatus(), WorkOrderStatus.CHECK_PASS)) {
                if (!Objects.equals(serverVmDo.getCpu(), serverVmDo.getModifyCpu())) {
                    applyServerVmDetailRespDto.setIfModifyCpu(true);
                }
                if (!Objects.equals(serverVmDo.getMem(), serverVmDo.getModifyMem())) {
                    applyServerVmDetailRespDto.setIfModifyMem(true);
                }
                if (!Objects.equals(serverVmDo.getApplyNum(), serverVmDo.getModifyApplyNum())) {
                    applyServerVmDetailRespDto.setIfModifyApplyNum(true);
                }
            }

            applyServerVmDetailRespDto.setMenUtil(serverVmDo.getMemUnit());
            //查询硬盘信息
            CloudWorkOrderServerVmDiskDo queryDiskDo = new CloudWorkOrderServerVmDiskDo();
            queryDiskDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmDiskDo> diskQueryWrapper = new QueryWrapper<>(queryDiskDo);
            List<CloudWorkOrderServerVmDiskDo> diskDoList = cloudWorkOrderServerVmDiskService.list(diskQueryWrapper);
            List<ServerVmDiskDto> disks = new ArrayList<>();
            if (diskDoList.size() > 0) {
                diskDoList.forEach(disk -> {
                    ServerVmDiskDto diskDto = new ServerVmDiskDto();
                    diskDto.setDiskSize(disk.getDiskSize());
                    diskDto.setPurpose(disk.getPurpose());
                    diskDto.setOldDiskSize(disk.getOldDiskSize());
                    diskDto.setModifyType(disk.getModifyType());
                    disks.add(diskDto);
                });
            }
            applyServerVmDetailRespDto.setDisks(disks);

            //查询网卡信息
            CloudWorkOrderServerVmNetworkDo queryNetworkDo = new CloudWorkOrderServerVmNetworkDo();
            queryNetworkDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmNetworkDo> networkQueryWrapper = new QueryWrapper<>(queryNetworkDo);
            List<CloudWorkOrderServerVmNetworkDo> networkDoList =
                    cloudWorkOrderServerVmNetworkService.list(networkQueryWrapper);
            List<ServerVmNetworkDto> networks = new ArrayList<>();
            networkDoList.forEach(network -> {
                ServerVmNetworkDto serverVmNetworkDto = new ServerVmNetworkDto();
                serverVmNetworkDto.setPurpose(network.getPurpose());
                serverVmNetworkDto.setModifyType(network.getModifyType());
                serverVmNetworkDto.setIpBindMac(network.getIpBindMac());
                serverVmNetworkDto.setManualSetIp(network.getManualSetIp());
                serverVmNetworkDto.setAutomaticAcqIp(network.getAutomaticAcqIp());
                serverVmNetworkDto.setIp(network.getIp());
                serverVmNetworkDto.setMask(network.getMask());
                serverVmNetworkDto.setGw(network.getGw());
                serverVmNetworkDto.setDns1(network.getDns1());
                serverVmNetworkDto.setDns2(network.getDns2());
                boolean setIpInfo = network.getIpBindMac() || network.getManualSetIp() || network.getAutomaticAcqIp();
                serverVmNetworkDto.setSetIpInfo(setIpInfo);
                networks.add(serverVmNetworkDto);
            });
            applyServerVmDetailRespDto.setNetworks(networks);

            //光驱信息
            CloudWorkOrderServerVmIsoDo queryIsoDo = new CloudWorkOrderServerVmIsoDo();
            queryIsoDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmIsoDo> isoQueryWrapper = new QueryWrapper<>(queryIsoDo);
            List<CloudWorkOrderServerVmIsoDo> isoDoList = cloudWorkOrderServerVmIsoService.list(isoQueryWrapper);
            List<ServerVmIsoDto> isoDtoList = new ArrayList<>();
            if (!isoDoList.isEmpty()) {
                isoDoList.stream().forEach(iso -> {
                    ServerVmIsoDto vmIsoDto = new ServerVmIsoDto();
                    vmIsoDto.setIsoFile(iso.getIsoFile());
                    vmIsoDto.setModifyType(iso.getModifyType());
                    vmIsoDto.setOldIsoFile(iso.getOldIsoFile());
                    isoDtoList.add(vmIsoDto);
                });
            }
            applyServerVmDetailRespDto.setIsoList(isoDtoList);

            if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.TEMPLATE)) {
                //获取模板名称
                QueryMcServerDetailParamReq queryMcServerDetailParamReq = new QueryMcServerDetailParamReq();
                queryMcServerDetailParamReq.setServervmId(serverVmDo.getTemplateId());
                McTemplateDetailResp mcTemplateDetailResp = getTemplateDetail(serverVmDo.getClusterId(),
                        queryMcServerDetailParamReq,
                        loginUserVo);
                if (Objects.nonNull(mcTemplateDetailResp)) {
                    applyServerVmDetailRespDto.setTemplateName(mcTemplateDetailResp.getAlisname());
                }
            }

        }

        return applyServerVmDetailRespDto;
    }

    @Override
    public ApplyDeferredDetailRespDto applyDeferredDetailByWorkOrderId(Integer workOrderId) {
        ApplyDeferredDetailRespDto applyDeferredDetailRespDto = new ApplyDeferredDetailRespDto();

        selfWorkOrderService.formatBaseWorkOrderDetail(workOrderId, applyDeferredDetailRespDto);


        //查询延期申请表
        CloudWorkOrderDeferredMachineDo cloudWorkOrderDeferredMachineDo = new CloudWorkOrderDeferredMachineDo();
        cloudWorkOrderDeferredMachineDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderDeferredMachineDo> queryWrapper = new QueryWrapper<>
                (cloudWorkOrderDeferredMachineDo);
        CloudWorkOrderDeferredMachineDo queryDo = deferredMachineService.getOne(queryWrapper);

        applyDeferredDetailRespDto.setOldDeadlineTime(DateUtils.format(queryDo.getOldDeadlineTime()));
        applyDeferredDetailRespDto.setNewDeadlineTime(DateUtils.format(queryDo.getDeadlineTime()));

        return applyDeferredDetailRespDto;
    }

    @Override
    public ModifyServerVmDetailRespDto modifyServerVmDetailByWorkOrderId(Integer workOrderId) {
        ModifyServerVmDetailRespDto modifyServerVmDetailRespDto = new ModifyServerVmDetailRespDto();

        selfWorkOrderService.formatBaseWorkOrderDetail(workOrderId, modifyServerVmDetailRespDto);

        //查询变更云服务器详情
        CloudWorkOrderModifyServerVmDo modifyServerVmDo = new CloudWorkOrderModifyServerVmDo();
        modifyServerVmDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderModifyServerVmDo> wrapper = new QueryWrapper<>(modifyServerVmDo);
        CloudWorkOrderModifyServerVmDo serverVmDo = cloudWorkOrderModifyServerVmService.getOne(wrapper);

        //查询用户已经拥有的云服务器
        CloudWorkOrderDo cloudWorkOrderDo = workOrderService.getById(workOrderId);

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmDo.getMachineUuid(),
                        cloudWorkOrderDo.getUserId());
        if (Objects.nonNull(userMachineDo)) {


            String deadLineTime = DateUtils.format(serverVmDo.getDeadlineTime(), DateUtils.DATE_ALL_PATTEN);

            modifyServerVmDetailRespDto.setDeadLineTime(deadLineTime);
            modifyServerVmDetailRespDto.setDeadlineType(serverVmDo.getDeadlineType());
            modifyServerVmDetailRespDto.setDeadlineTypeDesc(serverVmDo.getDeadlineType().getDesc());
            modifyServerVmDetailRespDto.setOsMachine(serverVmDo.getOsMachine());
            modifyServerVmDetailRespDto.setArchitecture(serverVmDo.getArchitecture());

            modifyServerVmDetailRespDto.setCpu(serverVmDo.getCpu());
            modifyServerVmDetailRespDto.setIfModifyCpu(!Objects.equals(serverVmDo.getCpu(),
                    serverVmDo.getOriginalCpu()));
            modifyServerVmDetailRespDto.setOriginalCpu(serverVmDo.getOriginalCpu());

            modifyServerVmDetailRespDto.setMem(serverVmDo.getMem());
            modifyServerVmDetailRespDto.setMenUtil(serverVmDo.getMemUnit());
            modifyServerVmDetailRespDto.setOriginalMem(serverVmDo.getOriginalMem());
            modifyServerVmDetailRespDto.setIfModifyMem(!Objects.equals(serverVmDo.getMem(),
                    serverVmDo.getOriginalMem()));


            //查询硬盘信息
            CloudWorkOrderServerVmDiskDo queryDiskDo = new CloudWorkOrderServerVmDiskDo();
            queryDiskDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmDiskDo> diskQueryWrapper = new QueryWrapper<>(queryDiskDo);
            List<CloudWorkOrderServerVmDiskDo> diskDoList = cloudWorkOrderServerVmDiskService.list(diskQueryWrapper);
            List<ServerVmDiskDto> disks = new ArrayList<>();
            if (diskDoList.size() > 0) {
                diskDoList.forEach(disk -> {
                    ServerVmDiskDto diskDto = new ServerVmDiskDto();
                    diskDto.setDiskSize(disk.getDiskSize());
                    diskDto.setPurpose(disk.getPurpose());
                    diskDto.setModifyType(disk.getModifyType());
                    diskDto.setOldDiskSize(disk.getOldDiskSize());
                    disks.add(diskDto);
                });
            }
            modifyServerVmDetailRespDto.setDisks(disks);

            //查询网卡信息
            CloudWorkOrderServerVmNetworkDo queryNetworkDo = new CloudWorkOrderServerVmNetworkDo();
            queryNetworkDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmNetworkDo> networkQueryWrapper = new QueryWrapper<>(queryNetworkDo);
            List<CloudWorkOrderServerVmNetworkDo> networkDoList =
                    cloudWorkOrderServerVmNetworkService.list(networkQueryWrapper);
            List<ServerVmNetworkDto> networks = new ArrayList<>();
            networkDoList.forEach(network -> {
                ServerVmNetworkDto serverVmNetworkDto = new ServerVmNetworkDto();
                serverVmNetworkDto.setPurpose(network.getPurpose());
                serverVmNetworkDto.setModifyType(network.getModifyType());
                serverVmNetworkDto.setIpBindMac(network.getIpBindMac());
                serverVmNetworkDto.setManualSetIp(network.getManualSetIp());
                serverVmNetworkDto.setAutomaticAcqIp(network.getAutomaticAcqIp());
                serverVmNetworkDto.setIp(network.getIp());
                serverVmNetworkDto.setMask(network.getMask());
                serverVmNetworkDto.setGw(network.getGw());
                serverVmNetworkDto.setDns1(network.getDns1());
                serverVmNetworkDto.setDns2(network.getDns2());
                boolean setIpInfo = network.getIpBindMac() || network.getManualSetIp() || network.getAutomaticAcqIp();
                serverVmNetworkDto.setSetIpInfo(setIpInfo);
                networks.add(serverVmNetworkDto);
            });
            modifyServerVmDetailRespDto.setNetworks(networks);

        }
        return modifyServerVmDetailRespDto;
    }

    @Override
    @Transactional
    public void applyDeferredMachine(ApplyDeferredParam applyDeferredParam, LoginUserVo loginUserVo) {

        Date now = new Date();
        //插入申请工单
        CloudWorkOrderDo cloudWorkOrderDo = new CloudWorkOrderDo();
        cloudWorkOrderDo.setUserId(loginUserVo.getUserId());
        cloudWorkOrderDo.setTarget(applyDeferredParam.getMachineName());
        cloudWorkOrderDo.setStatus(WorkOrderStatus.WAIT_CHECK);
        cloudWorkOrderDo.setType(WorkOrderType.DEFERRED_SERVERVM);
        cloudWorkOrderDo.setCreateTime(now);
        cloudWorkOrderDo.setApplyReason(applyDeferredParam.getApplyReason());
        cloudWorkOrderDo.setCreateBy(loginUserVo.getUserId());
        workOrderService.save(cloudWorkOrderDo);

        //查询用户拥有的云服务器详情-获取原过期时间
        CloudUserMachineDo queryDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(applyDeferredParam.getUserMachineUuid(),
                        cloudWorkOrderDo.getUserId());


        Date deadlineTime = DateUtils.parse(applyDeferredParam.getDeadlineTime() + DateUtils.DAY_END, DateUtils
                .DATE_ALL_PATTEN);
        //插入延期表
        CloudWorkOrderDeferredMachineDo deferredMachineDo = new CloudWorkOrderDeferredMachineDo();
        deferredMachineDo.setWorkOrderId(cloudWorkOrderDo.getId());
        deferredMachineDo.setUserMachineUuid(applyDeferredParam.getUserMachineUuid());

        deferredMachineDo.setDeadlineTime(deadlineTime);
        deferredMachineDo.setOldDeadlineTime(queryDo.getDeadlineTime());
        deferredMachineDo.setCreateBy(loginUserVo.getUserId());
        deferredMachineDo.setCreateTime(now);
        deferredMachineService.save(deferredMachineDo);
    }

    @Override
    public PageData<PageServerVmRespDto> listServerVm(ServerVmPageParam serverVmPageParam, LoginUserVo loginUserVo) {

        //查询用户拥有的云服务器uuid
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setUserId(loginUserVo.getUserId());
        cloudUserMachineDo.setDeleteFlag(false);
        //是否过期过滤
        if (Objects.nonNull(serverVmPageParam.getVmStatus()) && !Objects.equals(McServerVmStatus.ALL,
                serverVmPageParam.getVmStatus())) {
            cloudUserMachineDo.setDeadlineFlag(Objects.equals(McServerVmStatus.OVERDUE, serverVmPageParam.getVmStatus
                    ()));
        }

        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        List<CloudUserMachineDo> userMachineDoList =
                cloudUserMachineService.list(queryWrapper);

        if (userMachineDoList.isEmpty()) {
            return new PageData(null);
        }


        //将用户云服务器安装集群进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap =
                userMachineDoList.stream().collect(Collectors.groupingBy(CloudUserMachineDo::getClusterId));

        if (Objects.equals(clusterUserMachineMap.size(), 1)) {
            //单集群
            return singleClusterUserMachine(userMachineDoList, serverVmPageParam, loginUserVo,
                    userMachineDoList.get(0).getClusterId());
        } else {
            //多集群
            return manyClusterUserMachine(clusterUserMachineMap, serverVmPageParam, loginUserVo,
                    userMachineDoList.size());
        }
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
                                                           List<CloudUserMachineDo> userMachineDoList,
                                                           LoginUserVo loginUserVo) {

        //根据uuid 过滤，保存起来的用户云服务器。
        CloudUserMachineDo userMachineDo = userMachineDoList.stream().filter(userMachine -> Objects.equals
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
                //usage.put("disk", "0");
                usage.put("mem", "0");
            }
            if (StringUtils.isNotBlank(mcServerVm.getCpuRate())) {
                usage.put("cpu", mcServerVm.getCpuRate().replaceAll("%", ""));
            }
//                    if (StringUtils.isNotBlank(mcServerVm.getDiskRate())) {
//                        usage.put("disk", mcServerVm.getDiskRate().replaceAll("%", ""));
//                    }
            if (StringUtils.isNotBlank(mcServerVm.getMemoryRate())) {
                usage.put("mem", mcServerVm.getMemoryRate().replaceAll("%", ""));
            }
            serverVmRespDto.setUsage(usage.toJSONString());
        }
        return serverVmRespDto;
    }

    /**
     * 从mc查询服务器列表
     */
    private McPageResp<McServerVmPageDetailResp> listServerVmFromMc(ServerVmListReq serverVmListReq, LoginUserVo
            loginUserVo) {


        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(0, serverVmListReq,
                mcConfigProperties.getServerVmList(),
                loginUserVo.getUserName(), 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String serverListTxt = JSON.toJSONString(mcResponse.getData());
            McPageResp<McServerVmPageDetailResp> mcPageResp = JSONObject.parseObject(serverListTxt, new
                    TypeReference<McPageResp<McServerVmPageDetailResp>>() {
                    });

            return mcPageResp;
        }
        return null;

    }


    @Override
    @Transactional
    public BaseResult<String> deleteServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        List<String> serverVmUuids = new ArrayList();
        serverVmUuids.add(serverVmBaseParam.getServerVmUuid());
        ServerVmBatchOperateParam serverVmBatchOperateParam = new ServerVmBatchOperateParam();
        serverVmBatchOperateParam.setServerVmUuids(serverVmUuids);

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        boolean deleteFlag = singleClusterBatchOperate(serverVmBatchOperateParam, loginUserVo,
                mcConfigProperties.getBatchRemoveMachineToRecycleUrl(), userMachineDo.getClusterId());
        //mc中成功将云服务器放入回收站后，将用户用户拥有的云服务器逻辑删除
        if (deleteFlag) {
            deleteUserMachine(loginUserVo, serverVmUuids);
            return BaseResult.success(null);
        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);
    }


    @Override
    public McStartVmErrorCode startServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        checkUserMachineIfOverdue(serverVmBaseParam.getServerVmUuid(), loginUserVo.getUserId());
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq,
                mcConfigProperties.getStartServerVmUrl(), loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            return McStartVmErrorCode.SUCCESS;
        }
        String error = JSON.parseObject(JSON.toJSONString(mcResponse.getData())).getString("errorCode");
        McStartVmErrorCode errorCode = McStartVmErrorCode.valueOf(error);
        return errorCode;

    }

    @Override
    public boolean shutdownServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getShutdownServerVmUrl());

    }

    @Override
    public boolean restartServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getRestartServerVmUrl());


    }

    @Override
    public boolean forcedShutdownServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getForcedShutdownServerVmUrl());
    }

    @Override
    public boolean forcedRestartServerVm(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        return commonSingleOperate(serverVmBaseParam, loginUserVo, mcConfigProperties.getForcedRestartServerVmUrl());

    }

    private boolean commonSingleOperate(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo, String httpUrl) {
        checkUserMachineIfOverdue(serverVmBaseParam.getServerVmUuid(), loginUserVo.getUserId());

        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());
        //调用mc获取响应
        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());
        return mcHttpService.noDataCommonMcRequest(userMachineDo.getClusterId(), serverVmBaseReq, httpUrl,
                loginUserVo.getUserName(),
                0);
    }

    /**
     * 检查云服务器是否过期
     */
    private void checkUserMachineIfOverdue(String serverVmUuid, Integer userId) {


        CloudUserMachineDo queryDo = cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmUuid, userId);
        if (Objects.isNull(queryDo)) {
            throw new KylinException(KylinHttpResponseConstants.OPERATE_ERR);
        }
        if (queryDo.getDeadlineFlag()) {
            throw new KylinException(KylinHttpResponseConstants.SERVERVM_OVERDUE_NOT_OPERATE);
        }
    }


    @Override
    public BaseResult<String> batchStartServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                 LoginUserVo loginUserVo) {
        return serverVmBatchOperateToMc(serverVmBatchOperateParam, loginUserVo,
                mcConfigProperties.getBatchStartServerVmUrl());
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
    @Transactional
    public BaseResult<String> batchDeleteServerVm(ServerVmBatchOperateParam serverVmBatchOperateParam,
                                                  LoginUserVo loginUserVo) {
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap = groupByClusterId(serverVmBatchOperateParam);
        if (Objects.equals(clusterUserMachineMap.size(), 1)) {
            CloudUserMachineDo userMachineDo =
                    cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBatchOperateParam.getServerVmUuids().get(0),
                            loginUserVo.getUserId());
            boolean deleteFlag = singleClusterBatchOperate(serverVmBatchOperateParam, loginUserVo,
                    mcConfigProperties.getBatchRemoveMachineToRecycleUrl(), userMachineDo.getClusterId());
            //mc中成功将云服务器放入回收站后，将用户用户拥有的云服务器逻辑删除
            if (deleteFlag) {
                deleteUserMachine(loginUserVo, serverVmBatchOperateParam.getServerVmUuids());
                return BaseResult.success(null);
            }
            return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);
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
                        .append(KylinHttpResponseConstants.BATCH_OPERATE_ERR).append(";");
            });
        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);
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
     * 云服务器操作，批量开机/批量关机/批量重启/
     */
    private BaseResult<String> serverVmBatchOperateToMc(ServerVmBatchOperateParam serverVmBatchOperateParam, LoginUserVo
            loginUserVo, String httpUrl) {


        //将用户云服务器安装集群进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap = groupByClusterId(serverVmBatchOperateParam);

        ServerVmBatchReq serverVmBatchReq = new ServerVmBatchReq();
        serverVmBatchReq.setUuid(String.join(",", serverVmBatchOperateParam.getServerVmUuids()));


        int clusterMachineMapSize = clusterUserMachineMap.size();
        //选择的云服务器是通一个集群中
        if (Objects.equals(clusterMachineMapSize, 1)) {

            CloudUserMachineDo userMachineDo =
                    cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBatchOperateParam.getServerVmUuids().get(0),
                            loginUserVo.getUserId());

            boolean singleClusterBatchOperate = singleClusterBatchOperate(serverVmBatchOperateParam, loginUserVo,
                    httpUrl
                    , userMachineDo.getClusterId());
            if (singleClusterBatchOperate) {
                return BaseResult.success(null);
            }
            return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);
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
                        .append(KylinHttpResponseConstants.BATCH_OPERATE_ERR).append(";");
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
    public McServerVmInfoResp serverVmInfo(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        McServerVmInfoResp mcServerVmInfoResp = new McServerVmInfoResp();


        CloudUserMachineDo queryDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        if (Objects.nonNull(queryDo)) {

            ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
            serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());


            //调用mc获取响应
            MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(queryDo.getClusterId(),
                    serverVmBaseReq,
                    mcConfigProperties.getServerVmInfoUrl(),
                    loginUserVo.getUserName(), 0);

            if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                String mcServerVmDetail = JSON.toJSONString(mcResponse.getData());
                mcServerVmInfoResp = JSON.parseObject(mcServerVmDetail, McServerVmInfoResp.class);
                if (Objects.nonNull(mcServerVmInfoResp)) {

                    if (StringUtils.isNotBlank(mcServerVmInfoResp.getMachineInfo().getLogo())) {
                        String logo = mcServerVmInfoResp.getMachineInfo().getLogo();
                        mcServerVmInfoResp.getMachineInfo().setLogo(logo);
                    }
                }
                if (Objects.nonNull(mcServerVmInfoResp) && queryDo.getDeadlineFlag()) {
                    mcServerVmInfoResp.getMachineInfo().setStatus(McServerVmStatus.OVERDUE);


                }
            }
        }

        return mcServerVmInfoResp;
    }

    @Override
    public ServerVmMonitorInfoRespDto serverVmMonitor(ServerVmBaseParam serverVmBaseParam, LoginUserVo
            loginUserVo) {
        List<McServerVmMonitorDetailResp> mcServerVmMonitorDetailList = new ArrayList<>();

        McServerVmMonitorReq serverVmMonitorReq = new McServerVmMonitorReq();
        serverVmMonitorReq.setUuid(serverVmBaseParam.getServerVmUuid());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmMonitorReq,
                mcConfigProperties.getServerVmMonitorInfoUrl(),
                loginUserVo.getUserName(), 0);

        ServerVmMonitorInfoRespDto serverVmMonitorInfoRespDto = new ServerVmMonitorInfoRespDto();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
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
        PageData<McServerVmLogResp> pageData = new PageData(null);


        ServerVmOperateLogReq serverVmOperateLogReq = new ServerVmOperateLogReq();
        serverVmOperateLogReq.setUuid(serverVmOperateLogPageParam.getServerVmUuid());
        serverVmOperateLogReq.setPage(serverVmOperateLogPageParam.getPageNo());
        serverVmOperateLogReq.setRows(serverVmOperateLogPageParam.getPageSize());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmOperateLogPageParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmOperateLogReq,
                mcConfigProperties.getServerVmOperateLogUrl(),
                loginUserVo.getUserName(), 0);

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
    public BaseResult<String> createSnapshot(McServerVmCreateSnapshotReq mcServerVmCreateSnapshotReq,
                                             LoginUserVo loginUserVo) {

        checkUserMachineIfOverdue(mcServerVmCreateSnapshotReq.getUuid(), loginUserVo.getUserId());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(mcServerVmCreateSnapshotReq.getUuid(),
                        loginUserVo.getUserId());

        String httpUrl = mcConfigProperties.getServerVmCreateSnapshotUrl();
        return commonSnapshot(userMachineDo.getClusterId(), mcServerVmCreateSnapshotReq, httpUrl,
                loginUserVo.getUserName());
    }

    private BaseResult<String> commonSnapshot(Integer clusterId, Object reqObj, String httpUrl, String userName) {
        MCResponseData<Object> mcResponseData = mcHttpService.hasDataCommonMcRequest(clusterId,
                reqObj, httpUrl, userName, 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponseData.getStatus())) {
            return BaseResult.success("success");
        }
        if (Objects.equals(MCServerVmConstants.ERROR, mcResponseData.getStatus()) && StringUtils.isNotBlank(mcResponseData.getMessage())) {
            return BaseResult.error(mcResponseData.getMessage());
        }
        return BaseResult.error(KylinHttpResponseConstants.OPERATE_ERR);
    }

    @Override
    public BaseResult<String> updateSnapshot(McServerVmUpdateSnapshotReq mcServerVmUpdateSnapshotReq,
                                             LoginUserVo loginUserVo) {
        checkUserMachineIfOverdue(mcServerVmUpdateSnapshotReq.getUuid(), loginUserVo.getUserId());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(mcServerVmUpdateSnapshotReq.getUuid(),
                        loginUserVo.getUserId());

        String httpUrl = mcConfigProperties.getServerVmUpdateSnapshotUrl();

        return commonSnapshot(userMachineDo.getClusterId(), mcServerVmUpdateSnapshotReq, httpUrl,
                loginUserVo.getUserName());
    }

    @Override
    public boolean deleteSnapshot(McServerVmDeleteSnapshotReq mcServerVmDeleteSnapshotReq, LoginUserVo loginUserVo) {
        checkUserMachineIfOverdue(mcServerVmDeleteSnapshotReq.getUuid(), loginUserVo.getUserId());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(mcServerVmDeleteSnapshotReq.getUuid(),
                        loginUserVo.getUserId());

        String httpUrl = mcConfigProperties.getServerVmDeleteSnapshotUrl();
        return mcHttpService.noDataCommonMcRequest(userMachineDo.getClusterId(), mcServerVmDeleteSnapshotReq, httpUrl,
                loginUserVo.getUserName(), 0);
    }

    @Override
    public BaseResult<String> applySnapshot(McServerVmApplySnapshotReq mcServerVmApplySnapshotReq,
                                            LoginUserVo loginUserVo) {
        checkUserMachineIfOverdue(mcServerVmApplySnapshotReq.getUuid(), loginUserVo.getUserId());
        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(mcServerVmApplySnapshotReq.getUuid(),
                        loginUserVo.getUserId());
        String httpUrl = mcConfigProperties.getServerVmApplySnapshotUrl();
        return commonSnapshot(userMachineDo.getClusterId(), mcServerVmApplySnapshotReq, httpUrl,
                loginUserVo.getUserName());
    }

    @Override
    public List<McServerVmSnapshotResp> listSnapshot(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        List<McServerVmSnapshotResp> snapshotList = new ArrayList<>();
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        String httpUrl = mcConfigProperties.getServerVmSnapshotListUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq, httpUrl, loginUserVo.getUserName(), 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String snapshotListTxt = JSONArray.toJSONString(mcResponse.getData());

            snapshotList = JSON.parseArray(snapshotListTxt, McServerVmSnapshotResp.class);
        }
        return snapshotList;
    }

    @Override
    public VncUrlDto getVncUrl(ServerVmBaseParam serverVmBaseParam, LoginUserVo loginUserVo) {
        VncUrlDto vncUrlDto = new VncUrlDto();
        ServerVmBaseReq serverVmBaseReq = new ServerVmBaseReq();
        serverVmBaseReq.setUuid(serverVmBaseParam.getServerVmUuid());

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(serverVmBaseParam.getServerVmUuid(),
                        loginUserVo.getUserId());

        String httpUrl = mcConfigProperties.getServerVmVncUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                serverVmBaseReq,
                httpUrl,
                loginUserVo.getUserName(), 0);

        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            vncUrlDto = JSON.parseObject(JSON.toJSONString(mcResponse.getData()), VncUrlDto.class);

        }
        return vncUrlDto;
    }

    @Override
    public boolean updateMachineDescription(UpdateServerVmDescriptionParam updateServerVmDescriptionParam,
                                            LoginUserVo loginUserVo) {

        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(updateServerVmDescriptionParam.getUuid(),
                        loginUserVo.getUserId());

        String httpUrl = mcConfigProperties.getUpdateMachineDescriptionUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(), updateServerVmDescriptionParam,
                        httpUrl, loginUserVo.getUserName(), 0);

        return Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus());

    }

    @Override
    public boolean updateMachineName(UpdateServerVmNameParam updateServerVmNameParam, LoginUserVo loginUserVo) {
        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(updateServerVmNameParam.getUuid(),
                        loginUserVo.getUserId());

        checkServerNameExist(updateServerVmNameParam.getAliasName(), loginUserVo, userMachineDo);

        String httpUrl = mcConfigProperties.getUpdateMachineNameUrl();
        //调用mc获取响应
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(), updateServerVmNameParam,
                        httpUrl, loginUserVo.getUserName(), 0);

        return Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus());
    }

    /**
     * 检查云服务器名称是否重复
     *
     * @param machineName
     * @param loginUserVo
     */
    private void checkServerNameExist(String machineName, LoginUserVo loginUserVo, CloudUserMachineDo userMachineDo) {
        CheckServerNameParamReq checkServerNameParamReq = new CheckServerNameParamReq();
        checkServerNameParamReq.setServervmName(machineName);
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(), checkServerNameParamReq,
                        mcConfigProperties.getCheckVmServerNameUrl(),
                        loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String existStr = JSONArray.toJSONString(mcResponse.getData());
            CheckServerNameResp checkServerNameResp = JSON.parseObject(existStr, CheckServerNameResp.class);
            if (Objects.nonNull(checkServerNameResp) && checkServerNameResp.getExist()) {
                throw new KylinException(KylinHttpResponseConstants.SERVER_VM_NAME_EXIST);
            }
        }

    }

    @Override
    public boolean resetRemotePassword(McServerVmResetRemotePasswordReq remotePasswordReq, LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getResetRemotePasswordUrl();
        //调用mc获取响应
        CloudUserMachineDo userMachineDo =
                cloudUserMachineService.getUserMachineDoByUuidAndUserId(remotePasswordReq.getUuid(),
                        loginUserVo.getUserId());

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(userMachineDo.getClusterId(),
                remotePasswordReq,
                httpUrl,
                loginUserVo.getUserName(), 0);

        return Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus());
    }

    /**
     * 根据云服务器ID(UUID)获取云服务器(模板详情)
     */
    public McTemplateDetailResp getTemplateDetail(Integer clusterId,
                                                  QueryMcServerDetailParamReq queryMcServerDetailParamReq,
                                                  LoginUserVo loginUserVo) {
        String httpUrl = mcConfigProperties.getGetServervmDetailByServevmIdUrl();
        return getMcServerVmDetail(clusterId, queryMcServerDetailParamReq, loginUserVo, httpUrl);
    }


    private McTemplateDetailResp getMcServerVmDetail(Integer clusterId,
                                                     QueryMcServerDetailParamReq queryMcServerDetailParamReq,
                                                     LoginUserVo loginUserVo, String httpUrl) {


        //调用mc获取响应
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(clusterId, queryMcServerDetailParamReq, httpUrl,
                        loginUserVo.getUserName(), 0);
        McTemplateDetailResp mcTemplateDetailResp = new McTemplateDetailResp();
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String mcServerVmDetailStr = JSON.toJSONString(mcResponse.getData());
            mcTemplateDetailResp = JSON.parseObject(mcServerVmDetailStr, McTemplateDetailResp.class);
        }
        return mcTemplateDetailResp;
    }

    @Override
    public ResponseEntity<byte[]> downLoadServerVmLog(String mcServerVmLogoPath, String mcServerVmLogoName) {

        try {
            String localFilePath = mcConfigProperties.getMcLogoLocalFilePath() + mcServerVmLogoName;
            File file = new File(localFilePath);
            if (!file.exists()) {
                String encodeName = URLEncoder.encode(mcServerVmLogoName, "UTF-8");
                String mcDownLoadUrl =
                        mcNodeService.getMcLeaderUrlFromCache(0) + mcConfigProperties.getMcPrefix() +
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


    @Override
    public List<McOperateSystemResp> getAllOperatingSystem(QueryOperateSystem queryOperateSystem,
                                                           LoginUserVo loginUserVo) {
        List<McOperateSystemResp> systemList = new ArrayList<>();
        //调用mc获取响应
        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(queryOperateSystem.getClusterId(), queryOperateSystem,
                        mcConfigProperties.getAllOperatingSystemUrl(), loginUserVo.getUserName(), 0);
        if (Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String systemListStr = JSON.toJSONString(mcResponse.getData());
            systemList = JSON.parseArray(systemListStr, McOperateSystemResp.class);
        }
        return systemList;
    }
}
