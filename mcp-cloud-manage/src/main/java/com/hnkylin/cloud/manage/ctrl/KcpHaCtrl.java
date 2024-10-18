package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.entity.req.kcpha.AddSlaveKcpParam;
import com.hnkylin.cloud.core.entity.req.kcpha.ChangeKcpRoleParam;
import com.hnkylin.cloud.core.entity.resp.kcpha.AddSlaveResp;
import com.hnkylin.cloud.core.entity.resp.kcpha.KcpHaResp;
import com.hnkylin.cloud.core.entity.resp.kcpha.KcpNodeResp;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.service.KcpHaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/ha")
@Slf4j
public class KcpHaCtrl {

    @Resource
    private KcpHaService kcpHaService;

    @PostMapping("/slaveKcpInfo")
    public BaseResult<KcpHaResp> getSlaveKcpInfo() {

        return BaseResult.success(kcpHaService.getSlaveKcpInfo());

    }

    @PostMapping("/initSlave")
    public BaseResult<String> initSlave(@RequestBody ChangeKcpRoleParam addSlaveKcpParam) {
        kcpHaService.initSlave(addSlaveKcpParam);
        return BaseResult.success(null);

    }

    @PostMapping("/checkNameAndPassword")
    public BaseResult<AddSlaveResp> checkNameAndPassword(@RequestBody AddSlaveKcpParam addSlaveKcpParam) {

        return BaseResult.success(kcpHaService.checkNameAndPassword(addSlaveKcpParam));

    }

    @PostMapping("/resetSlave")
    public BaseResult<String> resetSlave() {
        kcpHaService.resetSlave();
        return BaseResult.success(null);

    }

    @PostMapping("/addSlave")
    @ParamCheck
    public BaseResult<String> addSlave(@ModelCheck(notNull = true) @RequestBody AddSlaveKcpParam addSlaveKcpParam,
                                       @LoginUser com.hnkylin.cloud.manage.entity.LoginUserVo loginUserVo) {
        kcpHaService.addSlave(addSlaveKcpParam, loginUserVo);
        return BaseResult.success(null);
    }


    @PostMapping("/nodeList")
    public BaseResult<List<KcpNodeResp>> nodeList() {

        return BaseResult.success(kcpHaService.nodeList());
    }


    @PostMapping("/changeToMaster")
    public BaseResult<String> changeToMaster() {
        kcpHaService.changeToMaster();
        return BaseResult.success(null);
    }

    @PostMapping("/deleteSlave")
    public BaseResult<String> deleteSlave(@LoginUser LoginUserVo loginUserVo) {
        kcpHaService.deleteSlave(loginUserVo);
        return BaseResult.success(null);
    }


}
