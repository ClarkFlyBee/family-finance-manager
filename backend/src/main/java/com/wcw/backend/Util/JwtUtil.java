package com.wcw.backend.Util;

import com.wcw.backend.Common.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

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

    /**
     * 从请求头中提取 Token
     */
    public static String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 从 HttpServletRequest 中解析出用户 ID
     * @return 用户 ID
     * @throws BizException 如果未登录或 Token 无效
     */
    public static Long getUserId(HttpServletRequest request){
        String token = extractToken(request);
        if (token == null){
            throw new BizException(401, "未提供登录凭证，请重新登录");
        }

        try{
            Claims claims = JwtUtil.parse(token);
            Long userId = claims.get("uid", Long.class);
            if (userId == null){
                throw new BizException(401, "Token 中不包含用户信息");
            }
            return userId;
        } catch (MalformedJwtException | SignatureException e){
            throw new BizException(401, "无效的 Token");
        } catch (ExpiredJwtException e){
            throw new BizException(401, "登录已过期，请重新登录");
        } catch (Exception e){
            throw new BizException(500, "Token 解析失败：" + e.getMessage());
        }
    }

    public static Long getFamilyId(HttpServletRequest request){
        String token = extractToken(request);
        if (token == null){
            throw new BizException(401, "未提供登录凭证，请重新登录");
        }

        try{
            Claims claims = JwtUtil.parse(token);
            Long familyId = claims.get("fid", Long.class);
            if (familyId == null){
                throw new BizException(401, "Token 中不包含家庭信息");
            }
            return familyId;
        } catch (Exception e){
            throw new BizException(500, "Token 解析失败：" +  e.getMessage());
        }
    }
}
