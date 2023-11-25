package com.hnkylin.cloud.selfservice.service;

import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.LoginUserParam;
import com.hnkylin.cloud.selfservice.entity.req.UpdateRealNameParam;
import com.hnkylin.cloud.selfservice.entity.req.UpdatePwdParam;
import com.hnkylin.cloud.selfservice.entity.req.UserRegisterParam;
import com.hnkylin.cloud.selfservice.entity.resp.WorkOrderUserDetailRespDto;
import com.hnkylin.cloud.selfservice.entity.resp.TokenRespDto;
import com.hnkylin.cloud.selfservice.entity.resp.UserInfoRespDto;

public interface SelfServiceUserService {

    /**
     * 注册
     */
    void register(UserRegisterParam userRegisterParam);

    /**
     * 登录
     */
    TokenRespDto login(LoginUserParam loginUserParam);

    /**
     * 修改密码
     */
    void updatePwd(UpdatePwdParam updatePwdParam, LoginUserVo loginUserVo);

    /**
     * 用户信息
     */
    UserInfoRespDto userInfo(LoginUserVo loginUserVo);

    /**
     * 登出
     */
    void loginOut(LoginUserVo loginUserVo);

    /**
     * 修改真实姓名
     */
    void updateRealName(UpdateRealNameParam updateRealNameParam, LoginUserVo loginUserVo);

    /**
     * 根据工单ID获取注册申请/修改账号 工单详情
     */
    WorkOrderUserDetailRespDto getWorkOrderUserDetailByWorkOrderId(Integer workOrderId);

}
