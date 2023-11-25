package com.hnkylin.cloud.selfservice.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.UpdateRealNameParam;
import com.hnkylin.cloud.selfservice.entity.req.UpdatePwdParam;
import com.hnkylin.cloud.selfservice.entity.req.UserRegisterParam;
import com.hnkylin.cloud.selfservice.entity.resp.UserInfoRespDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserCtrl {


    @Resource
    private SelfServiceUserService selfServiceUserService;

    /**
     * 用户注册
     *
     * @param userRegisterParam 注册参数
     * @return
     */
    @ParamCheck
    @PostMapping("/register")
    public BaseResult<String> register(@ModelCheck(notNull = true) @RequestBody UserRegisterParam userRegisterParam) {
        selfServiceUserService.register(userRegisterParam);
        return BaseResult.success(null);

    }

    @ParamCheck
    @PostMapping("/updatePwd")
    public BaseResult<String> updatePwd(@ModelCheck(notNull = true) @RequestBody UpdatePwdParam updatePwdParam,
                                        @LoginUser LoginUserVo loginUserVo) {
        selfServiceUserService.updatePwd(updatePwdParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/userInfo")
    public BaseResult<UserInfoRespDto> userInfo(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(selfServiceUserService.userInfo(loginUserVo));

    }

    @ParamCheck
    @PostMapping("/updateRealName")
    public BaseResult<String> updateRealName(@ModelCheck(notNull = true) @RequestBody UpdateRealNameParam
                                                     updateRealNameParam, @LoginUser LoginUserVo loginUserVo) {
        selfServiceUserService.updateRealName(updateRealNameParam, loginUserVo);
        return BaseResult.success(null);

    }
}
