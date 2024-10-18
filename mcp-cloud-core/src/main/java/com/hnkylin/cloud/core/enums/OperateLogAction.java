package com.hnkylin.cloud.core.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum OperateLogAction {
    IMAGE_UPLOAD("上传镜像", OperateLogType.IMAGE),
    IMAGE_SYNC("同步镜像文件", OperateLogType.IMAGE),
    IMAGE_CREATE_TEMPLATE("生成模板镜像", OperateLogType.IMAGE),
    IMAGE_MODIFY("编辑镜像", OperateLogType.IMAGE),
    IMAGE_PRIVATE_TO_SHARE("私有镜像提升为共享镜像", OperateLogType.IMAGE),
    IMAGE_SHARE_TO_PUBLIC("共享镜像提升为公共镜像", OperateLogType.IMAGE),
    IMAGE_MAKE("制作镜像", OperateLogType.IMAGE),
    IMAGE_CREATE_GVM_FILE("生成镜像文件", OperateLogType.IMAGE),
    IMAGE_ERROR_SYNC("异常镜像-同步", OperateLogType.IMAGE),
    IMAGE_DELETE("删除镜像", OperateLogType.IMAGE),
    IMAGE_DOWNLOAD("下载镜像", OperateLogType.IMAGE),
    USER_LOGIN("登录", OperateLogType.USER),
    USER_CREATE("创建用户", OperateLogType.USER),
    USER_MODIFY("编辑用户", OperateLogType.USER),
    USER_DELETE("删除用户", OperateLogType.USER),
    ROLE_CREATE("创建角色", OperateLogType.ROLE),
    ROLE_DELETE("删除角色", OperateLogType.ROLE),
    ROLE_MODIFY("编辑角色", OperateLogType.ROLE),
    ORG_CREATE("创建组织", OperateLogType.ORG),
    ORG_DELETE("删除组织", OperateLogType.ORG),
    ORG_MODIFY("编辑组织", OperateLogType.ORG),
    ZONE_CREATE("创建可用区", OperateLogType.ZONE),
    ZONE_MODIFY("编辑可用区", OperateLogType.ZONE),
    ZONE_DELETE("删除可用区", OperateLogType.ZONE),
    CLUSTER_CREATE("创建物理集群", OperateLogType.CLUSTER),
    CLUSTER_MODIFY("编辑物理集群", OperateLogType.CLUSTER),
    CLUSTER_DELETE("删除物理集群", OperateLogType.CLUSTER),
    VDC_CREATE("创建VDC", OperateLogType.VDC),
    VDC_MODIFY("编辑VDC", OperateLogType.VDC),
    VDC_DELETE("删除VDC", OperateLogType.VDC),
    VDC_MODIFY_RESOURCE("变更VDC资源", OperateLogType.VDC),
    VDC_RESOURCE_APPLY("VDC资源申请", OperateLogType.VDC),
    ALARM_VDC_CPU_MODIFY("VDC-CPU分配比告警", OperateLogType.ALARM),
    ALARM_VDC_MEM_MODIFY("VDC-内存使用率告警", OperateLogType.ALARM),
    ALARM_VDC_STORAGE_MODIFY("VDC-存储使用率告警", OperateLogType.ALARM),
    SERVERVM_START("开机", OperateLogType.SERVERVM),
    SERVERVM_SHUTDOWN("关机", OperateLogType.SERVERVM),
    SERVERVM_FORCESHUTDOWN("强制关机", OperateLogType.SERVERVM),
    SERVERVM_RESTART("重启", OperateLogType.SERVERVM),
    SERVERVM_FORCERESTART("强制重启", OperateLogType.SERVERVM),
    SERVERVM_DELETE("删除", OperateLogType.SERVERVM),
    SERVERVM_CREATESNAPSHOT("创建快照", OperateLogType.SERVERVM),
    SERVERVM_APPLYSNAPSHOT("恢复快照", OperateLogType.SERVERVM),
    SERVERVM_DELETESNAPSHOT("删除快照", OperateLogType.SERVERVM),
    KCP_HA_ADD_SLAVE("添加备KCP", OperateLogType.KCP_HA),
    KCP_HA_DELETE_SLAVE("删除备KCP", OperateLogType.KCP_HA),
;


    OperateLogAction(String desc, OperateLogType operateLogType) {
        this.desc = desc;
        this.operateLogType = operateLogType;
    }

    private String desc;


    private OperateLogType operateLogType;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public OperateLogType getOperateLogType() {
        return operateLogType;
    }

    public void setOperateLogType(OperateLogType operateLogType) {
        this.operateLogType = operateLogType;
    }

    /**
     * 根据任务类型获取任务操作列表
     *
     * @param operateLogType
     * @return
     */
    public static List<OperateLogAction> getActionListByType(OperateLogType operateLogType) {
        List<OperateLogAction> actionList = new ArrayList<>();
        for (OperateLogAction action : OperateLogAction.values()) {
            if (Objects.equals(action.getOperateLogType(), operateLogType)) {
                actionList.add(action);
            }
        }
        return actionList;
    }


}
