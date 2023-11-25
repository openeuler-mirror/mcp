package com.hnkylin.cloud.selfservice.ctrl;

import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.selfservice.entity.resp.OrganizationTreeDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/org")
@Slf4j
public class OrganizationCtrl {

    @Resource
    private SelfServiceOrganizationService selfServiceOrganizationService;


    @PostMapping("/queryOrgList")
    public BaseResult<List<OrganizationTreeDto>> queryOrgList() {
        return BaseResult.success(selfServiceOrganizationService.queryOrganizationTree());

    }

}
