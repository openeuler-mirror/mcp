package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.KcpHaNodeDo;
import com.hnkylin.cloud.core.enums.KcpHaNodeRole;
import com.hnkylin.cloud.core.mapper.CloudHaNodeMapper;
import com.hnkylin.cloud.core.service.CloudHaNodeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CloudHaNodeServiceImpl extends ServiceImpl<CloudHaNodeMapper, KcpHaNodeDo>
        implements CloudHaNodeService {


    @Override
    public KcpHaNodeDo getKcpNodeByNodeType(KcpHaNodeRole kcpHaNodeRole) {
        KcpHaNodeDo kcpHaNodeDo = new KcpHaNodeDo();
        kcpHaNodeDo.setDeleteFlag(false);
        kcpHaNodeDo.setNodeType(kcpHaNodeRole.name());
        Wrapper<KcpHaNodeDo> wrapper = new QueryWrapper<>(kcpHaNodeDo);
        List<KcpHaNodeDo> kcoNodeList = baseMapper.selectList(wrapper);
        return kcoNodeList.isEmpty() ? null : kcoNodeList.get(0);
    }
}
