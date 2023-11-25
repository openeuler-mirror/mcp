package com.hnkylin.cloud.manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.hnkylin"})
@MapperScan({"com.hnkylin.cloud.core.mapper", "com.hnkylin.cloud.manage.mapper"})
public class KylinCloudManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(KylinCloudManageApplication.class, args);
    }
}
