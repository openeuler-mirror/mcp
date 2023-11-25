package com.hnkylin.cloud.manage.ctrl;

import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.annotation.ParamCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.NetworkConfigResp;
import com.hnkylin.cloud.manage.entity.mc.resp.NetworkPortGroupResp;
import com.hnkylin.cloud.manage.entity.req.network.BaseNetworkParam;
import com.hnkylin.cloud.manage.entity.req.network.CreateNetworkParam;
import com.hnkylin.cloud.manage.entity.req.network.ModifyNetworkParam;
import com.hnkylin.cloud.manage.entity.req.vdc.BaseVdcParam;
import com.hnkylin.cloud.manage.entity.req.workorder.QueryPortGroupParam;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkConfigRespDto;
import com.hnkylin.cloud.manage.service.NetworkConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/networkConfig")
@Slf4j
public class NetworkConfigCtrl {


    @Resource
    private NetworkConfigService networkConfigService;


    @PostMapping("/pageNetworkConfig")
    public BaseResult<PageData<NetworkConfigRespDto>> pageNetworkConfig(@RequestBody BasePageParam basePageParam,
                                                                        @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(networkConfigService.pageNetworkConfig(basePageParam, loginUserVo));

    }

    @PostMapping("/listNetworkListByVdcId")
    public BaseResult<List<NetworkConfigRespDto>> listNetworkListByVdcId(@RequestBody BaseVdcParam baseVdcParam) {
        return BaseResult.success(networkConfigService.listNetworkListByVdcId(baseVdcParam.getVdcId()));

    }


    @PostMapping("/createNetwork")
    @ParamCheck
    public BaseResult<String> createNetwork(@ModelCheck(notNull = true) @RequestBody CreateNetworkParam createNetworkParam,
                                            @LoginUser LoginUserVo loginUserVo) {
        networkConfigService.createNetwork(createNetworkParam, loginUserVo);
        return BaseResult.success(null);

    }

    @PostMapping("/networkDetail")
    @ParamCheck
    public BaseResult<NetworkConfigRespDto> networkDetail(@ModelCheck(notNull = true) @RequestBody BaseNetworkParam baseNetworkParam,
                                                          @LoginUser LoginUserVo loginUserVo) {
        return BaseResult.success(networkConfigService.networkDetail(baseNetworkParam.getNetworkId()));

    }

    @PostMapping("/modifyNetwork")
    @ParamCheck
    public BaseResult<String> modifyNetwork(@ModelCheck(notNull = true) @RequestBody ModifyNetworkParam modifyNetworkParam,
                                            @LoginUser LoginUserVo loginUserVo) {
        networkConfigService.modifyNetwork(modifyNetworkParam, loginUserVo);
        return BaseResult.success(null);

    }


}
