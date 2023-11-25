package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_user_machine")
public class CloudUserMachineDo extends BaseDo {


    private Integer userId;


    private String machineUuid;

    private Date deadlineTime;

    //是否过期
    private Boolean deadlineFlag;

    private ServerVmDeadlineType deadlineType;

    private Integer clusterId;


}
