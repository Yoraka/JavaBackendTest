package com.sdj.sdjtest.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTUtil {
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // JWT 的密钥
    private static final long EXPIRATION_TIME = 3600_000; // JWT 的过期时间，单位为毫秒

    
    // 验证 JWT
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // 从 JWT 中获取用户 ID
    public static int getUserId(String token) {
        System.out.println(KEY);
        System.out.println(token);
        try{
            Claims claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody();
            System.out.println(Integer.parseInt(claims.getSubject()));
            return Integer.parseInt(claims.getSubject());
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean verify(String token) {
        try {
            Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // 生成 JWT
    // 生成 JWT Token
    public static String sign(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        System.out.println("JWTUtil.sign: userId = " + String.valueOf(userId));
        System.out.println("JWTUtil.sign: Key = " + KEY);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(KEY)
                .compact();
    }
}
