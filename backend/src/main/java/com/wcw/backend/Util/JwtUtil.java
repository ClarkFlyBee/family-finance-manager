package com.wcw.backend.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    // 实际生产中，一定不要把 KEY 写在源码里
    private static final SecretKey KEY = Keys.hmacShaKeyFor("familyFinanceManagerSecretKey2025@#$".getBytes(StandardCharsets.UTF_8));
    private static final long EXP = 86400000;

    public static String createToken(Long userId, Long familyId){
        return Jwts.builder()
                .claim("uid", userId)
                .claim("fid", familyId)
                .expiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(KEY)
                .compact();
    }

    public static Claims parse(String token){
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
