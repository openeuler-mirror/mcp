package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseServerVmConstants {


    String OPERATE_SUCCESS = "操作成功";

    String OPERATE_ERR = "操作失败";

    String START_VM_ERR = "开机失败，请联系管理员";

    String SHUTDOWN_VM_ERR = "关机失败，请联系管理员";

    String FORCED_SHUTDOWN_VM_ERR = "强制关机失败，请联系管理员";

    String RESTART_VM_ERR = "重启失败，请联系管理员";

    String FORCED_RESTART_VM_ERR = "强制重启失败，请联系管理员";

    String SERVERVM_OVERDUE_NOT_OPERATE = "云服务器已过期不能操作";

    String BATCH_OPERATE_ERR = "批量操作失败";


}
