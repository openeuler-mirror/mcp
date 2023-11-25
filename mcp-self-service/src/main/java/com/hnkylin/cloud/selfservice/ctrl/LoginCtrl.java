package com.hnkylin.cloud.selfservice.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.selfservice.entity.LoginUserVo;
import com.hnkylin.cloud.selfservice.entity.req.LoginUserParam;
import com.hnkylin.cloud.selfservice.entity.resp.TokenRespDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/")
@Slf4j
public class LoginCtrl {


    @Resource
    private SelfServiceUserService selfServiceUserService;

    /**
     * 用户登录
     *
     * @param loginUserParam 用户参数
     * @return
     */
    @ParamCheck
    @PostMapping("/login")
    public BaseResult<TokenRespDto> login(@ModelCheck(notNull = true) @RequestBody LoginUserParam loginUserParam) {

        return BaseResult.success(selfServiceUserService.login(loginUserParam));
    }

    @PostMapping("/loginOut")
    public BaseResult<TokenRespDto> loginOut(@LoginUser LoginUserVo loginUserVo) {
        selfServiceUserService.loginOut(loginUserVo);
        return BaseResult.success(null);
    }
}
