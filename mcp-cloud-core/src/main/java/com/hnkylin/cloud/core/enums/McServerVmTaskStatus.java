package com.hnkylin.cloud.core.enums;

/**
 * Created by kylin-ksvd on 21-10-08.
 */
public enum McServerVmTaskStatus {

    NEW("新建"),
    INSTALLING("安装中"),
    NEWFAILED("新建失败"),
    INSTALLFAILED("安装失败"),
    CREATING("正在创建"),
    STARTING("正在开机"),
    SHUTDOWNING("正在关机"),
    REBOOTING("正在重启"),
    SUSPENDING("正在暂停"),
    RESUMING("正在唤醒"),

    VOLUME_MIGRATING("正在存储迁移"),
    HOST_MIGRATING("正在主机迁移"),
    BACKUP_RUNNING("正在备份"),
    BACKUP_RECOVERING("正在备份恢复"),
    SNAPSHOT_RUNNING("正在快照"),
    SNAPSHOT_RECOVERING("正在快照恢复"),
    IMPORTING("正在导入"),
    EXPORTING("正在导出"),
    CLONING("正在克隆"),
    DISK_COPYING("正在磁盘复制"),
    LEISURE("空闲状态");

    McServerVmTaskStatus(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
