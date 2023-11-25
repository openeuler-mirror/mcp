package com.hnkylin.cloud.selfservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties("auth")
public class AuthExcludeApiProperties {

    private List<String> authExcludeApis;

    public List<String> getAuthExcludeApis() {
        return authExcludeApis;
    }

    public void setAuthExcludeApis(List<String> authExcludeApis) {
        this.authExcludeApis = authExcludeApis;
    }
}
