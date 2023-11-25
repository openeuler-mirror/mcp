package com.hnkylin.cloud.selfservice.ctrl;

import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.selfservice.entity.resp.DeptRespDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/dept")
@Slf4j
public class DeptCtrl {


    @Resource
    private SelfServiceDeptService selfServiceDeptService;


    @PostMapping("/queryDeptList")
    public BaseResult<List<DeptRespDto>> queryDeptList() {
        return BaseResult.success(selfServiceDeptService.queryDeptList());

    }
}
