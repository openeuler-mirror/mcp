package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.KcpHaNodeDo;
import com.hnkylin.cloud.core.enums.KcpHaNodeRole;

public interface CloudHaNodeService extends IService<KcpHaNodeDo> {

    /**
     * 查询备节点KCP
     *
     * @return
     */
    KcpHaNodeDo getKcpNodeByNodeType(KcpHaNodeRole kcpHaNodeRole);


}
