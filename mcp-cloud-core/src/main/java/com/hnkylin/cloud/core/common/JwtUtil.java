package com.hnkylin.cloud.core.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * JWT工具类
 **/
public class JwtUtil {


    public final static String USER_NAME = "userName";

    public final static String USER_ID = "userId";


    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            // 根据密码生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withClaim(USER_NAME, username).build();
            // 效验TOKEN
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_NAME).asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户id
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_ID).asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成签名
     *
     * @param username 用户名
     * @param userId   用户id
     * @param secret   用户的密码
     * @return 加密的token
     */
    public static String sign(String username, String userId, String secret) {
        Date date = new Date(System.currentTimeMillis() + KylinCommonConstants.TOKEN_EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create().withClaim(USER_NAME, username).withClaim(USER_ID, userId).
                withClaim("create_time", System.currentTimeMillis()).withExpiresAt(date)
                .sign(algorithm);

    }


}
