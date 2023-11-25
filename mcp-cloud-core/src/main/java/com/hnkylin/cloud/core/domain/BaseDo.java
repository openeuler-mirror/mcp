package com.hnkylin.cloud.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
public class BaseDo {


    @TableId(type = IdType.AUTO)
    private Integer id;

    //创建时间
    private Date createTime;

    //创建者
    private Integer createBy;

    //更新时间
    private Date updateTime;

    //更新者
    private Integer updateBy;


    //删除者
    private Integer deleteBy;

    //删除标志
    private Boolean deleteFlag;

    //删除时间
    private Date deleteTime;
}