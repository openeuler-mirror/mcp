package com.hnkylin.cloud.selfservice.service.impl;

import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.domain.CloudOrganizationDo;
import com.hnkylin.cloud.core.service.CloudOrganizationService;
import com.hnkylin.cloud.selfservice.entity.resp.OrganizationTreeDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceOrganizationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SelfOrganizationServiceImpl implements SelfServiceOrganizationService {

    @Resource
    private CloudOrganizationService cloudOrganizationService;


    @Override
    public List<OrganizationTreeDto> queryOrganizationTree() {
        ArrayList<OrganizationTreeDto> list = new ArrayList<>();
        List<CloudOrganizationDo> orgList = cloudOrganizationService.queryAllOrgList();

        CloudOrganizationDo defaultTopOrg = cloudOrganizationService.getDefaultTopOrg();

        for (CloudOrganizationDo orgDo : orgList) {
            if (Objects.equals(orgDo.getParentId(), defaultTopOrg.getId())) {
                list.add(createOrgTreeDto(orgDo, orgList));
            }
        }
        return list;
    }


    /**
     * 递归查找子菜单
     */
    private List<OrganizationTreeDto> getChild(Integer parentId, List<CloudOrganizationDo> allOrgList) {
        List<OrganizationTreeDto> childList = new ArrayList<>();
        for (CloudOrganizationDo organizationDo : allOrgList) {
            if (Objects.equals(parentId, organizationDo.getParentId())) {
                OrganizationTreeDto organizationDto = createOrgTreeDto(organizationDo, allOrgList);
                childList.add(organizationDto);
            }
        }
        return childList;
    }


    private OrganizationTreeDto createOrgTreeDto(CloudOrganizationDo cloudOrganizationDo, List<CloudOrganizationDo>
            allOrgList) {
        OrganizationTreeDto organizationDto = new OrganizationTreeDto();
        organizationDto.setOrganizationId(cloudOrganizationDo.getId());
        organizationDto.setOrganizationName(cloudOrganizationDo.getOrganizationName());
        organizationDto.setParentId(cloudOrganizationDo.getParentId());
        organizationDto.setChildren(getChild(cloudOrganizationDo.getId(), allOrgList));
        return organizationDto;
    }
}
