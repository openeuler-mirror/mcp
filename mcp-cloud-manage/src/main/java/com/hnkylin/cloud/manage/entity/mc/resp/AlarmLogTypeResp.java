package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.util.List;

@Data
public class AlarmLogTypeResp {

    private List<McFilterTypeDto> level;

    private List<McFilterTypeDto> type;

    private List<McFilterTypeDto> targets;


}
