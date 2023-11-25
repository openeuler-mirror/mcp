package com.hnkylin.cloud.manage.entity.req.user;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

import java.util.List;

@Data
public class BatchDeleteUserParam {

    @FieldCheck(notNull = true, notNullMessage = "用户ID不能为空")
    List<Integer> userIds;


}
