package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_user")
public class CloudWorkOrderUserDo extends BaseDo {


    private Integer workOrderId;


    private String oldRealName;

    private String newRealName;


}
