package com.hnkylin.cloud.manage.constant;

/**
 * Created by kylin-ksvd on 2023-01-04.
 */
public interface KylinHttpResponseHAConstants {

    String EXIST_SLAVE = "已经存在备kcp，不能加入多个备kcp";

    String ADD_SLAVE_ERROR = "添加备KCP失败，请联系技术支持人员";

    String SLAVE_PASSWORD_ERROR = "备KCP密码错误，请重新输入";

    String CHANGE_MASTER_ERROR = "备kcp升级为主kcp失败，请联系技术支持人员";

    String MASTER_ONLINE_NOT_CHANGE = "主kcp在线，不能将备KCP升级为主KCP";

    String NOT_DELETE_SLAVE = "当前状态不能删除备kcp";


}
