package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.user.*;
import com.hnkylin.cloud.manage.entity.resp.user.ModifyUserInfoRespDto;
import com.hnkylin.cloud.manage.entity.resp.user.PageUserRespDto;
import com.hnkylin.cloud.manage.entity.resp.user.UserInfoRespDto;
import com.hnkylin.cloud.manage.service.UserService;
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
    private UserService userService;


    @PostMapping("/userInfo")
    public BaseResult<UserInfoRespDto> userInfo(@LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(userService.userInfo(loginUserVo));

    }


    @PostMapping("/resetPassword")
    @ParamCheck
    public BaseResult<String> resetPassword(@ModelCheck(notNull = true) @RequestBody ResetPasswordParam resetPasswordParam, @LoginUser LoginUserVo loginUserVo) {
        userService.resetPassword(loginUserVo, resetPasswordParam);
        return BaseResult.success("success");

    }

    @PostMapping("/createUser")
    @ParamCheck
    public BaseResult<String> createUser(@ModelCheck(notNull = true) @RequestBody CreateUserParam createUserParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        userService.createUser(createUserParam, loginUserVo);
        return BaseResult.success("success");

    }

    @PostMapping("/modifyUser")
    @ParamCheck
    public BaseResult<String> modifyUser(@ModelCheck(notNull = true) @RequestBody ModifyUserParam modifyUserParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        userService.modifyUser(modifyUserParam, loginUserVo);
        return BaseResult.success("success");

    }


    @PostMapping("/modifyUserInfo")
    @ParamCheck
    public BaseResult<ModifyUserInfoRespDto> modifyUserInfo(@ModelCheck(notNull = true) @RequestBody BaseUserParam baseUserParam) {
        return BaseResult.success(userService.modifyUserInfo(baseUserParam));

    }

    @PostMapping("/deleteUser")
    @ParamCheck
    public BaseResult<String> deleteUser(@ModelCheck(notNull = true) @RequestBody BaseUserParam baseUserParam,
                                         @LoginUser LoginUserVo loginUserVo) {
        userService.deleteUser(baseUserParam, loginUserVo);
        return BaseResult.success("success");

    }

    @PostMapping("/batchDeleteUser")
    @ParamCheck
    public BaseResult<String> batchDeleteUser(@ModelCheck(notNull = true) @RequestBody BatchDeleteUserParam batchDeleteUserParam,
                                              @LoginUser LoginUserVo loginUserVo) {
        userService.batchDeleteUser(batchDeleteUserParam, loginUserVo);
        return BaseResult.success("success");

    }


    @PostMapping("/pageUser")
    @ParamCheck
    public BaseResult<PageData<PageUserRespDto>> pageUser(@ModelCheck(notNull = true) @RequestBody
                                                                  PageUserParam pageUserParam,
                                                          @LoginUser LoginUserVo loginUserVo) {

        return BaseResult.success(userService.pageUser(pageUserParam, loginUserVo));

    }


}
