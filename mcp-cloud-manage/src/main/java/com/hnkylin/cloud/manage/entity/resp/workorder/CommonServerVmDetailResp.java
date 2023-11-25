package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.McServerClusterType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkConfigRespDto;
import lombok.Data;

import java.util.List;

@Data
public class CommonServerVmDetailResp {
    //申请云服务器名称
    private String aliasName;

    //申请时-cpu
    private Integer cpu;

    //申请时-内存
    private Integer mem;
    //申请时内存单位
    private MemUnit memUnit;


    //模板-操作系统
    private String osMachine;

    //模板-架构
    private String architecture;

    private String systemType;

    //主机模式类型
    private McServerClusterType serverClusterType = McServerClusterType.CUSTOM;

    //模板选中的计算资源
    private String selectCluster;

    //计算资源uudi
    private String selectClusterUuid;

    //主机资源模式id
    private String selectTagIds;

    //主机资源模式名称
    private String selectTagNames;

    //模板选中的计存储位置
    private Integer storageLocationId;


    //磁盘列表
    private List<McServerVmDiskDetailResp> disks;

    //网卡信息
    private List<McServerVmNetworkDetailResp> interfaceList;

    //模板对应可选择的计算资源节点
    private List<McClusterResp> clusterList;

    //mc中可选的存储位置
    private List<McStorageLocationResp> storageLocationList;

    //云管中自定义的网络信息
    private List<NetworkConfigRespDto> networkConfigList;


    //绑定的资源列表
    private List<McClusterBindResource> clusterBindResourceList;
}
