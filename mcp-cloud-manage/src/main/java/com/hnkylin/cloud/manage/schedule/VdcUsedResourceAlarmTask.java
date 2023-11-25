package com.hnkylin.cloud.manage.schedule;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.domain.CloudAlarmConfigDo;
import com.hnkylin.cloud.core.domain.CloudAlarmLogDo;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.core.enums.AlarmLevel;
import com.hnkylin.cloud.core.enums.AlarmResourceType;
import com.hnkylin.cloud.core.enums.AlarmTargetType;
import com.hnkylin.cloud.core.service.CloudAlarmConfigService;
import com.hnkylin.cloud.core.service.CloudAlarmLogService;
import com.hnkylin.cloud.core.service.CloudUserService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcUsedResourceDto;
import com.hnkylin.cloud.manage.service.VdcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * VDC-已使用资源告警
 */
@Configuration
@EnableScheduling
@Slf4j
public class VdcUsedResourceAlarmTask {

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private CloudAlarmConfigService cloudAlarmConfigService;

    @Resource
    private VdcService vdcService;

    private static final int ZERO = 0;

    @Resource
    private CloudUserService cloudUserService;

    private static final String EXCEED = "超过";

    private static final String PERCENT = "%";

    @Resource
    private CloudAlarmLogService cloudAlarmLogService;


    /**
     * 每5分钟执行一次 校验VDC资源使用率
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    private void runServerVmExpireTask() {
        log.info("VdcUsedResourceAlarmTask-start");


        //查询告警设置
        CloudAlarmConfigDo cloudAlarmConfigDo = new CloudAlarmConfigDo();
        cloudAlarmConfigDo.setDeleteFlag(false);
        QueryWrapper alarmSettingWrapper = new QueryWrapper(cloudAlarmConfigDo);
        List<CloudAlarmConfigDo> alarmConfigDoList = cloudAlarmConfigService.list(alarmSettingWrapper);


        //查询VDC列表
        CloudVdcDo cloudVdcDo = new CloudVdcDo();
        cloudVdcDo.setDeleteFlag(false);
        QueryWrapper vdcQueryWrapper = new QueryWrapper(cloudVdcDo);
        List<CloudVdcDo> vdcDoList = cloudVdcService.list(vdcQueryWrapper);
        List<CloudAlarmLogDo> alarmLogDoList = new ArrayList<>();
        vdcDoList.forEach(vdcDo -> {
            alarmLogDoList.addAll(verifyVdcAlarmSetting(vdcDo, alarmConfigDoList));

        });

        if (!alarmLogDoList.isEmpty()) {
            cloudAlarmLogService.saveBatch(alarmLogDoList);
        }
        log.info("VdcUsedResourceAlarmTask-alarmLogDoList-size:" + alarmLogDoList.size());
    }

    /**
     * 校验VDC资源设置
     *
     * @param vdcDo
     * @param alarmConfigDoList
     */
    private List<CloudAlarmLogDo> verifyVdcAlarmSetting(CloudVdcDo vdcDo, List<CloudAlarmConfigDo> alarmConfigDoList) {

        List<CloudAlarmLogDo> vdcAlarmLogDoList = new ArrayList<>();


        //Cpu 告警设置
        CloudAlarmConfigDo vdcCpuAlarm =
                alarmConfigDoList.stream().filter(item -> Objects.equals(item.getResourceType(),
                        AlarmResourceType.VDC_CPU))
                        .findFirst().orElse(null);
        //内存告警设置
        CloudAlarmConfigDo vdcMemAlarm =
                alarmConfigDoList.stream().filter(item -> Objects.equals(item.getResourceType(),
                        AlarmResourceType.VDC_MEM))
                        .findFirst().orElse(null);
        //存储告警设置
        CloudAlarmConfigDo vdcStorageAlarm =
                alarmConfigDoList.stream().filter(item -> Objects.equals(item.getResourceType(),
                        AlarmResourceType.VDC_STORAGE))
                        .findFirst().orElse(null);

        LoginUserVo sysAdminUser = getSysAdminUser();

        //获取VDC资源情况
        VdcUsedResourceDto vdcResourceDto = vdcService.getVdcResourceInfo(vdcDo.getId(), sysAdminUser);

        vdcAlarmLogDoList.addAll(verifyExceedAlarmSetting(vdcDo, vdcCpuAlarm, AlarmResourceType.VDC_CPU, sysAdminUser
                , vdcResourceDto));
        vdcAlarmLogDoList.addAll(verifyExceedAlarmSetting(vdcDo, vdcMemAlarm, AlarmResourceType.VDC_MEM, sysAdminUser
                , vdcResourceDto));
        vdcAlarmLogDoList.addAll(verifyExceedAlarmSetting(vdcDo, vdcStorageAlarm, AlarmResourceType.VDC_STORAGE,
                sysAdminUser, vdcResourceDto));
        return vdcAlarmLogDoList;

    }

