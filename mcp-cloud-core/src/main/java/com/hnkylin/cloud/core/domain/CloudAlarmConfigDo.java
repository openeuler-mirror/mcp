package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.AlarmResourceType;
import com.hnkylin.cloud.core.enums.ArchitectureResourceType;
import com.hnkylin.cloud.core.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_alarm_config")
public class CloudAlarmConfigDo extends BaseDo {


    private AlarmResourceType resourceType;

    private Integer generalAlarm;

    private Integer severityAlarm;

    private Integer urgentAlarm;

    private Integer durationTime;

}
