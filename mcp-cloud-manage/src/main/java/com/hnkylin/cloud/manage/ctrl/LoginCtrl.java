package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.workorder.LoginUserParam;
import com.hnkylin.cloud.manage.entity.resp.TokenRespDto;
import com.hnkylin.cloud.manage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@RestController
@RequestMapping("/api/")
@Slf4j
public class LoginCtrl {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginUserParam 用户参数
     * @return
     */
    @ParamCheck
    @PostMapping("/login")
    public BaseResult<TokenRespDto> login(@ModelCheck(notNull = true) @RequestBody LoginUserParam loginUserParam) {

        return BaseResult.success(userService.login(loginUserParam));
    }

    @PostMapping("/loginOut")
    public BaseResult<TokenRespDto> loginOut(@LoginUser LoginUserVo loginUserVo) {
        userService.loginOut(loginUserVo);
        return BaseResult.success(null);
    }
}
