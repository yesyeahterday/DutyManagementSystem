package com.luke.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private static long tokenExpiration = 360 * 60 * 60 * 1000L;
    private static SecretKey tokenSignKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());

    public static String createToken(String username,String password) {
        String token = Jwts.builder().
                setSubject("USER_INFO").
                setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).
                claim("username", username).
                claim("password", password).
                signWith(tokenSignKey).
                compact();
        return token;
    }
    public static Claims parseToken(String token){

            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenSignKey).build();
            return jwtParser.parseClaimsJws(token).getBody();
    }
    public static String getUserId(String token){
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenSignKey).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        String userId = claims.get("userId", String.class);
        String username = claims.get("username", String.class);
        return userId;
    }
    public static String getUsername(String token){
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenSignKey).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        String username = claims.get("username", String.class);
        return username;
    }
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(tokenSignKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 检查过期时间
            if (claims.getExpiration().before(new Date())) {
                System.out.println("Token has expired");
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(JwtUtil.createToken("12345", "admin"));

    }
}
