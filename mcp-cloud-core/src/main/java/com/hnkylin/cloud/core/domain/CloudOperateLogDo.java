package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_operate_log")
public class CloudOperateLogDo extends BaseDo {


    private Integer parentId;

    //类型
    private String type;

    //动作
    private String action;

    //状态
    private String status;

    //执行进度
    private String percent;

    //对象id
    private Integer objId;

    //对象名称
    private String objName;

    //操作详情
    private String detail;

    //操作结果
    private String result;

    //结束时间
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date endTime;

    private String clientIp;

    private Long mcTaskId;

    private Integer clusterId;

    private String mcTaskDetailInfo;
    private String vcenterTaskKey;

    private String vcenterObjName;


}
