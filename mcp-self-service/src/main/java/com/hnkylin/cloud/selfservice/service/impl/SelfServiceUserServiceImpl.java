package com.hnkylin.cloud.selfservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.JwtUtil;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudWorkOrderDo;
import com.hnkylin.cloud.core.domain.CloudWorkOrderUserDo;
import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.UserType;
import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import com.hnkylin.cloud.core.enums.WorkOrderType;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.selfservice.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.selfservice.constant.KylinSelfConstants;
import com.hnkylin.cloud.selfservice.constant.SelfServiceRedisConstant;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.LoginUserParam;
import com.hnkylin.cloud.selfservice.entity.req.UpdateRealNameParam;
import com.hnkylin.cloud.selfservice.entity.req.UpdatePwdParam;
import com.hnkylin.cloud.selfservice.entity.req.UserRegisterParam;
import com.hnkylin.cloud.selfservice.entity.resp.TokenRespDto;
import com.hnkylin.cloud.selfservice.entity.resp.UserInfoRespDto;
import com.hnkylin.cloud.selfservice.entity.resp.WorkOrderUserDetailRespDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceUserService;
import com.hnkylin.cloud.selfservice.service.SelfWorkOrderService;
import com.hnkylin.cloud.selfservice.service.cache.LoginUserCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SelfServiceUserServiceImpl implements SelfServiceUserService {

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private LoginUserCacheService loginUserCacheService;

    @Resource
    private CloudDeptService cloudDeptService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;

    @Resource
    private CloudWorkOrderService workOrderService;

    @Resource
    private CloudWorkOrderUserService workOrderUserService;

    @Resource
    private SelfWorkOrderService selfWorkOrderService;

    @Override
    @Transactional
    public void register(UserRegisterParam userRegisterParam) {

        //判断用户名是否已经存在。
        CloudUserDo cloudUserDo = cloudUserService.queryUserByUserName(userRegisterParam.getUserName());
        if (Objects.nonNull(cloudUserDo)) {
            throw new KylinException(KylinHttpResponseConstants.ALREADY_EXIST_USER);
        }

        Date now = new Date();
        CloudUserDo registerCloudUserDo = CloudUserDo.builder().userName(userRegisterParam.getUserName())
                .realName(userRegisterParam.getRealName()).password(userRegisterParam.getPassword()).remark
                        (userRegisterParam.getApplyReason()).userType(UserType.selfServiceUser).superUser(false).defaultUser(false)
                .status(CloudUserStatus.NO_ACTIVATE).organizationId(userRegisterParam.getOrganizationId()).mobile
                        (userRegisterParam.getMobile())
                .build();
        registerCloudUserDo.setCreateTime(now);
        registerCloudUserDo.setDeleteFlag(Boolean.FALSE);
        cloudUserService.save(registerCloudUserDo);
        //插入申请工单
        CloudWorkOrderDo cloudWorkOrderDo = new CloudWorkOrderDo();
        cloudWorkOrderDo.setUserId(registerCloudUserDo.getId());
        cloudWorkOrderDo.setTarget(registerCloudUserDo.getUserName());
        cloudWorkOrderDo.setStatus(WorkOrderStatus.WAIT_CHECK);
        cloudWorkOrderDo.setType(WorkOrderType.REGISTER_USER);
        cloudWorkOrderDo.setCreateTime(now);
        cloudWorkOrderDo.setApplyReason(userRegisterParam.getApplyReason());
        cloudWorkOrderDo.setCreateBy(registerCloudUserDo.getId());
        workOrderService.save(cloudWorkOrderDo);
    }

    @Override
    public TokenRespDto login(LoginUserParam loginUserParam) {
        CloudUserDo cloudUserDo = cloudUserService.queryUserByUserName(loginUserParam.getUserName());
        if (Objects.isNull(cloudUserDo)) {
            throw new KylinException(KylinHttpResponseConstants.NOT_EXIST_USER);
        }
        if (!Objects.equals(loginUserParam.getPassword(), cloudUserDo.getPassword())) {
            throw new KylinException(KylinHttpResponseConstants.PASSWORD_ERR);
        }
        if (Objects.equals(cloudUserDo.getStatus(), CloudUserStatus.NO_ACTIVATE)) {
            throw new KylinException(KylinHttpResponseConstants.USER_NOT_ACTIVATE);
        }
        if (!Objects.equals(cloudUserDo.getUserType(), UserType.selfServiceUser)) {
            throw new KylinException(KylinHttpResponseConstants.NOT_LOING_PERMISSION);
        }
        //登录成功，生成token
        String token =
                JwtUtil.sign(cloudUserDo.getUserName(), cloudUserDo.getId().toString(), KylinSelfConstants
                        .KYLIN_TOKEN_SECRET);

        //同一用户互斥登录
        Object cacheToken = loginUserCacheService.vGet(SelfServiceRedisConstant.UID + cloudUserDo.getId());
        if (Objects.nonNull(cacheToken)) {
            loginUserCacheService.vDelete(SelfServiceRedisConstant.UID + cloudUserDo.getId());
            loginUserCacheService.vDelete(cacheToken.toString());
        }

        //设置token缓存有效时间
        loginUserCacheService.vSet(token, cloudUserDo.getId(), SelfServiceRedisConstant
                        .SELF_SERVICE_LOGIN_USER_CACHE_EXPIRE,
                TimeUnit.MILLISECONDS);
        loginUserCacheService.vSet(SelfServiceRedisConstant.UID + cloudUserDo.getId(), token,
                SelfServiceRedisConstant.SELF_SERVICE_LOGIN_USER_CACHE_EXPIRE, TimeUnit.MILLISECONDS);


        TokenRespDto tokenRespDto = new TokenRespDto();
        tokenRespDto.setToken(token);
        return tokenRespDto;
    }

    @Override
    public void updatePwd(UpdatePwdParam updatePwdParam, LoginUserVo loginUserVo) {
        CloudUserDo cloudUserDo = cloudUserService.getById(loginUserVo.getUserId());
        if (Objects.isNull(cloudUserDo)) {
            throw new KylinException(KylinHttpResponseConstants.NOT_EXIST_USER);
        }
        if (!Objects.equals(cloudUserDo.getPassword(), updatePwdParam.getOldPassword())) {
            throw new KylinException(KylinHttpResponseConstants.OLD_PASSWORD_ERR);
        }
        cloudUserDo.setPassword(updatePwdParam.getPassword());
        cloudUserDo.setUpdateBy(loginUserVo.getUserId());
        cloudUserDo.setUpdateTime(new Date());
        cloudUserService.updateById(cloudUserDo);
    }

    @Override
    public UserInfoRespDto userInfo(LoginUserVo loginUserVo) {
        UserInfoRespDto userInfoRespDto = new UserInfoRespDto();
        CloudUserDo cloudUserDo = cloudUserService.getById(loginUserVo.getUserId());
        userInfoRespDto.setUserName(cloudUserDo.getUserName());
        userInfoRespDto.setRealName(cloudUserDo.getRealName());
        userInfoRespDto.setMobile(cloudUserDo.getMobile());
        userInfoRespDto.setRemark(cloudUserDo.getRemark());
//        userInfoRespDto.setOrganizationName(cloudOrganizationService.getById(cloudUserDo.getOrganizationId())
//                .getOrganizationName());
        return userInfoRespDto;
    }

    @Override
    public void loginOut(LoginUserVo loginUserVo) {
        Object cacheToken = loginUserCacheService.vGet(SelfServiceRedisConstant.UID + loginUserVo.getUserId());
        if (Objects.nonNull(cacheToken)) {
            loginUserCacheService.vDelete(SelfServiceRedisConstant.UID + loginUserVo.getUserId());
            loginUserCacheService.vDelete(cacheToken.toString());
        }
    }

    @Override
    @Transactional
    public void updateRealName(UpdateRealNameParam updateRealNameParam, LoginUserVo loginUserVo) {
        CloudUserDo cloudUserDo = cloudUserService.getById(loginUserVo.getUserId());
        if (Objects.isNull(cloudUserDo)) {
            throw new KylinException(KylinHttpResponseConstants.NOT_EXIST_USER);
        }
        Date now = new Date();
        String oldRealName = cloudUserDo.getRealName();
        cloudUserDo.setRealName(updateRealNameParam.getRealName());
        cloudUserDo.setUpdateBy(loginUserVo.getUserId());
        cloudUserDo.setUpdateTime(now);
        //插入申请工单
        CloudWorkOrderDo cloudWorkOrderDo = new CloudWorkOrderDo();
        cloudWorkOrderDo.setUserId(cloudUserDo.getId());
        cloudWorkOrderDo.setTarget(cloudUserDo.getUserName());
        cloudWorkOrderDo.setStatus(WorkOrderStatus.WAIT_CHECK);
        cloudWorkOrderDo.setType(WorkOrderType.MODIFY_USER);
        cloudWorkOrderDo.setCreateTime(now);
        cloudWorkOrderDo.setApplyReason(KylinHttpResponseConstants.UPDATE_REAL_NAME);
        cloudWorkOrderDo.setCreateBy(cloudUserDo.getId());
        workOrderService.save(cloudWorkOrderDo);
        //修改账户工单详情表
        CloudWorkOrderUserDo workOrderUserDo = new CloudWorkOrderUserDo();
        workOrderUserDo.setOldRealName(oldRealName);
        workOrderUserDo.setNewRealName(updateRealNameParam.getRealName());
        workOrderUserDo.setWorkOrderId(cloudWorkOrderDo.getId());
        workOrderUserDo.setCreateTime(now);
        workOrderUserDo.setCreateBy(cloudUserDo.getId());
        workOrderUserService.save(workOrderUserDo);

    }

    @Override
    public WorkOrderUserDetailRespDto getWorkOrderUserDetailByWorkOrderId(Integer workOrderId) {
        WorkOrderUserDetailRespDto workOrderUserDetailRespDto = new WorkOrderUserDetailRespDto();

        selfWorkOrderService.formatBaseWorkOrderDetail(workOrderId, workOrderUserDetailRespDto);

        CloudUserDo cloudUserDo = cloudUserService.getById(workOrderUserDetailRespDto.getUserId());
        workOrderUserDetailRespDto.setUserName(cloudUserDo.getUserName());
        workOrderUserDetailRespDto.setRealName(cloudUserDo.getRealName());
        workOrderUserDetailRespDto.setMobile(cloudUserDo.getMobile());
        if (cloudUserDo.getOrganizationId() > 0) {
            workOrderUserDetailRespDto.setOrganizationName(cloudOrganizationService.getById(cloudUserDo
                    .getOrganizationId()).getOrganizationName());
        }
//        if (cloudUserDo.getDeptId() > 0) {
//            workOrderUserDetailRespDto.setDeptName(cloudDeptService.getById(cloudUserDo.getDeptId()).getDeptName());
//        }

        //修改账号，需要查询旧真实姓名，及新真实姓名
        if (Objects.equals(workOrderUserDetailRespDto.getWorkOrderType(), WorkOrderType.MODIFY_USER)) {
            CloudWorkOrderUserDo queryDo = new CloudWorkOrderUserDo();
            queryDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderUserDo> wrapper = new QueryWrapper<>(queryDo);
            CloudWorkOrderUserDo cloudWorkOrderUserDo = workOrderUserService.getOne(wrapper);
            workOrderUserDetailRespDto.setNewRealName(cloudWorkOrderUserDo.getNewRealName());
        }
        return workOrderUserDetailRespDto;
    }
}
