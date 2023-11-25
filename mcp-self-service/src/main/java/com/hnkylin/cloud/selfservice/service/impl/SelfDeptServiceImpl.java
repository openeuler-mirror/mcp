package com.hnkylin.cloud.selfservice.service.impl;

import com.hnkylin.cloud.core.domain.CloudDeptDo;
import com.hnkylin.cloud.core.service.CloudDeptService;
import com.hnkylin.cloud.selfservice.entity.resp.DeptRespDto;
import com.hnkylin.cloud.selfservice.service.SelfServiceDeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SelfDeptServiceImpl implements SelfServiceDeptService {

    @Resource
    private CloudDeptService cloudDeptService;

    @Override
    public List<DeptRespDto> queryDeptList() {
        ArrayList<DeptRespDto> list = new ArrayList<>();
        List<CloudDeptDo> deptDoList = cloudDeptService.queryAllDeptList();
        for (CloudDeptDo deptDo : deptDoList) {
            if (Objects.equals(deptDo.getParentId(), 0)) {
                list.add(createDeptRespDto(deptDo, deptDoList));
            }
        }
        return list;
    }


    /**
     * 递归查找子菜单
     */
    private List<DeptRespDto> getChild(Integer parentId, List<CloudDeptDo> allDeptList) {
        List<DeptRespDto> childList = new ArrayList<>();
        for (CloudDeptDo deptDo : allDeptList) {
            if (Objects.equals(parentId, deptDo.getParentId())) {
                DeptRespDto deptRespDto = createDeptRespDto(deptDo, allDeptList);
                childList.add(deptRespDto);
            }
        }
        return childList;
    }


    private DeptRespDto createDeptRespDto(CloudDeptDo cloudDeptDo, List<CloudDeptDo> allDeptList) {
        DeptRespDto deptRespDto = new DeptRespDto();
        deptRespDto.setDeptId(cloudDeptDo.getId());
        deptRespDto.setDeptName(cloudDeptDo.getDeptName());
        deptRespDto.setParentId(cloudDeptDo.getParentId());
        deptRespDto.setChildDepts(getChild(cloudDeptDo.getId(), allDeptList));
        return deptRespDto;
    }
}
