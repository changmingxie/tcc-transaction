package org.mengyun.tcctransaction.dashboard.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mengyun.tcctransaction.dashboard.model.SystemUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author huabao.fang
 * @Date 2022/6/6 15:05
 **/
public class JwtUtil {

    private static final String JWT_SECRET = "tcc-transaction";

    // tocken存活时长 单位为秒
    private static final Long JWT_LIVE_DURATION = 2592000L;// 30 天


    public static String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", username);
        claims.put("createTime", new Date());
        return generateToken(claims);
    }

    private static String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_LIVE_DURATION * 1000);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
    }

    public static String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 判断token是否合法
     *
     * @param token
     * @param userDetails
     * @return
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {
        SystemUser user = (SystemUser) userDetails;
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 判断token是否过期
     *
     * @param token
     * @return
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
