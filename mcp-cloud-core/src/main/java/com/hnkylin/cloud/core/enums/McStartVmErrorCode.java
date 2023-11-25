package com.hnkylin.cloud.core.enums;

public enum McStartVmErrorCode {
    SUCCESS(0, "开机成功"),
    OTHER_REASON(1, "开机失败,请联系管理员"),
    TICKET_EXPIRED(67, "票据过期,请联系管理员"),
    RUNTIME_SERVER_ERR(70, "runtime-server 错误,请联系管理员"),
    ERR_SYSTEM(85, "系统内部错误,请联系管理员"),
    NO_MATCHING_SERVER(104, "桌面未被任何服务器缓存,请联系管理员"),
    LAUNCH_SUSPENDED(105, "暂停启动新会话,请联系管理员"),
    AT_LICENSE_CAPACITY(106, "群集已达到许可证限制,请联系管理员"),
    LICENSE_EXPIRED(122, "许可过期,请联系管理员"),
    LICENSE_INVALID(123, "许可不可用,请联系管理员"),
    LICENSE_MISSING(124, "许可不存在,请联系管理员"),
    RESOURCE_REQUIREMENTS_GPU(205, "无法满足gpu的资源需求,请联系管理员"),
    RESOURCE_REQUIREMENTS_ARCH(206, "无法满足arch的资源需求,请联系管理员"),
    RESOURCE_REQUIREMENTS_OTHER(207, "无法满足组织、客户或网络的资源需求,请联系管理员"),
    ALL_SERVER_NOREADY(208, "服务器均未就绪,请联系管理员"),
    ALL_SERVER_OFFLINE(209, "服务器脱机,请联系管理员"),
    AT_RESOURCE_CAPACITY_MEM(210, "集群的内存资源容量不足,请联系管理员"),
    AT_RESOURCE_CAPACITY_SESS(211, "群集的会话资源容量不足,请联系管理员"),
    NO_STORAGE_LOCATION(218, "服务器没有存储,请联系管理员"),
    LICENSE_NO_GPU_CAPABILITY(219, "GPU桌面需要Pro许可证,请联系管理员"),
    LICENSE_CONCURRENT_USERS_EXCEEDED(220, "并发用户数超过许可限制，请释放部分用户或更新许可证！请联系管理员"),
    LICENSE_SV_CPUS_EXCEEDED(221, "服务器虚拟化CPU数量超过许可限制，请更新或升级许可证！,请联系管理员"),
    AFFINEGROUP_NO_SERVER(226, "亲和组策略限制,请联系管理员");


    private Integer errorCode;

    private String errorMsg;

    McStartVmErrorCode(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
