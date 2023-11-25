package com.hnkylin.cloud.core.common;

import org.springframework.http.HttpHeaders;

public class McUtils {

    public static HttpHeaders createMcHeaders(String userName) {
        Long current = System.currentTimeMillis();
        String authToken = SHAUtil.getSHA256(current + MCServerVmConstants.TOKEN_SECRET);
        HttpHeaders headers = new HttpHeaders();
        headers.add(MCServerVmConstants.TIMESTAMP, current.toString());
        headers.add(MCServerVmConstants.USER_NAME, userName);
        headers.add(MCServerVmConstants.AUTH_TOKEN, authToken);
        //headers.add("Content-Type","application/x-www-form-urlencoded");
        return headers;
    }
}
