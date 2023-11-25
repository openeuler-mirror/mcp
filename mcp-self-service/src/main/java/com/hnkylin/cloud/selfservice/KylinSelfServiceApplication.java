package com.hnkylin.cloud.selfservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hnkylin"})
@MapperScan({"com.hnkylin.cloud.core.mapper"})
public class KylinSelfServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KylinSelfServiceApplication.class, args);
    }
}