    /**
     * 校验各个资源是否超出阈值设置
     *
     * @param vdcDo
     * @param alarmConfigDo
     * @param alarmResourceType
     * @param sysAdminUser
     * @param vdcResourceDto
     * @return
     */
    private List<CloudAlarmLogDo> verifyExceedAlarmSetting(CloudVdcDo vdcDo, CloudAlarmConfigDo alarmConfigDo,
                                                           AlarmResourceType alarmResourceType,
                                                           LoginUserVo sysAdminUser,
                                                           VdcUsedResourceDto vdcResourceDto) {

        List<CloudAlarmLogDo> alarmLogDoList = new ArrayList<>();

        Integer generalAlarm = alarmConfigDo.getGeneralAlarm();
        Integer severityAlarm = alarmConfigDo.getSeverityAlarm();
        Integer urgentAlarm = alarmConfigDo.getUrgentAlarm();
        if (Objects.equals(generalAlarm, ZERO) && Objects.equals(severityAlarm, ZERO) && Objects.equals(urgentAlarm,
                ZERO)) {
            return alarmLogDoList;
        }

        int total = 0;
        int used = 0;
        switch (alarmResourceType) {
            case VDC_CPU:
                total = vdcResourceDto.getTotalCpu();
                used = vdcResourceDto.getUsedCpu();
                break;
            case VDC_MEM:
                total = vdcResourceDto.getTotalMem();
                used = vdcResourceDto.getUsedMem();
                break;
            case VDC_STORAGE:
                total = vdcResourceDto.getTotalStorage();
                used = vdcResourceDto.getUsedMem();
                break;
            default:
        }
        //使用率
        if (total > 0) {
            BigDecimal totalResource = new BigDecimal(total + "");
            BigDecimal usedResource = new BigDecimal(used + "");
            BigDecimal usageRate =
                    usedResource.divide(totalResource, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100 + ""));

            //判断顺序，紧急告警->严重告警->一般告警
            if (!Objects.equals(urgentAlarm, ZERO) && Objects.nonNull(urgentAlarm)) {
                //紧急告警
                BigDecimal urgentAlarmBig = new BigDecimal(urgentAlarm + "");
                if (usageRate.compareTo(urgentAlarmBig) >= 0) {
                    alarmLogDoList.add(createAlarmLog(vdcDo, sysAdminUser, AlarmLevel.URGENT, alarmResourceType,
                            urgentAlarm));
                    return alarmLogDoList;
                }

            }
            if (!Objects.equals(severityAlarm, ZERO) && Objects.nonNull(severityAlarm)) {
                //严重告警
                BigDecimal severityAlarmBig = new BigDecimal(severityAlarm + "");
                if (usageRate.compareTo(severityAlarmBig) >= 0) {
                    alarmLogDoList.add(createAlarmLog(vdcDo, sysAdminUser, AlarmLevel.SEVERITY, alarmResourceType,
                            severityAlarm));
                    return alarmLogDoList;
                }
            }
            if (!Objects.equals(generalAlarm, ZERO) && Objects.nonNull(generalAlarm)) {
                //一般告警
                BigDecimal generalAlarmBig = new BigDecimal(generalAlarm + "");
                if (usageRate.compareTo(generalAlarmBig) >= 0) {
                    alarmLogDoList.add(createAlarmLog(vdcDo, sysAdminUser, AlarmLevel.GENERAL, alarmResourceType,
                            generalAlarm));
                }
            }


        }
        return alarmLogDoList;
    }


    /**
     * 创建告警对象
     *
     * @param vdcDo
     * @param loginUserVo
     * @param alarmLevel
     * @param alarmResourceType
     * @param alarmValue
     * @return
     */
    private CloudAlarmLogDo createAlarmLog(CloudVdcDo vdcDo, LoginUserVo loginUserVo, AlarmLevel alarmLevel,
                                           AlarmResourceType alarmResourceType, Integer alarmValue) {
        CloudAlarmLogDo cloudAlarmLogDo = new CloudAlarmLogDo();
        cloudAlarmLogDo.setAlarmLevel(alarmLevel);
        cloudAlarmLogDo.setAlarmTargetId(vdcDo.getId());
        cloudAlarmLogDo.setAlarmTarget(vdcDo.getVdcName());
        cloudAlarmLogDo.setResourceType(alarmResourceType);
        cloudAlarmLogDo.setTargetType(AlarmTargetType.PLATFORM);
        cloudAlarmLogDo.setCreateBy(loginUserVo.getUserId());
        StringBuilder targetDetail = new StringBuilder(vdcDo.getVdcName());
        targetDetail.append("--").append(alarmResourceType.getDesc()).append(EXCEED).append(alarmValue).append(PERCENT);
        cloudAlarmLogDo.setAlarmDetail(targetDetail.toString());
        cloudAlarmLogDo.setCreateTime(new Date());
        return cloudAlarmLogDo;
    }

    /**
     * 获取系统管理员
     *
     * @return
     */
    private LoginUserVo getSysAdminUser() {
        CloudUserDo cloudUserDo = new CloudUserDo();
        cloudUserDo.setUserName(KylinCloudManageConstants.SYSADMINUSER);
        cloudUserDo.setOrganizationId(KylinCloudManageConstants.TOP_ORG_ID);
        QueryWrapper queryWrapper = new QueryWrapper(cloudUserDo);
        CloudUserDo sysAdmin = cloudUserService.getOne(queryWrapper);
        LoginUserVo sysAdminUser = new LoginUserVo();
        sysAdminUser.setUserId(sysAdmin.getId());
        sysAdminUser.setUserName(sysAdmin.getUserName());
        return sysAdminUser;
    }


}
